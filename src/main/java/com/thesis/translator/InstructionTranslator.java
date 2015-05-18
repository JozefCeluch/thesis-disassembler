package com.thesis.translator;

import com.thesis.block.MethodBlock;
import com.thesis.exception.DecompilerRuntimeException;
import com.thesis.expression.VariableDeclarationExpression;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.statement.Statement;
import com.thesis.translator.handler.*;
import org.apache.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructionTranslator {
	private static final Logger LOG = Logger.getLogger(InstructionTranslator.class);

	private final MethodNode mMethod;
	private MethodBlock mMethodBlock;
	private Map<Integer, NodeHandler> mHandlers;

	private MethodState mState;

	public InstructionTranslator(MethodBlock methodBlock) {
		mState = new MethodState();
		mMethodBlock = methodBlock;
		mMethod = methodBlock.getMethodNode();
		prepareLocalVariables(mMethod.localVariables, mMethodBlock.getArguments());
		mState.setupTryCatchManager(mMethod.tryCatchBlocks);
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

	private void prepareLocalVariables(List localVariables, Map<Integer, LocalVariable> args) {
		for (int position : args.keySet()) {
			mState.addLocalVariable(position, args.get(position));
		}

		if (localVariables.size() > 0) {
			for (Object var : localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mState.addLocalVariable(variable.index, new LocalVariable(variable));
			}
		}
	}

	private List<Statement> getLocalVariableAssignments() {
		List<Statement> localVars = new ArrayList<>();
		for (List<LocalVariable> variableList : mState.getLocalVariables().values()) {
			if (variableList.size() == 1) {
				LocalVariable variable = variableList.get(0);
				if (variable.getScopes().size() > 1 && !variable.isArgument()) {
					localVars.add(new Statement(new VariableDeclarationExpression(variable), 0, mMethodBlock));
				}
			}
		}

		List<Statement> result = new ArrayList<>();
		result.addAll(0, localVars);
		return result;
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
		mHandlers.put(AbstractInsnNode.LABEL, new LabelNodeHandler(mState, nodeMoveListener));
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
}
