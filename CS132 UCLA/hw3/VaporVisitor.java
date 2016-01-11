
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionRest;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterRest;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.Node;
import syntaxtree.NodeList;
import syntaxtree.NodeListOptional;
import syntaxtree.NodeOptional;
import syntaxtree.NodeSequence;
import syntaxtree.NodeToken;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;

public class VaporVisitor extends GJDepthFirst<String, Integer> {

	Hashtable<String, Class> symboltable;
	String header;
	Class currentClass;
	Method currentMethod;
	int currentRegister = 0;

	public void setTable(SymbolTable t) {
		symboltable = t.symboltable;

	}

	@Override
	public String visit(NodeList n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(NodeListOptional n, Integer argu) {
		// TODO Auto-generated method stub
		if (n.present()) {
			String _ret = "";
			for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
				_ret = _ret + e.nextElement().accept(this, argu);
			}
			return _ret;
		} else
			return "";
	}

	@Override
	public String visit(NodeOptional n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(NodeSequence n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(NodeToken n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(Goal n, Integer argu) {
		// TODO Auto-generated method stub
		String temp = n.f0.accept(this, argu) + "\n" + n.f1.accept(this, argu);
		// p(temp);
		 String arrayallocatio = "\nfunc AllocArray(size)\n  bytes = MulS(size 4)\n  bytes = Add(bytes 4)\n"
	 				+ "  v = HeapAllocZ(bytes)\n  [v] = size\n  ret v";
		if(arrayalloc){
			temp = temp + arrayallocatio;
		}
		
		return process(temp);
	}

	public String process(String str) {
		String[] st = str.split("\\n+");
		String ret = "";
		for (String temp : st) {
			ret = ret + "\n" + temp;
		}
		return ret;
	}

	public void setClass(Class cls) {
		this.currentClass = cls;
	}

	public String getSpaces(int i) {
		String temp = "";
		for (int j = 0; j < i; j++) {
			temp = temp + "  ";
		}
		return temp;
	}

	@Override
	public String visit(MainClass n, Integer argu) {
		// TODO Auto-generated method stub
		setClass(symboltable.get(n.f1.f0.toString()));
		return "func Main()" + n.f15.accept(this, argu + 1) + "\n"
				+ getSpaces(argu + 1) + "ret";

	}

	@Override
	public String visit(TypeDeclaration n, Integer argu) {
		// TODO Auto-generated method stub
		return n.f0.accept(this, argu);
	}

	@Override
	public String visit(ClassDeclaration n, Integer argu) {
		// TODO Auto-generated method stub
		setClass(symboltable.get(n.f1.f0.toString()));
		return n.f4.accept(this, argu);
	}

	@Override
	public String visit(ClassExtendsDeclaration n, Integer argu) {
		// TODO Auto-generated method stub
		setClass(symboltable.get(n.f1.f0.toString()));
		return n.f6.accept(this, argu);
	}

	@Override
	public String visit(VarDeclaration n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	public Method getMethodDecl(String ident) {
		if (currentClass.funcs.containsKey(ident)) {
			return currentClass.funcs.get(ident);
		} else {
			String parent = currentClass.parentClass;
			Class temp;
			while (parent != null) {
				temp = symboltable.get(parent);
				if (temp.funcs.containsKey(ident)) {
					return temp.funcs.get(ident);
				} else {
					parent = temp.parentClass;
				}

			}
			System.out.println("Method not found in parents");
			return null;
		}

	}

	@Override
	public String visit(MethodDeclaration n, Integer argu) {
		// TODO Auto-generated method stub
		currentMethod = getMethodDecl(n.f2.f0.toString());
		String funcname = currentClass.name + "." + currentMethod.name;
		String args = n.f4.accept(this, argu);
		if (args == null) {
			args = "";
		}
		String funcdefine = getSpaces(argu) + "func " + funcname
				+ "(this " + args + ")";

		String func = funcdefine + n.f8.accept(this, argu + 1) + "\n";
		// p(func);
		String ret = n.f10.accept(this, argu + 1);
		func = func + ret;

		String[] spl = ret.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			return func + "\n" + getSpaces(argu + 1) + "ret " + line[0] + "\n";
		} else {

			return func + "\n" + getSpaces(argu + 1) + "ret " + line[1] + "\n";
		}
	}

	public void p(String s) {
		System.out.println("******" + s + "******");
	}

	@Override
	public String visit(FormalParameterList n, Integer argu) {
		// TODO Auto-generated method stub
		// p("In Here");
		String args = n.f0.accept(this, argu);
		// p(args);
		if (args == null) {
			return "";
		}
		Enumeration<Node> enm = n.f1.elements();
		while (enm.hasMoreElements()) {
			args += " " + enm.nextElement().accept(this, argu);
		}
		return args;
	}

	@Override
	public String visit(FormalParameter n, Integer argu) {
		// TODO Auto-generated method stub
		// p("IN here");
		return n.f1.f0.toString();
	}

	@Override
	public String visit(FormalParameterRest n, Integer argu) {
		// TODO Auto-generated method stub
		return n.f1.accept(this, argu);
	}

	@Override
	public String visit(Type n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(ArrayType n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(BooleanType n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(IntegerType n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(Statement n, Integer argu) {
		// TODO Auto-generated method stub
		return "\n" + n.f0.accept(this, argu);
	}

	@Override
	public String visit(Block n, Integer argu) {
		// TODO Auto-generated method stub
		return n.f1.accept(this, argu);
	}

	@Override
	public String visit(AssignmentStatement n, Integer argu) {
		// TODO Auto-generated method stub
		String ident = n.f0.f0.toString();
		if (!currentMethod.localvars.containsKey(ident)
				&& !currentMethod.args.containsKey(ident)) {
			ident = "[this+" + String.valueOf(currentClass.vars_pos.get(ident))
					+ "]";
		}

		String exp = n.f2.accept(this, argu);
		String reg;
		String[] spl = exp.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg = line[0];
		} else {

			reg = line[1];
		}

		return exp + "\n" + getSpaces(argu) + ident + " = " + reg;
	}

	@Override
	public String visit(ArrayAssignmentStatement n, Integer argu) {
		// TODO Auto-generated method stub
		String ret = n.f2.accept(this, argu);

		String reg;
		String[] spl = ret.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg = line[0];
		} else {

			reg = line[1];
		}
		String expr = n.f5.accept(this, argu);
		String reg1;
		spl = expr.split("\\n+");
		line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg1 = line[0];
		} else {

			reg1 = line[1];
		}

		ret = ret + expr;

		String ident = n.f0.f0.toString();
		if (!currentMethod.localvars.containsKey(ident)
				&& !currentMethod.args.containsKey(ident)) {
			ident = "[this+" + currentClass.vars_pos.get(ident) + "]";
		}

		String array = getSpaces(argu) + "t." + currentRegister + " = " + ident;
		String tempor = "t." + currentRegister;
		currentRegister++;

		String size = getSpaces(argu) + "t." + currentRegister + " = " + "["
				+ tempor + "+0" + "]";
		String sizeTemp = "t." + currentRegister;
		currentRegister++;
		size = size + "\n" + getSpaces(argu) + sizeTemp + " = " + "LtS(" + reg
				+ " " + sizeTemp + ")\n" + setChecker(argu, sizeTemp);

		String set_pos = getSpaces(argu) + reg + " = " + "MulS(" + reg
				+ " 4)\n" + getSpaces(argu) + tempor + " = " + "Add(" + tempor
				+ " " + reg + ")";
		String load = getSpaces(argu) + "[" + tempor + "+" + 4 + "] = " + reg1;

		return ret + "\n" + array + "\n" + size + "\n" + set_pos + "\n" + load;
	}

	int currentCheckNum = 0;

	private String setChecker(int spaces, String reg) {
		String check = "outofbound" + currentCheckNum;

		currentCheckNum++;
		String checkif = getSpaces(spaces) + "if " + reg + " goto :" + check
				+ "\n" + getSpaces(spaces + 1)
				+ "Error(\"array index out of bounds\")";

		return checkif + "\n" + getSpaces(spaces) + check + ":";
	}

	@Override
	public String visit(IfStatement n, Integer argu) {
		// TODO Auto-generated method stub
		String exp = n.f2.accept(this, argu);
		String reg1;
		String[] spl = exp.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg1 = line[0];
		} else {

			reg1 = line[1];
		}
		String iff = n.f4.accept(this, argu + 1);
		String els = n.f6.accept(this, argu + 1);

		return exp + "\n" + getIf_else(argu, reg1, iff, els);
	}

	int ifnum = 1;

	String getIf_else(int spaces, String cond, String iff, String els) {
		String ifelse = getSpaces(spaces) + "if0 " + cond + " goto :else"
				+ ifnum + "\n" + iff + "\n" + getSpaces(spaces + 1)
				+ "goto :ifend" + ifnum;
		String ret = ifelse + "\n" + getSpaces(spaces) + "else" + ifnum + ":"
				+ els + "\n" + getSpaces(spaces) + "ifend" + ifnum + ":\n";
		ifnum++;
		return ret;
	}

	int loop = 1;

	@Override
	public String visit(WhileStatement n, Integer argu) {
		// TODO Auto-generated method stub
		String exp = n.f2.accept(this, argu + 1);
		String reg1;
		String[] spl = exp.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg1 = line[0];
		} else {

			reg1 = line[1];
		}
		int loop_temp = loop;
		String cond_exp = getSpaces(argu) + "loop" + loop + ":\n" + exp;
		loop++;

		String cond = getIf_else(argu + 1, reg1, n.f4.accept(this, argu + 2),
				getSpaces(argu + 2) + "goto :loop_end" + loop_temp)
				+ "\n"
				+ getSpaces(argu + 1)
				+ "goto :loop"
				+ loop_temp
				+ "\n"
				+ getSpaces(argu) + "loop_end" + loop_temp + ":";
		loop++;
		return cond_exp + "\n" + cond;
	}

	@Override
	public String visit(PrintStatement n, Integer argu) {
		// TODO Auto-generated method stub
		String exp = n.f2.accept(this, argu);
		String reg1;
		String[] spl = exp.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg1 = line[0];
		} else {

			reg1 = line[1];
		}
		return exp + "\n" + getSpaces(argu) + "PrintIntS(" + reg1 + ")";
	}

	@Override
	public String visit(Expression n, Integer argu) {
		// TODO Auto-generated method stub
		return "\n" + n.f0.accept(this, argu);
	}

	public String extracttemp(String exp) {
		String reg1;
		String[] spl = exp.split("\\n+");
		String[] line = spl[spl.length - 1].split("\\s+");

		if (line[0].length() != 0) {
			reg1 = line[0];
		} else {

			reg1 = line[1];
		}
		return reg1;

	}

	@Override
	public String visit(AndExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);
		String and = getIf_else(argu, tempor1, getSpaces(argu + 1) + "t."
				+ currentRegister + " = " + tempor2, getSpaces(argu + 1) + "t."
				+ currentRegister + " = 0");
		String ret = cond1 + "\n" + cond2 + "\n" + and + "\n"
				+ getSpaces(argu + 1) + "t." + currentRegister + " = " + "t."
				+ currentRegister;
		currentRegister++;
		return ret;

	}

	@Override
	public String visit(CompareExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);
		String ret = getSpaces(argu) + "t." + currentRegister + " = " + "LtS("
				+ tempor1 + " " + tempor2 + ")";
		ret = cond1 + "\n" + cond2 + "\n" + ret;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(PlusExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);
		String ret = getSpaces(argu) + "t." + currentRegister + " = " + "Add("
				+ tempor1 + " " + tempor2 + ")";
		ret = cond1 + "\n" + cond2 + "\n" + ret;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(MinusExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);
		String ret = getSpaces(argu) + "t." + currentRegister + " = " + "Sub("
				+ tempor1 + " " + tempor2 + ")";
		ret = cond1 + "\n" + cond2 + "\n" + ret;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(TimesExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);
		String ret = getSpaces(argu) + "t." + currentRegister + " = " + "MulS("
				+ tempor1 + " " + tempor2 + ")";
		ret = cond1 + "\n" + cond2 + "\n" + ret;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(ArrayLookup n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String tempor2 = extracttemp(cond2);

		String size = getSpaces(argu) + "t." + currentRegister + " = " + "["
				+ tempor1 + "+0" + "]";
		String sizeTemp = "t." + currentRegister;
		currentRegister++;
		size = size + "\n" + getSpaces(argu) + sizeTemp + " = " + "LtS("
				+ tempor2 + " " + sizeTemp + ")\n" + setChecker(argu, sizeTemp);

		String set_pos = getSpaces(argu) + tempor2 + " = " + "MulS(" + tempor2
				+ " 4)\n" + getSpaces(argu) + tempor1 + " = " + "Add("
				+ tempor1 + " " + tempor2 + ")";
		String load = getSpaces(argu) + "t." + currentRegister + " = " + "["
				+ tempor1 + "+" + 4 + "]";
		currentRegister++;
		return cond1 + "\n" + cond2 + "\n" + size + "\n" + set_pos + "\n"
				+ load;
	}

	@Override
	public String visit(ArrayLength n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f0.accept(this, argu);
		// String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		// String tempor2 = extracttemp(cond2);
		String ret = cond1 + "\n" + getSpaces(argu) + "t." + currentRegister
				+ " = [" + tempor1 + "+0]";
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(MessageSend n, Integer argu) {
		// TODO Auto-generated method stub
		String exp = n.f0.accept(this, argu);
		String tempor1 = extracttemp(exp);

		Class cls = new TypeChecker(new SymbolTable(symboltable), currentClass,
				currentMethod).getTypeExp(n.f0);
		// p(n.f0.toString()+"");
		cls = symboltable.get(cls.name);

		int method_pos = cls.funcs_pos.get(n.f2.f0.toString());

		ArrayList<String> temoporaries = new ArrayList<String>();
		String args = "";
		if (n.f4.present()) {
			ExpressionList list = (ExpressionList) n.f4.node;
			args = list.f0.accept(this, argu);
			String regargs = extracttemp(args);
			temoporaries.add(regargs);
			Enumeration<Node> e = list.f1.elements();
			while (e.hasMoreElements()) {
				args = args + e.nextElement().accept(this, argu);
				regargs = extracttemp(args);
				temoporaries.add(regargs);
			}
		}

		exp = exp + args;

		exp = exp + "\n" + getSpaces(argu) + "t." + currentRegister + " = "
				+ "[" + tempor1 + "]";
		currentRegister++;

		exp = exp + "\n" + getSpaces(argu) + "t." + currentRegister + " = [t."
				+ (currentRegister - 1) + "+" + method_pos + "]";

		String method_args = "";
		for (String temp : temoporaries)
			method_args = method_args + " " + temp;

		currentRegister++;
		exp = exp + "\n" + getSpaces(argu) + "t." + currentRegister + " = "
				+ "call " + "t." + (currentRegister - 1) + "(" + tempor1
				+ method_args + ")";
		currentRegister++;
		return exp;

	}

	@Override
	public String visit(ExpressionList n, Integer argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public String visit(ExpressionRest n, Integer argu) {
		// TODO Auto-generated method stub
		return n.f1.accept(this, argu);
	}

	@Override
	public String visit(PrimaryExpression n, Integer argu) {

		return n.f0.accept(this, argu);
	}

	@Override
	public String visit(IntegerLiteral n, Integer argu) {
		// TODO Auto-generated method stub
		String ret = getSpaces(argu) + "t." + currentRegister + " = "
				+ n.f0.tokenImage;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(TrueLiteral n, Integer argu) {
		// TODO Auto-generated method stub
		String ret = getSpaces(argu) + "t." + currentRegister + " = 1";
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(FalseLiteral n, Integer argu) {
		// TODO Auto-generated method stub
		String ret = getSpaces(argu) + "t." + currentRegister + " = 0";
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(Identifier n, Integer argu) {
		// TODO Auto-generated method stub
		String ident = n.f0.toString();
		if (!currentMethod.localvars.containsKey(ident)
				&& !currentMethod.args.containsKey(ident)) {
			ident = "[this+" + currentClass.vars_pos.get(ident) + "]";
		}
		ident = getSpaces(argu) + "t." + currentRegister + " = " + ident;
		currentRegister++;

		return ident;
	}

	@Override
	public String visit(ThisExpression n, Integer argu) {
		// TODO
		// p("here");
		String ret = getSpaces(argu) + "t." + currentRegister + " = " + "this";
		currentRegister++;
		return ret;
	}
boolean arrayalloc  =false;
	@Override
	public String visit(ArrayAllocationExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f3.accept(this, argu);
		// String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		cond1 = cond1 + "\n" + getSpaces(argu) + "t." + currentRegister + " = "
				+ "call :AllocArray(" + tempor1 + ")";
		currentRegister++;
		if(cond1!=null){
			arrayalloc = true;
		}
		return cond1;
	}

	int num_null = 1;

	@Override
	public String visit(AllocationExpression n, Integer argu) {
		// TODO Auto-generated method stub

		Class cls = symboltable.get(n.f1.f0.toString());
		String alloc = getSpaces(argu) + "t." + currentRegister + " = "
				+ "HeapAllocZ(" + cls.sizebytes + ")";

		String ret = ":vmt_" + cls.name;
		ret = alloc + "\n" + getSpaces(argu) + "[t." + currentRegister
				+ "] = " + ret;
		ret = ret + "\n" + getSpaces(argu) + "if " + "t." + currentRegister
				+ " goto :null" + num_null + "\n" + getSpaces(argu + 1)
				+ "Error(\"null pointer\")\n" + getSpaces(argu) + "null"
				+ num_null + ":\n" + getSpaces(argu) + "t." + currentRegister
				+ " = " + "t." + currentRegister;
		num_null++;
		currentRegister++;
		return ret;
	}

	@Override
	public String visit(NotExpression n, Integer argu) {
		// TODO Auto-generated method stub
		String cond1 = n.f1.accept(this, argu);
		// String cond2 = n.f2.accept(this, argu);
		String tempor1 = extracttemp(cond1);
		String ret = getSpaces(argu) + "t." + currentRegister + " = Sub(1 "
				+ tempor1 + ")";
		currentRegister++;
		return cond1 + "\n" + ret;
	}

	@Override
	public String visit(BracketExpression n, Integer argu) {
		// TODO Auto-generated method stub
		return n.f1.accept(this, argu);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

}
