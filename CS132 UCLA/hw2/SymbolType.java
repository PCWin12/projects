
import java.util.ArrayList;
import java.util.HashMap;

class SymbolType {

	public String type;
}

class Class extends SymbolType {

	public HashMap<String, SymbolType> vars = new HashMap<String, SymbolType>();
	public HashMap<String, Method> funcs = new HashMap<String, Method>();

	public Class() {
		type = "class";
	}

	public String name = null;
	public String parentClass;
	public boolean extended = false;

}

class Method extends SymbolType {
	public Method() {
		type = "method";
	}

	public String prentClass;
	public String name;

	public HashMap<String, SymbolType> args = new HashMap<String, SymbolType>();

	public HashMap<String, SymbolType> localvars = new HashMap<String, SymbolType>();
	public SymbolType retType;
	public ArrayList<SymbolType> types = new ArrayList<SymbolType>();
}

class Boolean extends SymbolType {
	public String name;

	public Boolean() {
		type = "bool";
	}
}

class Array extends SymbolType {
	public String name;

	public Array() {
		type = "array";
	}
}

class Integ extends SymbolType {
	public String name;

	public Integ() {
		type = "integer";
	}
}

class Identi extends SymbolType {
	public String name;

	public Identi() {
		type = "identi";
	}

	public Identi(String nam) {
		type = "identi";
		name = nam;
	}
}
