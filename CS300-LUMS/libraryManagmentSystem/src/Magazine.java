/**
* This class is inherited from the LibraryResource class. Since a magazine <b>cannot</b> be issued, it needs not implement the Borrowable interface
*/

public class Magazine extends LibraryResource{

	
	
	public Magazine(String name, int id){
		super.resourceID = id;
		super.resourceName = name;
		super.type = Constants.MAGAZINE;
		
	}
}