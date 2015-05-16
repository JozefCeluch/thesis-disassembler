package com.thesis.translator;

import com.thesis.block.MethodBlock;
import com.thesis.exception.DecompilerException;
import com.thesis.exception.DecompilerRuntimeException;
import com.thesis.expression.JumpExpression;
import com.thesis.expression.TryCatchExpression;
import com.thesis.statement.Statement;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.VariableDeclarationExpression;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.handler.*;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator implements MethodState.OnLabelChangeListener {
	private static final Logger LOG = Logger.getLogger(InstructionTranslator.class);

	private final MethodNode mMethod;
	private MethodBlock mMethodBlock;
	private Map<Integer, NodeHandler> mHandlers;

	private MethodState mState;
	private TryCatchManager mTryCatchManager;


	public InstructionTranslator(MethodBlock methodBlock) {
		mState = new MethodState();
		mMethodBlock = methodBlock;
		mMethod = methodBlock.getMethodNode();
		mState.setLocalVariables(prepareLocalVariables(mMethod.localVariables, mMethodBlock.getArguments()));
		mState.setOnLabelChangeListener(this);
		mTryCatchManager = TryCatchManager.newInstance(mMethod.tryCatchBlocks, mState.getFinalStack());
		prepareHandlers();
	}

	public List<Statement> addCode() {
		LOG.debug("METHOD: " + mMethod.name);

		mState.getFinalStack().addEnhancer(new LoopEnhancer());
		mState.setCurrentNode(mMethod.instructions.getFirst());
		while (mState.getCurrentNode() != null) {
			processNode(mState.getCurrentNode());
			mState.moveNode();
		}

		mState.getFinalStack().enhance();

		StatementCreator sc = new StatementCreator(mState.getFinalStack(), mMethodBlock);

		List<Statement> statements = getLocalVariableAssignments();
		statements.addAll(sc.getStatements());
		return statements;
	}

	private Map<Integer, LocalVariable> prepareLocalVariables(List localVariables, Map<Integer, LocalVariable> args) {
		Map<Integer, LocalVariable> localVarMap = new HashMap<>();

		if (localVariables.size() > 0) {
			for (Object var : localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				localVarMap.put(variable.index, new LocalVariable(variable));
			}
		}
		localVarMap.putAll(args);
		return localVarMap;
	}

	private void prepareHandlers() {
		mHandlers = new HashMap<>();

		NodeHandler.OnNodeMovedListener nodeMoveListener = () -> processNode(mState.getCurrentNode());
		mHandlers.put(AbstractInsnNode.INSN, new InsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.INT_INSN, new IntInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.VAR_INSN, new VarInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.TYPE_INSN, new TypeInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.FIELD_INSN, new FieldInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.METHOD_INSN, new MethodInsnNodeHandler(mState, mMethod.name, mMethodBlock.getClassType()));
		mHandlers.put(AbstractInsnNode.INVOKE_DYNAMIC_INSN, new InvokeDynamicInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.JUMP_INSN, new JumpInsnNodeHandler(mState, nodeMoveListener));
		mHandlers.put(AbstractInsnNode.LABEL, new LabelNodeHandler(mState, mMethod.tryCatchBlocks, nodeMoveListener));
		mHandlers.put(AbstractInsnNode.LDC_INSN, new LdcInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.IINC_INSN, new IincInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.MULTIANEWARRAY_INSN, new MultiANewArrayInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.FRAME, new FrameNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.LINE, new LineNumberNodeHandler(mState));
		SwitchInsnNodeHandler switchHandler = new SwitchInsnNodeHandler(mState, nodeMoveListener);
		mHandlers.put(AbstractInsnNode.TABLESWITCH_INSN, switchHandler);
		mHandlers.put(AbstractInsnNode.LOOKUPSWITCH_INSN, switchHandler);
	}

	private void processNode(AbstractInsnNode node) {
		if (node == null) return;

		NodeHandler handler = mHandlers.get(node.getType());
		if (handler == null) {
			throw new DecompilerRuntimeException("No handler for this node type: " + node.getType());
		}
		handler.handle(node);
	}

	@Override
	public void onLabelChange(int newLabel) {
		createTryCatchBlocks(mState);
	}

	public void createTryCatchBlocks(MethodState state) {
		if (mTryCatchManager.isEmpty()) return;
		List<TryCatchManager.Item> tryCatchItems = mTryCatchManager.getItemsWithStartId(state.getCurrentLabel());
		if (tryCatchItems.isEmpty()) return;

		TryCatchExpression tryCatchExpression = null;
		for(TryCatchManager.Item item : tryCatchItems) {
			prepareTryCatchItem(state, item, tryCatchExpression);
			tryCatchExpression = new TryCatchExpression(item);
		}
		state.getActiveStack().push(tryCatchExpression);
	}

	private void prepareTryCatchItem(MethodState state, TryCatchManager.Item item, TryCatchExpression innerTryCatchBlock) {
		if (item.getCatchBlockCount() == item.getHandlerTypes().size()) return;

		// fill try block
		item.setTryStack(state.startNewStack());
		if (innerTryCatchBlock != null) {
			item.getTryStack().push(innerTryCatchBlock);
		}

		while (item.getEndId() != state.getCurrentLabel()) {
			if (state.moveNode() == null) break;
			processNode(mState.getCurrentNode());
		}
		state.finishStack();
		// ignore repeated finally blocks
		ExpressionStack repeatedFinallyCalls = state.startNewStack();
		while (!item.hasHandlerLabel(state.getCurrentLabel())) {
			if (state.moveNode() == null) break;
			processNode(mState.getCurrentNode());
		}

		int tryCatchBlockEnd = JumpExpression.NO_DESTINATION;
		if (repeatedFinallyCalls.peek() instanceof JumpExpression) {
			tryCatchBlockEnd = ((JumpExpression) repeatedFinallyCalls.peek()).getJumpDestination();
		}
		state.finishStack();
		// fill catch blocks
		for (int i = 0; i < item.getHandlerCount(); i++) {
			item.addCatchBlock(state.getCurrentLabel(), state.startNewStack());
			int currentBlockLabel = state.getCurrentLabel();

			while (state.getCurrentLabel() == currentBlockLabel || !(item.hasHandlerLabel(state.getCurrentLabel())
					|| mTryCatchManager.hasCatchHandlerEnd(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				if (state.moveNode() == null) break;
				processNode(mState.getCurrentNode());
			}
			state.finishStack();

			state.startNewStack();
			// ignore repeated finally blocks
			while (!(item.hasHandlerLabel(state.getCurrentLabel()) || state.getCurrentLabel() == tryCatchBlockEnd)) {
				if (state.moveNode() == null) break;
				processNode(mState.getCurrentNode());
			}
			state.finishStack();
		}
	}

	private List<Statement> getLocalVariableAssignments() {
		List<Statement> localVars = mState.getLocalVariables().values().stream()
				.filter(variable -> !variable.isArgument() && !variable.isAdded())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable), 0, mMethodBlock)) //todo variable line number
				.collect(Collectors.toList());
		List<Statement> result = new ArrayList<>();
		result.addAll(0, localVars);
		return result;
	}
}
