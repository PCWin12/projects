

import java.util.Hashtable;

public class SymbolTable extends SymbolType{

	  public Hashtable<String, Class> symboltable = new Hashtable<String, Class>();
	  
	  public SymbolTable( Hashtable<String, Class> symbol){
		  symboltable = symbol;
		  
	  }

	public SymbolTable() {
		// TODO Auto-generated constructor stub
	}
}
