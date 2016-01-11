

import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.FormalParameter;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MethodDeclaration;
import syntaxtree.Type;
import syntaxtree.VarDeclaration;
import visitor.GJDepthFirst;

public class SymbolTableGenerator extends GJDepthFirst<SymbolType, SymbolType> {

	Class currentClass;
	public boolean foundError = false;
	/**
	 * f0 -> MainClass() 
	 * f1 -> ( TypeDeclaration() )* 
	 * f2 -> <EOF>
	 */
	public SymbolType visit(Goal n, SymbolType argu) {
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		return null;
	}

	/**
	 * f0 -> "class" 
	 * f1 -> Identifier() 
	 * f2 -> "{" 
	 * f3 -> "public" 
	 * f4 -> "static"
	 * f5 -> "void" 
	 * f6 -> "main" 
	 * f7 -> "(" 
	 * f8 -> "String" 
	 * f9 -> "[" 
	 * f10 -> "]"
	 * f11 -> Identifier() 
	 * f12 -> ")" 
	 * f13 -> "{" 
	 * f14 -> PrintStatement() 
	 * f15 ->"}" 
	 * f16 -> "}"
	 */
	public SymbolType visit(MainClass n, SymbolType argu) {
		SymbolTable currenttable = (SymbolTable) argu;

		if (currenttable.symboltable.containsKey(n.f1.f0.toString())) {
			foundError = true;
		} else {

			Class clas = new Class();
			clas.name = n.f1.f0.toString();
			currentClass = clas;
			Method mtd = new Method();
			mtd.name = "main";
			mtd.prentClass = n.f1.f0.toString();

			clas.funcs.put("main", mtd);

			currenttable.symboltable.put(n.f1.f0.toString(), clas);

		}
		return null;
	}

	/**
	 * f0 -> "class" 
	 * f1 -> Identifier() 
	 * f2 -> "{" 
	 * f3 -> ( VarDeclaration() )* 
	 * f4-> ( MethodDeclaration() )* 
	 * f5 -> "}"
	 */

	int formethod;

	public SymbolType visit(ClassDeclaration n, SymbolType argu) {
		//System.out.println("In new Class");
		SymbolTable currenttable = (SymbolTable) argu;
		if (currenttable.symboltable.containsKey(n.f1.f0.toString())) {
			System.out.println("TypeError");
		} else {
			Class clas = new Class();
			clas.name = n.f1.f0.toString();
			formethod = 1;
			n.f4.accept(this, clas);
			formethod = 0;
			n.f3.accept(this, clas);
			clas.parentClass = null;
			currenttable.symboltable.put(n.f1.f0.toString(), clas);
			//System.out.println(n.f1.f0.toString());

		}
		return null;
	}

	/**
	 * f0 -> "class" 
	 * f1 -> Identifier() 
	 * f2 -> "extends" 
	 * f3 -> Identifier() 
	 * f4 ->"{" 
	 * f5 -> ( VarDeclaration() )* 
	 * f6 -> ( MethodDeclaration() )* 
	 * f7 -> "}"
	 */
	public SymbolType visit(ClassExtendsDeclaration n, SymbolType argu) {
		SymbolTable currenttable = (SymbolTable) argu;
		if (currenttable.symboltable.containsKey(n.f1.f0.toString())) {
			foundError = true;
		} else {

			Class clas = new Class();
			clas.name = n.f1.f0.toString();
			formethod = 1;
			n.f6.accept(this, clas);
			formethod = 0;
			n.f5.accept(this, clas);
			clas.extended = true;
			clas.parentClass = n.f3.f0.toString();
			currenttable.symboltable.put(n.f1.f0.toString(), clas);

		}
		return null;
	}

	/**
	 * f0 -> Type() 
	 * f1 -> Identifier() 
	 * f2 -> ";"
	 */
	public SymbolType visit(VarDeclaration n, SymbolType argu) {
		//System.out.println(n.f1.f0.toString());
		if (formethod == 1) {
			Method mtd = (Method) argu;
			if (mtd.localvars.containsKey(n.f1.f0.toString())
					|| mtd.args.containsKey(n.f1.f0.toString())) {
				foundError = true;
			} else {
				SymbolType type = n.f0.accept(this, argu);
				mtd.localvars.put(n.f1.f0.toString(), type);
				
			}
		} else {
			Class clas = (Class) argu;
			if (clas.vars.containsKey(n.f1.f0.toString())) {
				foundError = true;
			} else {
				SymbolType type = n.f0.accept(this, argu);
				clas.vars.put(n.f1.f0.toString(), type);
			}
		}
		return null;
	}

	/**
	 * f0 -> "public"
	 * f1 -> Type() 
	 * f2 -> Identifier() 
	 * f3 -> "(" 
	 * f4 -> (FormalParameterList() )? 
	 * f5 -> ")" 
	 * f6 -> "{" 
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )* 
	 * f9 -> "return" 
	 * f10 -> Expression() 
	 * f11 -> ";" 
	 * f12-> "}"
	 */
	public SymbolType visit(MethodDeclaration n, SymbolType argu) {
		Class clas = (Class) argu;
	//	System.out.println(n.f2.f0.toString());
	 
		if (clas.funcs.containsKey( n.f2.f0.toString())) {
			foundError = true;
		} else {
			Method mtd = new Method();
			mtd.prentClass = clas.name;
			mtd.retType = n.f1.accept(this, argu);
			n.f4.accept(this, mtd); 
			n.f7.accept(this, mtd);
			clas.funcs.put( n.f2.f0.toString(), mtd);
			
		}
		return null;
	}

	/**
	 * f0 -> Type() 
	 * f1 -> Identifier()
	 */
	public SymbolType visit(FormalParameter n, SymbolType argu) {
		//System.out.println(n.f1.f0.toString());
		SymbolType type = n.f0.accept(this, argu);
		Method mtd = (Method) argu;

	if (mtd.args.containsKey(n.f1.f0.toString())) {
			foundError = true;
		} else {
			mtd.args.put(n.f1.f0.toString(), type); 
			mtd.types.add(type);
		}
		return null;
	}

	/**
	 * f0 -> ArrayType() 
	 * | BooleanType() 
	 * | IntegerType() 
	 * | Identifier()
	 */
	public SymbolType visit(Type n, SymbolType argu) {
		return n.f0.accept(this, argu);
	}

	/**
	 * f0 -> "int" f1 -> "[" f2 -> "]"
	 */
	public SymbolType visit(ArrayType n, SymbolType argu) {
		return new Array();
	}

	/**
	 * f0 -> "boolean"
	 */
	public SymbolType visit(BooleanType n, SymbolType argu) {
		return  new Boolean();
	}

	/**
	 * f0 -> "int"
	 */
	public SymbolType visit(IntegerType n, SymbolType argu) {
		return new Integ();
	}

	/**
	 * f0 -> <IDENTIFIER>
	 */
	public SymbolType visit(Identifier n, SymbolType argu) {
		Identi temp = new Identi();
		temp.name = n.f0.toString();
		//System.out.println(temp.name);
		return temp;
	}

}
