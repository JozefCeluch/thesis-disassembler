package com.thesis.block;

import com.thesis.common.*;
import com.thesis.exception.DecompilerException;
import com.thesis.expression.PrimaryExpression;
import com.thesis.file.Disassembler;
import com.thesis.statement.Statement;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Representation of a class
 *
 * Drives the decompilation process and stores the results
 */
public class ClassBlock extends Block {

	private ClassNode mClassNode;

	/**
	 * Class level annotations
	 */
	private List<Object> mAnnotations;

	/**
	 * Class access modifiers
	 */
	private String mAccessFlags;

	/**
	 * Type of the class
	 */
	private DataType mClassType;

	/**
	 * Superclasses
	 */
	private String mExtends = "";

	/**
	 * Superinterfaces
	 */
	private String mImplements = "";

	/**
	 * Bytecode in ASM format
	 */
	private String mBytecode;

	/**
	 * Constructor
	 * @param classNode instance of ASM ClassNode that represents a class
	 * @param parent enclosing class, non-null in case of innner class
	 */
	public ClassBlock(ClassNode classNode, Block parent) {
		super(parent);
		mClassNode = classNode;
		mParent = parent;
	}

	@Override
	public Block disassemble() {
		storeInnerClassesNames(mClassNode.innerClasses);

		mAnnotations = getSingleLineAnnotations(mClassNode.visibleAnnotations, mClassNode.invisibleAnnotations);
		mAccessFlags = getAccessFlags();
		mClassType = DataType.getTypeFromObject(mClassNode.name);

		String genericDeclaration = null;
		if (mClassNode.signature != null) {
			SignatureVisitor sv = new SignatureVisitor(0);
			SignatureReader r = new SignatureReader(mClassNode.signature);
			r.accept(sv);
			genericDeclaration = sv.getDeclaration();
		}
		if (genericDeclaration != null){
			mExtends = genericDeclaration;
		} else {
			mExtends = getSuperClass(mClassNode.superName);
			if (isClass()) {
				mImplements = getInterfaces(mClassNode.interfaces);
			}
		}

		appendFields(mClassNode.fields);
		appendMethods(mClassNode.methods);
		appendInnerClasses(mClassNode.innerClasses);

		return this;
	}

	/**
	 * Textual representation of class file
	 * @param bytecode disassembled bytecode
	 */
	public void setBytecode(String bytecode) {
		mBytecode = bytecode;
	}

	/**
	 *
	 * @return bytecode of this class and all its inner classes
	 */
	public String getBytecode() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(mBytecode);
		for (CodeElement child : children) {
			if (child instanceof ClassBlock) {
				buffer.append(((ClassBlock) child).getBytecode());
			}
		}
		return buffer.toString();
	}

	private String getAccessFlags() {
		clearBuffer();
		addAccess(mClassNode.access & ~Opcodes.ACC_SUPER);

		if (Util.containsFlag(mClassNode.access, Opcodes.ACC_ANNOTATION)) {
			buf.append("@interface ");
			removeFromBuffer("abstract ");
		} else if (Util.containsFlag(mClassNode.access, Opcodes.ACC_INTERFACE)) {
			buf.append("interface "); // interface is implicitly abstract
			removeFromBuffer("abstract ");
		} else if (!Util.containsFlag(mClassNode.access, Opcodes.ACC_ENUM)) {
			buf.append("class ");
		}
		return buf.toString();
	}

	private String getSuperClass(String superName) {
		clearBuffer();
		if (superName != null && !superName.equals("java/lang/Object")) {
			buf.append(" extends ").append(DataType.getTypeFromObject(superName).print()).append(" ");
		}
		return buf.toString();
	}

	private String getInterfaces(List interfaces) {
		clearBuffer();
		if (interfaces != null && interfaces.size() > 0) {
			buf.append(" implements ");
			for (int i = 0; i < interfaces.size(); i++) {
				buf.append(Util.getCommaIfNeeded(i));
				buf.append(DataType.getTypeFromObject((String) interfaces.get(i)).print());
			}
		}
		return buf.toString();
	}

	/**
	 * Decompiles methods of the class
	 * @param methods list of {@link MethodNode}s
	 */
	private void appendMethods(List methods) {
		for (Object method : methods) {
			MethodBlock methodBlock = new MethodBlock((MethodNode)method, this);
			methodBlock.setClassType(mClassType);
			methodBlock.setClassAccess(mClassNode.access);
			children.add(methodBlock.disassemble());
		}
	}

	/**
	 * Decompiles fields
	 * @param fields list of {@link FieldNode}s
	 */
	private void appendFields(List fields) {
		for (Object object : fields) {
			FieldNode field = (FieldNode)object;
			FieldBlock fieldBlock = new FieldBlock(field, this);
			children.add(fieldBlock.disassemble());
		}
	}

	/**
	 * Decompiles inner classes
	 * @param innerClasses list of {@link InnerClassNode}
	 */
	private void appendInnerClasses(List innerClasses) {
		for (Object object : innerClasses) {
			InnerClassNode innerClass = (InnerClassNode) object;
			if (shouldAddInnerClass(innerClass)) {
				try {
					children.add(Disassembler.getInstance().decompileInnerClass(innerClass.name, this));
				} catch (DecompilerException e) {
					children.add(new Statement(new PrimaryExpression(wrapInComment("Classfile of inner class " + innerClass.name + " was not found"),DataType.UNKNOWN), 0, this));
				}
			}
		}
	}

	private void storeInnerClassesNames(List innerClasses) {
		for (Object innerClass : innerClasses) {
			saveInnerClassName((InnerClassNode)innerClass);
		}
	}

	private void saveInnerClassName(InnerClassNode innerClass) {
		if (innerClass.innerName != null && innerClass.outerName != null) {
			Disassembler.getInstance().addInnerClassName(innerClass.name, innerClass.innerName);
		}
	}

	private boolean shouldAddInnerClass(InnerClassNode innerClass) {
		if (innerClass.innerName != null && innerClass.outerName != null) {
			return innerClass.name.equals(mClassNode.name + "$" + innerClass.innerName);
		} else if (innerClass.outerName == null) {
			int lastDollarLocation = innerClass.name.lastIndexOf("$");
			String classNameWithoutInnerClass = innerClass.name.substring(0, lastDollarLocation);
			String innerClassName = innerClass.name.substring(lastDollarLocation + 1);

			if (innerClass.innerName == null) {
				return classNameWithoutInnerClass.equals(mClassNode.name) && innerClassName.matches("[0-9]+$");
			} else {
				return classNameWithoutInnerClass.equals(mClassNode.name) && innerClassName.matches("[0-9]+.+$");
			}
		}

		return false;
	}

	private boolean isClass() {
		return !Util.containsFlag(mClassNode.access, Opcodes.ACC_ANNOTATION | Opcodes.ACC_INTERFACE | Opcodes.ACC_ENUM);
	}

	@Override
	public void write(Writer writer) throws IOException {
		String tabs = getTabs();
		if (tabs != null && !tabs.isEmpty()) {
			writer.write(NL);
		}
		printList(writer, mAnnotations);
		writer.append(tabs).append(mAccessFlags).append(mClassType.print())
				.append(mExtends).append(mImplements)
				.write(BLOCK_START);

		for (Writable child : children) {
			child.write(writer);
		}
		writer.append(tabs).write(BLOCK_END);
	}
}
