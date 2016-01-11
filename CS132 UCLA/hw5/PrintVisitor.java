import java.util.HashMap;
import java.util.Map.Entry;

import cs132.vapor.ast.VAssign;
import cs132.vapor.ast.VBranch;
import cs132.vapor.ast.VBuiltIn;
import cs132.vapor.ast.VCall;
import cs132.vapor.ast.VCodeLabel;
import cs132.vapor.ast.VFunction;
import cs132.vapor.ast.VGoto;
import cs132.vapor.ast.VInstr;
import cs132.vapor.ast.VInstr.Visitor;
import cs132.vapor.ast.VMemRef.Global;
import cs132.vapor.ast.VMemRef.Stack;
import cs132.vapor.ast.VMemRead;
import cs132.vapor.ast.VMemWrite;
import cs132.vapor.ast.VReturn;
import cs132.vapor.ast.VaporProgram;

public class PrintVisitor extends Visitor<Throwable> {

	VaporProgram vp;

	public PrintVisitor(VaporProgram vp) {
		this.vp = vp;
		String ret = ".text\n\n";
		// MAin
		ret = ret + " jal Main\n li $v0 10\n syscall\n\n";
		System.out.println(ret);

	}

	VFunction currentMehtod;
	HashMap<String, Integer> mapper_I_L = new HashMap<String, Integer>();

	public void initiateVisits() throws Throwable {
		for (VFunction func : vp.functions) {
			currentMehtod = func;
			mapper_I_L = new HashMap<String, Integer>();
			String ret = "";
			ret = ret + func.ident + ":\n";
			ret = ret + " sw $fp -8($sp)\n" + " move $fp $sp\n";
			int num = func.stack.local + func.stack.out;
			ret = ret + " subu $sp $sp " + (8 + 4 * num) + "\n";
			ret = ret + " sw $ra -4($fp)\n";
			System.out.println(ret);
			currentMehtod = func;
			for (VCodeLabel i : func.labels) {

				int nextLine = i.sourcePos.line + 1;
				int labelIndex = 0;
				while (labelIndex < func.labels.length
						&& func.labels[labelIndex].sourcePos.line < nextLine) {
					labelIndex++;
				}
				while (labelIndex < func.labels.length
						&& func.labels[labelIndex].sourcePos.line == nextLine) {
					nextLine++;
					labelIndex++;
				}

				mapper_I_L.put(i.ident, nextLine);
			}

			for (VInstr instr : func.body) {
				instr.accept(this);
			}
		}

		printfooter();
	}

	private void printfooter() {
		// TODO Auto-generated method stub

		String ret = "_print:\n li $v0 1\n  syscall\n la $a0 _newline\n li $v0 4\n syscall\n jr $ra\n\n";

		ret = ret + "_error:\n li $v0 4\n syscall\n li $v0 10\n syscall\n\n";

		ret = ret + "_heapAlloc:\n li $v0 9\n syscall\n jr $ra\n\n";
		ret = ret
				+ ".data\n.align 0\n_newline: .asciiz \"\\n\"\n_str0: .asciiz \"null pointer\\n\"";
		System.out.println(ret);
	}

	@Override
	public void visit(VAssign arg0) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}

		if (arg0.source.toString().startsWith("$")) {
			ret = ret + " move " + arg0.dest.toString() + " "
					+ arg0.source.toString() + "\n";
		} else if (arg0.source.toString().startsWith(":")) {
			ret = ret + " la " + arg0.dest.toString() + " "
					+ arg0.source.toString().substring(1) + "\n";
		} else if (checkInteger(arg0.source.toString())) {
			ret = ret + " li " + arg0.dest.toString() + " "
					+ arg0.source.toString() + "\n";
		}
		System.out.println(ret);
	}

	private boolean checkInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	@Override
	public void visit(VCall arg0) throws Throwable {
		// TODO Auto-generated method stub

		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}

		if (arg0.addr.toString().startsWith(":")) {
			ret = ret + " jal " + arg0.addr.toString().replaceFirst(":", "")
					+ "\n";
		} else {
			ret = ret + " jalr " + arg0.addr.toString() + "\n";
		}
		System.out.println(ret);

	}

	@Override
	public void visit(VBuiltIn arg0) throws Throwable {
		// TODO Auto-g
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}
		String name = arg0.op.name;
		if (name.equals("Add")) {
			if (arg0.args[0].toString().startsWith("$")) {
				String first = arg0.args[0].toString();
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " addu " + arg0.dest + " " + first + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					ret = ret + " li " + "$t9 " + arg0.args[1].toString()
							+ "\n";
					ret = ret + " addu " + arg0.dest + " " + first + " "
							+ "$t9" + "\n";
				}
			} else {
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " li " + "$t9 " + arg0.args[0].toString()
							+ "\n";
					ret = ret + " addu " + arg0.dest + " " + "$t9" + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					int add = Integer.parseInt(arg0.args[0].toString())
							+ Integer.parseInt(arg0.args[1].toString());
					ret = ret + " li " + arg0.dest + " " + add + "\n";
				}
			}
		} else if (name.equals("PrintIntS")) {
			if (arg0.args[0].toString().startsWith("$")) {
				ret = ret + " move " + "$a0" + " " + arg0.args[0].toString()
						+ "\n";
			} else {
				ret = ret + " li " + "$a0 " + arg0.args[0].toString() + "\n";
			}
			ret = ret + " jal _print\n";
		} else if (name.equals("Sub")) {

			if (arg0.args[0].toString().startsWith("$")) {
				String first = arg0.args[0].toString();
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " subu " + arg0.dest + " " + first + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					ret = ret + " li " + "$t9 " + arg0.args[1].toString()
							+ "\n";
					ret = ret + " subu " + arg0.dest + " " + first + " "
							+ "$t9" + "\n";
				}
			} else {
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " li " + "$t9 " + arg0.args[0].toString()
							+ "\n";
					ret = ret + " subu " + arg0.dest + " " + "$t9" + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					int sub = Integer.parseInt(arg0.args[0].toString())
							- Integer.parseInt(arg0.args[1].toString());
					ret = ret + " li " + arg0.dest + " " + sub + "\n";
				}
			}
		} else if (name.equals("Eq")) {

			if (arg0.args[0].toString().startsWith("$")) {
				String first = arg0.args[0].toString();
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " xor " + arg0.dest + " " + first + " "
							+ arg0.args[1].toString() + "\n";
					ret = ret + " slti " + arg0.dest + " " + arg0.dest + " "
							+ "1\n";
				} else {
					ret = ret + " li " + "$t9 " + arg0.args[1].toString()
							+ "\n";
					ret = ret + " xor " + arg0.dest + " " + first + " "
							+ "$t9\n";
					ret = ret + " slti " + arg0.dest + " " + arg0.dest + " "
							+ "1\n";
				}
			} else {
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " li " + "$t9 " + arg0.args[0].toString()
							+ "\n";
					ret = ret + " xor " + arg0.dest + " " + "$t9" + " "
							+ arg0.args[1].toString() + "\n";
					ret = ret + " slti " + arg0.dest + " " + arg0.dest + " "
							+ "1\n";
				} else {
					int cond = 1;
					if (!arg0.args[0].toString()
							.equals(arg0.args[1].toString())) {
						cond = 0;
					}
					ret = ret + " li " + arg0.dest + " " + cond + "\n";
				}
			}
		} else if (name.equals("MulS")) {
			if (arg0.args[0].toString().startsWith("$")) {
				String first = arg0.args[0].toString();
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " mul " + arg0.dest + " " + first + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					ret = ret + " li " + "$t9 " + arg0.args[1].toString()
							+ "\n";
					ret = ret + " mul " + arg0.dest + " " + first + " " + "$t9"
							+ "\n";
				}
			} else {
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " li " + "$t9 " + arg0.args[0].toString()
							+ "\n";
					ret = ret + " mul " + arg0.dest + " " + "$t9" + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					int mul = Integer.parseInt(arg0.args[0].toString())
							* Integer.parseInt(arg0.args[1].toString());
					ret = ret + " li " + arg0.dest + " " + mul + "\n";
				}
			}
		} else if (name.equals("Lt") || name.equals("LtS")) {

			if (arg0.args[0].toString().startsWith("$")) {
				String first = arg0.args[0].toString();
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " slt " + arg0.dest.toString() + " " + first
							+ " " + arg0.args[1].toString() + "\n";
				} else {
					ret = ret + " slti " + arg0.dest + " "
							+ arg0.args[0].toString() + " "
							+ arg0.args[1].toString() + "\n";
				}
			} else {
				if (arg0.args[1].toString().startsWith("$")) {
					ret = ret + " li " + "$t9 " + arg0.args[0].toString()
							+ "\n";
					ret = ret + " slt " + arg0.dest + " " + "$t9" + " "
							+ arg0.args[1].toString() + "\n";
				} else {
					int cond = 1;
					if (Integer.parseInt(arg0.args[0].toString()) >= Integer
							.parseInt(arg0.args[1].toString())) {
						cond = 0;
					}
					ret = ret + " li " + arg0.dest + " " + cond + "\n";
				}
			}
		} else if (name.equals("Error")) {

			if (arg0.args[0].toString().startsWith("$")) {
				ret = ret + " lw " + "$a0" + " " + "_str0\n";
			}
			ret = ret + " jal _error\n";
		} else if (name.equals("HeapAllocZ")) {
			if (arg0.args[0].toString().startsWith("$")) {
				ret = ret + " move " + "$a0" + " " + arg0.args[0].toString()
						+ "\n";
			} else {
				ret = ret + " li " + "$a0 " + arg0.args[0].toString() + "\n";
			}
			ret = ret + " jal _heapAlloc\n";
			ret = ret + " move " + arg0.dest.toString() + " " + "$v0\n";
		}
		System.out.println(ret);
	}

	@Override
	public void visit(VMemWrite arg0) throws Throwable {
		// TODO Auto-generated method stub

		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}

		String reg = null;

		if (arg0.source.toString().startsWith("$")) {
			reg = arg0.source.toString();
		} else if (arg0.source.toString().startsWith(":")) {
			ret = ret + " la $t9 "
					+ arg0.source.toString().replaceFirst(":", "") + "\n";
			reg = "$t9";
		} else if (checkInteger(arg0.source.toString())) {
			ret = ret + " li $t9 " + arg0.source.toString() + "\n";
			reg = "$t9";
		}

		if (arg0.dest instanceof Stack) {
			Stack refer = (Stack) arg0.dest;
			if (refer.region.equals(Stack.Region.Out)) {
				ret = ret + " sw " + reg + " " + (refer.index * 4) + "($sp)"
						+ "\n";
			} else if (refer.region.equals(Stack.Region.Local)) {
				ret = ret + " sw " + reg + " "
						+ (4 * (refer.index + currentMehtod.stack.out))
						+ "($sp)" + "\n";
			}
		} else {
			Global refer = (Global) arg0.dest;
			ret = ret + " sw " + reg + " " + refer.byteOffset + "("
					+ refer.base.toString() + ")" + "\n";

		}
		System.out.println(ret);
	}

	@Override
	public void visit(VMemRead arg0) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}
		if (arg0.source instanceof Stack) {
			Stack refer = (Stack) arg0.source;
			if (refer.region.equals(Stack.Region.In)) {
				ret = ret + " lw " + arg0.dest.toString() + " "
						+ (refer.index * 4) + "($fp)\n";
			} else if (refer.region.equals(Stack.Region.Local)) {
				ret = ret + " lw " + arg0.dest.toString() + " "
						+ ((refer.index + currentMehtod.stack.out) * 4)
						+ "($sp)\n";
			}
		} else {
			Global refer = (Global) arg0.source;
			ret = ret + " lw " + arg0.dest.toString() + " " + refer.byteOffset
					+ "(" + refer.base + ")\n";
		}
		System.out.println(ret);
	}

	@Override
	public void visit(VBranch arg0) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}
		if (!arg0.positive)
			ret = ret + " beqz" + " " + arg0.value + " " + arg0.target.ident
					+ "\n";
		else
			ret = ret + " bnez" + " " + arg0.value + " " + arg0.target.ident
					+ "\n";
		System.out.println(ret);
	}

	@Override
	public void visit(VGoto arg0) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}
		System.out.println(ret + " j " + arg0.target.toString().substring(1));

	}

	@Override
	public void visit(VReturn arg0) throws Throwable {
		// TODO Auto-generated method stub
		String ret = "";
		for (Entry<String, Integer> i : mapper_I_L.entrySet()) {
			if (i.getValue().equals(arg0.sourcePos.line))
				ret = ret + i.getKey() + ":\n";
		}
		ret = ret
				+ " lw $ra -4($fp)\n lw $fp -8($fp)\n addu $sp $sp "
				+ ((currentMehtod.stack.local + currentMehtod.stack.out) * 4 + 8)
				+ "\n jr $ra\n";
		System.out.println(ret);

	}

}
