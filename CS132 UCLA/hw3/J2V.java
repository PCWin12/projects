
import syntaxtree.Node;

public class J2V {
	public static void main(String[] args) throws Exception{
		try {
			Node root = new MiniJavaParser(System.in).Goal();
			SymbolTable s = new SymbolTable();
			SymbolTableGenerator sg = new SymbolTableGenerator();
			root.accept(sg, s);
			String head = sg.createHeader(s);
			VaporVisitor v = new VaporVisitor();
			v.setTable(s);
			System.out.println(head + "\n\n" + root.accept(v, 0));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
