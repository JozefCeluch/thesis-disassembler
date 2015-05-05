package com.thesis.translator;

import com.thesis.block.MethodBlock;
import com.thesis.statement.Statement;
import com.thesis.exception.IncorrectNodeException;
import com.thesis.expression.VariableDeclarationExpression;
import com.thesis.expression.variable.LocalVariable;
import com.thesis.translator.handler.*;
import org.objectweb.asm.tree.*;

import java.util.*;
import java.util.stream.Collectors;

public class InstructionTranslator {

	private final MethodNode mMethod;
	private Map<Integer, LocalVariable> mLocalVariables;
	private MethodBlock mMethodBlock;
	private Map<Integer, NodeHandler> mHandlers;

	private MethodState mState;

	public InstructionTranslator(MethodBlock methodBlock) {
		mState = new MethodState();
		mMethodBlock = methodBlock;
		mMethod = methodBlock.getMethodNode();
		copyLocalVariables();
		prepareHandlers();
	}

	public List<Statement> addCode() {
		System.out.println(" ");
		System.out.println("METHOD: " + mMethod.name);

		mState.getFinalStack().addEnhancer(new LoopEnhancer());
		mState.setCurrentNode(mMethod.instructions.getFirst());
		while (mState.getCurrentNode() != null) {
			processNode(mState.getCurrentNode());
			mState.moveNode();
		}

		mState.getFinalStack().enhance();

		List<Statement> statements = getLocalVariableAssignments();
		StatementCreator sc = new StatementCreator(mState.getFinalStack());
		statements.addAll(sc.getStatements());
		return statements;
	}

	private void copyLocalVariables() {
		mLocalVariables = new HashMap<>();

		if (mMethod.localVariables.size() > 0) {
			for (Object var : mMethod.localVariables) {
				LocalVariableNode variable = (LocalVariableNode) var;
				mLocalVariables.put(variable.index, new LocalVariable(variable));
			}
		}
		mLocalVariables.putAll(mMethodBlock.getArguments());

	}

	private void prepareHandlers() {
		mHandlers = new HashMap<>();

		NodeHandler.OnNodeMoveListener nodeMoveListener = () -> processNode(mState.getCurrentNode());
		mHandlers.put(AbstractInsnNode.INSN, new InsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.INT_INSN, new IntInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.VAR_INSN, new VarInsnNodeHandler(mState, mLocalVariables));
		mHandlers.put(AbstractInsnNode.TYPE_INSN, new TypeInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.FIELD_INSN, new FieldInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.METHOD_INSN, new MethodInsnNodeHandler(mState, mMethod.name, mMethodBlock.getClassType()));
		mHandlers.put(AbstractInsnNode.INVOKE_DYNAMIC_INSN, new InvokeDynamicInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.JUMP_INSN, new JumpInsnNodeHandler(mState, nodeMoveListener));
		mHandlers.put(AbstractInsnNode.LABEL, new LabelNodeHandler(mState, mMethod.tryCatchBlocks, nodeMoveListener));
		mHandlers.put(AbstractInsnNode.LDC_INSN, new LdcInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.IINC_INSN, new IincInsnNodeHandler(mState, mLocalVariables));
		mHandlers.put(AbstractInsnNode.MULTIANEWARRAY_INSN, new MultiANewArrayInsnNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.FRAME, new FrameNodeHandler(mState));
		mHandlers.put(AbstractInsnNode.LINE, new LineNumberNodeHandler(mState));
		SwitchInsnNodeHandler switchHandler = new SwitchInsnNodeHandler(mState, nodeMoveListener);
		mHandlers.put(AbstractInsnNode.TABLESWITCH_INSN, switchHandler);
		mHandlers.put(AbstractInsnNode.LOOKUPSWITCH_INSN, switchHandler);
	}

	private void processNode(AbstractInsnNode node) {
		NodeHandler handler = mHandlers.get(node.getType());
		if (handler != null) {
			try {
				handler.handle(node);
			} catch (IncorrectNodeException e) {
				e.printStackTrace(); //TODO
			}
		} else {
			AbstractHandler.printNodeInfo(node, mState);
		}
	}

	private List<Statement> getLocalVariableAssignments() {
		List<Statement> localVars = mLocalVariables.values().stream()
				.filter(variable -> !variable.isArgument() && !variable.isAdded())
				.map(variable -> new Statement(new VariableDeclarationExpression(variable), 0)) //todo variable line number
				.collect(Collectors.toList());
		List<Statement> result = new ArrayList<>();
		result.addAll(0, localVars);
		return result;
	}
}
