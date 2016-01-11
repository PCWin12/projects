
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

public class TypeChecker extends GJDepthFirst<SymbolType, SymbolType> {
	public boolean foundError = false;
	SymbolTable currentTable;
	Class currentClass;
	Method currentMethod;

	
	
	public TypeChecker(SymbolTable t , Class clas  , Method mtd){
		currentTable = t;
		currentClass = clas;
		currentMethod = mtd;
		
	}
public TypeChecker() {
		// TODO Auto-generated constructor stub
	}
public Class getTypeExp(PrimaryExpression n){
	 SymbolType pexp = n.accept(this,currentTable);
	 if (pexp.type.equals("identi")) {

			pexp = getTypeMethod(((Identi) pexp).name);
		}

		if (pexp == null) {
			System.out.println("wrong");
			return null;
		}
		if (!pexp.type.equals("class")) {
			System.out.println("wrong");
			return null;
		}
		return (Class)pexp;
	}
	public SymbolTable getTable(SymbolType argu) {
		return ((SymbolTable) argu);
	}

	public void setClass(Class cls) {
		this.currentClass = cls;
	}

	@Override
	public SymbolType visit(NodeList n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(NodeListOptional n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(NodeOptional n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(NodeSequence n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(NodeToken n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(Goal n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(MainClass n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolTable table = getTable(argu);
		setClass(table.symboltable.get(n.f1.f0.toString()));
		n.f14.accept(this, currentClass.funcs.get("main"));
		return null;
	}

	@Override
	public SymbolType visit(TypeDeclaration n, SymbolType argu) {
		// TODO Auto-generated method stub
		return super.visit(n, argu);
	}

	@Override
	public SymbolType visit(ClassDeclaration n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolTable table = getTable(argu);
		Class clas = table.symboltable.get(n.f1.f0.toString());
		setClass(clas);
		n.f3.accept(this, clas);
		n.f4.accept(this, clas);
		return null;
	}

	@Override
	public SymbolType visit(ClassExtendsDeclaration n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolTable table = getTable(argu);
		Class clas = table.symboltable.get(n.f1.f0.toString());
		setClass(clas);
		String extender = n.f3.f0.toString();
		if (table.symboltable.containsKey(extender)) {
			clas.extended = true;
			clas.parentClass = extender;
			Class extnds = table.symboltable.get(extender);
			while (extnds.extended) {
				if (extnds.name.equals(clas.name)) {
					foundError = true;
					break;
				} else {
					extnds = table.symboltable.get(extnds.parentClass);
				}
			}

			n.f5.accept(this, clas);
			n.f6.accept(this, clas);
		} else {
			foundError = true;
		}
		return null;
	}

	@Override
	public SymbolType visit(VarDeclaration n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType varType = n.f0.accept(this, argu);
		if (varType == null) {
			foundError = true;
			return null;
		}
		return varType;
	}

	@Override
	public SymbolType visit(MethodDeclaration n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType mtdType = n.f1.accept(this, argu);
		if (mtdType == null) {
			foundError = true;
		}
		String mtdName = n.f2.f0.toString();
		currentMethod = currentClass.funcs.get(mtdName);

		n.f4.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		if (mtdType.type.equals("identi")) {
			mtdType = getTypeMethod(((Identi) mtdType).name);
		}

		SymbolType ret_exp = n.f10.accept(this, argu);
		if (ret_exp.type.equals("identi")) {
			ret_exp = getTypeMethod(((Identi) ret_exp).name);
		} else if (mtdType.type.equals("class")) {
			if (!checkClass(ret_exp, mtdType)) {
				foundError = true;
			}
		} else {
			if (!typeCheck(mtdType, ret_exp)) {
				foundError = true;
			}
		}

		if (currentClass.extended) {
			Class tmp = currentClass;
			while (tmp.extended) {
				tmp = currentTable.symboltable.get(currentClass.parentClass); // parent
																				
				if (tmp == null) {
					break;
				}
				if (tmp.funcs.containsKey(mtdName)) {
					SymbolType typeret = tmp.funcs.get(mtdName).retType;
					if (mtdType.type.equals("class")) {
						if (!checkClass(mtdType, typeret)) {
							foundError = true;
						}
					} else 					{
						if (!typeCheck(mtdType, typeret)) {
							foundError = true;
						}
					}
					break;
				}

			}
		}

		return null;
	}

	@Override
	public SymbolType visit(FormalParameterList n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	@Override
	public SymbolType visit(FormalParameter n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (n.f1.accept(this, argu) == null) {
			foundError = true;
			return null;
		}
		return null;
	}

	@Override
	public SymbolType visit(FormalParameterRest n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	@Override
	public SymbolType visit(Type n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType type = n.f0.accept(this, argu);
		if (type.type.equals("identi")) {
			if (!currentTable.symboltable.containsKey(((Identi) type).name)) {
				foundError = true;
				return null;
			}
		}
		return type;
	}

	@Override
	public SymbolType visit(ArrayType n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Array();
	}

	@Override
	public SymbolType visit(BooleanType n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Boolean();
	}

	@Override
	public SymbolType visit(IntegerType n, SymbolType argu) {

		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Integ();
	}

	@Override
	public SymbolType visit(Statement n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return n.f0.accept(this, argu);
	}

	@Override
	public SymbolType visit(Block n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		n.f1.accept(this, argu);
		return null;
	}

	@Override
	public SymbolType visit(AssignmentStatement n, SymbolType argu) {
		// TODO Auto-generated method stub

		SymbolType exp = n.f2.accept(this, argu); 
		if (exp == null) {
			foundError = true;
			return null;
		}

		if (exp.type == "identi") {
			exp = getTypeMethod(((Identi) exp).name);
		}

		SymbolType idt = getTypeMethod(n.f0.f0.toString());
		if (idt == null) {
			foundError = true;
			return null;
		} else if (idt.type.equals("identi")) {

			idt = getTypeMethod(((Identi) idt).name);
		}

		if (idt.type.equals("class")) {
			if (typeCheck(idt, exp) || checkClass(exp, idt)) {
				return null;
			} else {
				foundError = true;
				return null;
			}
		} else {
			if (!typeCheck(idt, exp)) {

				foundError = true;
				return null;
			} else {
				return null;
			}
		}

	}

	@Override
	public SymbolType visit(ArrayAssignmentStatement n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());

		if (!typeCheck(n.f0.accept(this, argu), "array")
				|| !typeCheck(n.f2.accept(this, argu), "integer")
				|| !typeCheck(n.f5.accept(this, argu), "integer")) {
			foundError = true;
		}
		return null;

	}

	@Override
	public SymbolType visit(IfStatement n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f2.accept(this, argu), "bool")) {
			foundError = true;
		}
		n.f4.accept(this, argu);
		n.f6.accept(this, argu);
		return null;
	}

	@Override
	public SymbolType visit(WhileStatement n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f2.accept(this, argu), "bool")) {
			foundError = true;
		}
		n.f4.accept(this, argu);
		return null;
	}

	@Override
	public SymbolType visit(PrintStatement n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return null;
	}

	@Override
	public SymbolType visit(Expression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return n.f0.accept(this, argu);
	}

	@Override
	public SymbolType visit(AndExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "bool")
				|| !typeCheck(n.f2.accept(this, argu), "bool")) {
			foundError = true;

		}
		return new Boolean();
	}

	@Override
	public SymbolType visit(CompareExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "integer")
				|| !typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Boolean();
	}

	@Override
	public SymbolType visit(PlusExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "integer")
				|| !typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Integ();
	}

	@Override
	public SymbolType visit(MinusExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "integer")
				|| !typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Integ();
	}

	@Override
	public SymbolType visit(TimesExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());

		if (!typeCheck(n.f0.accept(this, argu), "integer")
				|| !typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Integ();
	}

	@Override
	public SymbolType visit(ArrayLookup n, SymbolType argu) {
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "array")
				|| !typeCheck(n.f2.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Integ();
	}

	@Override
	public SymbolType visit(ArrayLength n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f0.accept(this, argu), "array")) {
			foundError = true;
		}
		return new Integ();
	}

	@Override
	public SymbolType visit(MessageSend n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType pexp = n.f0.accept(this, argu);
		if (pexp.type.equals("identi")) {

			pexp = getTypeMethod(((Identi) pexp).name);
		}

		if (pexp == null) {
			foundError = true;
			return null;
		}
		if (!pexp.type.equals("class")) {
			foundError = true;
			return null;
		}

		Method mtd = getClassMethod(pexp, n.f2.f0.toString());
		if (mtd == null) {
			foundError = true;
			return null;
		}
		SymbolType args = n.f4.accept(this, mtd);
		if (args == null) {
			if (mtd.args.size() != 0) {
				foundError = true;
			}
		}
		return mtd.retType;

	}

	private Method getClassMethod(SymbolType pexp, String string) {
		// TODO Auto-generated method stub

		Method mtd = null;
		Class clas = null;
		if (Class.class.isInstance(pexp)) {
			clas = (Class) pexp;
		} else {
			Identi id = (Identi) pexp;

			clas = currentTable.symboltable.get(id.name);
		}
		if (clas.funcs.containsKey(string)) {
			return clas.funcs.get(string);
		} else { // check parent class
			Class ext = clas;
			while (ext.extended) {
				ext = currentTable.symboltable.get(ext.parentClass);
				if (ext == null) {
					break; // not found
				}
				if (ext.funcs.containsKey(string)) {
					return ext.funcs.get(string);
				}
			}
		}
		return mtd;
	}

	@Override
	public SymbolType visit(ExpressionList n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType arg = n.f0.accept(this, argu);

		Method mtd = (Method) argu;
		if (arg == null) {
			return null;
		} else {
			if (arg.type.equals("identi")) {
				arg = getTypeMethod(((Identi) arg).name);
			}
		}
		// Compare first arg
		SymbolType argDec;
		if (mtd.types.size() > 0) {
			argDec = mtd.types.get(0);
			if (argDec.type.equals("identi")) {
				argDec = getTypeMethod(((Identi) argDec).name);
			}
		} else {
			foundError = true;
			return null;

		}
		if (arg.type.equals("class")) {

			if (!checkClass(arg, argDec)) {
				foundError = true;
			}
		} else {
			if (!typeCheck(arg, argDec)) {
				foundError = true;
				return mtd;
			}
		}
		argNum = 0;
		n.f1.accept(this, argu);
		if (mtd.types.size() - 1 != argNum) {
			foundError = true;
		}
		return mtd;

	}

	public boolean checkClass(SymbolType arg, SymbolType argDec) {
		// TODO Auto-generated method stub
		if (!Class.class.isInstance(arg) || !Class.class.isInstance(argDec)) {
			return false;
		}
		Class parent = (Class) argDec;
		Class extender = (Class) arg;
		if (parent.name.equals(extender.name)) {
			return true;
		}
		while (extender.extended) {
			if (extender.parentClass.equals(parent.name)) {
				return true;
			}
			extender = currentTable.symboltable.get(extender.parentClass);
			if (extender == null) {
				break;
			}
		}
		return false;
	}

	public SymbolType getIndentiType(SymbolType sym) {
		if (sym.type.equals("identi")) {
			return currentTable.symboltable.get(((Identi) sym).name);
		}
		return sym;
	}

	public SymbolType getTypeMethod(String name) {
		// TODO Auto-generated method stub
		// First check method and then class and then parent classes

		SymbolType symType;
		if (currentMethod != null) {

			if (currentMethod.args.containsKey(name)) {
				symType = currentMethod.args.get(name);
				return getIndentiType(symType);
			} else if (currentMethod.localvars.containsKey(name)) {
				symType = currentMethod.localvars.get(name);
				return getIndentiType(symType);
			}

		}
		if (currentClass != null) {
			if (currentClass.vars.containsKey(name)) {
				symType = currentClass.vars.get(name);
				return getIndentiType(symType);
			} else if (currentTable.symboltable.containsKey(name)) {
				return currentTable.symboltable.get(name);

			}
		}
		// not in current Class and method then search in parent classe
		Class clas = currentClass;
		while (clas.extended) {
			clas = currentTable.symboltable.get(clas.parentClass);
			if (clas == null) {
				break;
			}
			if (clas.vars.containsKey(name)) {
				symType = clas.vars.get(name);
				if (symType.type.equals("identi")) {
					return currentTable.symboltable.get(((Identi) symType).name);
				}
				return clas.vars.get(name);
			}
		}

		return null;

	}

	int argNum = 0;

	@Override
	public SymbolType visit(ExpressionRest n, SymbolType argu) {
		argNum = argNum + 1;
		// System.out.println(n.toString());
		// TODO Auto-generated method stub
		Method mtd = (Method) argu;
		if (mtd.types.size() <= argNum
				|| !typeCheck(n.f1.accept(this, argu), mtd.types.get(argNum))) {
			foundError = true;
		}

		return null;
	}

	private boolean typeCheck(SymbolType accept, SymbolType symbolType) {
		// TODO Auto-generated method stub
		if (accept == null || symbolType == null) {
			return false;
		}
		if (accept.type.equals("identi")) {
			accept = getTypeMethod(((Identi) accept).name);
			if (accept == null) {
				return false;
			}
		}
		if (symbolType.type.equals("identi")) {
			symbolType = getTypeMethod(((Identi) symbolType).name);
			if (symbolType == null) {
				return false;
			}
		}
		if (accept.type.equals("class") && symbolType.type.equals("class")) {
			Class c1 = (Class) accept;
			Class c2 = (Class) symbolType;
			return c1.name == c2.name;
		} else
			return accept.type.equals(symbolType.type);
	}
	
	

	@Override
	public SymbolType visit(PrimaryExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return n.f0.accept(this, argu);
	}

	@Override
	public SymbolType visit(IntegerLiteral n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Integ();
	}

	@Override
	public SymbolType visit(TrueLiteral n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Boolean();
	}

	@Override
	public SymbolType visit(FalseLiteral n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return new Boolean();
	}

	@Override
	public SymbolType visit(Identifier n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.f0.toString());
		return new Identi(n.f0.toString());
	}

	@Override
	public SymbolType visit(ThisExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return currentClass;
	}

	@Override
	public SymbolType visit(ArrayAllocationExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		if (!typeCheck(n.f3.accept(this, argu), "integer")) {
			foundError = true;
		}
		return new Array();
	}

	@Override
	public SymbolType visit(AllocationExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// Set<Entry<String,Class>> hashSet=currentTable.symboltable.entrySet();
		// for(Entry entry:hashSet ) {
		//
		// System.out.println("Key="+entry.getKey()+", Value="+entry.getValue());
		// }
		// System.out.println("----------------------------------");
		// System.out.println(n.f1.f0.toString());
		// System.out.println((currentTable.symboltable.containsKey(n.f1.f0.toString())));
		//
		// System.out.println(n.toString());
		if (currentTable.symboltable.containsKey(n.f1.f0.toString())) {
			return currentTable.symboltable.get(n.f1.f0.toString());
		} else {
			foundError = true;
			return null;
		}

	}

	@Override
	public SymbolType visit(NotExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		SymbolType bool = n.f1.accept(this, argu);
		if (typeCheck(bool, "bool")) {
			return new Boolean();
		} else {
			foundError = true;
			return new Boolean();
		}
	}

	@Override
	public SymbolType visit(BracketExpression n, SymbolType argu) {
		// TODO Auto-generated method stub
		// System.out.println(n.toString());
		return n.f1.accept(this, argu);
	}

	public boolean typeCheck(SymbolType symbol, String type) {

		if (symbol == null || type == null) {
			return false;
		}
		if (symbol.type.equals(type)) {
			return true;
		}
		SymbolType typesym;
		if (symbol.type == "identi") { 
			typesym = getTypeMethod(((Identi) symbol).name);
			if (typesym != null) {
				if (typesym.type.equals(type)) {
					return true;
				}
			} else {

				return false;
			}
		}
		return false;
	}

	public void setSymbolTable(SymbolTable s) {
		// TODO Auto-generated method stub
		currentTable = s;

	}

}
