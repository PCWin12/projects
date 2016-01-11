
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

class SymbolType {

	public String type;
}

class Class extends SymbolType {
	int sizebytes;
	public HashMap<String, Integer> funcs_pos = new HashMap<String, Integer>();
	public HashMap<String, Integer> vars_pos = new HashMap<String, Integer>();

	public HashMap<String, SymbolType> complete_vars;
	public HashMap<String, Method> complete_funcs;

	public HashMap<String, SymbolType> vars = new HashMap<String, SymbolType>();
	public HashMap<String, Method> funcs = new HashMap<String, Method>();

	public Class() {
		type = "class";
	}

	public String name = null;
	public String parentClass;
	public boolean extended = false;

	public HashMap<String, SymbolType> getcomplete_vars(
			Hashtable<String, Class> table, String cls) {
		if (complete_vars == null) {

			complete_vars = new HashMap<String, SymbolType>();

			if (parentClass != null) {
				HashMap<String, SymbolType> inherited = table.get(
						parentClass).getcomplete_vars(table,
						table.get(parentClass).name);

				complete_vars.putAll(inherited);
			}
			HashMap<String, SymbolType> temp = new HashMap<String, SymbolType>();

			for (Map.Entry<String, SymbolType> v : vars.entrySet()) {
				String key = v.getKey();
				temp.put(key, v.getValue());
			}
			complete_vars.putAll(temp);
			return complete_vars;
		} else {
			return complete_vars;
		}
	}

	public HashMap<String, Method> getcomplete_funcs(
			Hashtable<String, Class> table, String cls) {
		if (complete_funcs == null) {
			complete_funcs = new HashMap<String, Method>();
			HashMap<String, Method> localfunc = new HashMap<String, Method>();

			for (Map.Entry<String, Method> func : funcs.entrySet()) {
				String key = func.getKey();
				localfunc.put(key, func.getValue());
			
			}

			if (parentClass != null) {

				HashMap<String, Method> inheritedmtds = table.get(
						parentClass).getcomplete_funcs(table, parentClass);
				for (Map.Entry<String, Method> v : inheritedmtds.entrySet()) {
					if (funcs.containsKey(v.getKey())) {
						
						complete_funcs.put(v.getKey(), funcs.get(v.getKey()));
						inheritedmtds.remove(v.getKey());
					} else {
						complete_funcs.put(v.getKey(), v.getValue());
					}
				}
			}

			complete_funcs.putAll(localfunc);
			return complete_funcs;
		}

		else {
			return complete_funcs;

		}
	}

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

	public Array(String nam) {
		name = nam;
		type = "array";
	}

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
