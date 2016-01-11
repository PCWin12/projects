
import syntaxtree.Node;

public class Typecheck {
   public static void main(String [] args) throws Exception {
      try {
        


      Node root = new MiniJavaParser(System.in).Goal();
  
          //Node root = new MiniJavaParser(System.in).Goal();
        // System.out.println("Program parsed successfully");
         SymbolTable s = new SymbolTable();
         SymbolTableGenerator sg  =  new SymbolTableGenerator();
         root.accept(sg, s);
         if(sg.foundError){
           System.out.println("Type error");
           System.exit(1);
          // return;
         }
         TypeChecker typeCheck = new TypeChecker();
         typeCheck.setSymbolTable(s);
         root.accept(typeCheck, s);
         if(typeCheck.foundError){
           System.out.println("Type error");
            System.exit(1);
         }else{
           System.out.println("Program type checked successfully");
         }
         //System.out.println("ok");
      }
      catch (Exception e) {
         System.out.println(e.toString());
      }
   }
}
