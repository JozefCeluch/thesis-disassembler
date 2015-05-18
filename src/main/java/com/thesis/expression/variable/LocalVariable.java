package com.thesis.expression.variable;

import com.thesis.common.DataType;
import com.thesis.common.SignatureVisitor;
import com.thesis.translator.ExpressionStack;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.ArrayList;
import java.util.List;

public class LocalVariable extends Variable {

	private int mIndex;
	private boolean mIsArgument;
	private List<Scope> mScopes = new ArrayList<>();

	public LocalVariable(int index) {
		super();
		this.mIndex = index;
		mPrintType = true;
	}

	public LocalVariable(LocalVariableNode variableNode) {
		super(variableNode.name, DataType.getTypeFromDesc(variableNode.desc));

		if (variableNode.signature != null && !variableNode.signature.isEmpty()) {
			SignatureVisitor visitor = new SignatureVisitor(Opcodes.ACC_PRIVATE);
			SignatureReader reader = new SignatureReader(variableNode.signature);
			reader.acceptType(visitor);
			mType = DataType.getTypeFromObject(visitor.getDeclaration());
		}
		mIndex = variableNode.index;
		mScopes.add(new Scope(variableNode.start.getLabel(), variableNode.end.getLabel()));
		mDebugType = true;
		mPrintType = true;
	}

	public LocalVariable(String name, DataType type, int index) {
		super(name, type);
		mIndex = index;
		mPrintType = true;
	}

	public int getIndex() {
		return mIndex;
	}

	public void setIndex(int index) {
		mIndex = index;
	}

	public void setIsArgument(boolean isArgument) {
		mIsArgument = isArgument;
		mPrintType = !isArgument;
	}

	public boolean isArgument() {
		return mIsArgument;
	}

	public List<Scope> getScopes() {
		return mScopes;
	}

	public void merge(LocalVariable other) {
		if (!this.equals(other)) {
			return;
		}
		mScopes.addAll(other.mScopes);
	}

	@Override
	public String toString() {
		return mName;
	}

	@Override
	public String write() {
		String variable = "";
		if (mPrintType) {
			variable += getType().print() + " ";
			mPrintType = false;
		}
		variable += mName;
		return variable;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LocalVariable that = (LocalVariable) o;

		if (mIndex != that.mIndex) return false;
		if (mName != null ? !mName.equals(that.mName) : that.mName != null) return false;
		return !(mType != null ? !mType.equals(that.mType) : that.mType != null);

	}

	@Override
	public int hashCode() {
		int result = mIndex;
		result = 31 * result + (mName != null ? mName.hashCode() : 0);
		result = 31 * result + (mType != null ? mType.hashCode() : 0);
		return result;
	}

	public static class Scope {
		public static final int UNDEFINED = -1;

		private Label mStart;
		private Label mEnd;

		public Scope(Label start, Label end) {
			mStart = start;
			mEnd = end;
		}

		public int getStartLabelId(ExpressionStack stack) {
			if (mStart == null) {
				return UNDEFINED;
			}
			return stack.getLabelId(mStart);
		}

		public int getEndLabelId(ExpressionStack stack) {
			if (mEnd == null) {
				return UNDEFINED;
			}
			return stack.getLabelId(mEnd);
		}
	}
}
