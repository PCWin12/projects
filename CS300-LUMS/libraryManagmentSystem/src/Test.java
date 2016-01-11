import java.io.ObjectInputStream.GetField;



public class Test{
	public static void main(String args[]){
		
		/***************************Test 1***************************/
		
		System.out.println("Starting Test 1\n");
		Library lib =Library.getInstance("LUMS"); //create library instance (Note that when the system runs, the constructor of the Library class must automatically generate an admin type user with username admin and password admin, this user will login, and add other users to the system)
		
		lib.getLibraryStats(); //view the library system stats, currently there should be no users or resources in teh system
		
		Admin admin1 = (Admin) lib.users.get(0); //get a pointer for the automatically generated admin user
		
		System.out.println(admin1.addUser("peter", "asd", Constants.ADMIN)); //added a new admin, should print the unique id of the newly created user
		
		System.out.println(admin1.addUser("bill", "lums123", Constants.FACULTY)); //faculty type user
		System.out.println(admin1.addUser("alice", "lums456", Constants.FACULTY)); //faculty type user
		System.out.println(admin1.addUser("bob", "lums123", Constants.FACULTY)); //faculty type user
		admin1.login(); //login must fail, there cannot be two users logged in at the same time
		admin1.logout(); //logout is a method of the LibraryUser class
		
		Admin admin2 = (Admin) lib.users.get(1); // get the pointer to the user named peter
		admin2.login();
		
		System.out.println(admin2.addUser("carlos", "asdawe", Constants.STUDENT)); //create student type user
		System.out.println(admin2.addUser("gerard", "ghliel", Constants.STUDENT)); //create student type user
		System.out.println(admin2.addUser("william", "jmfjgl", Constants.STUDENT)); //create student type user
		System.out.println(admin2.addUser("john", "mkfiow", Constants.STUDENT)); //create student type user
		System.out.println(admin2.addUser("robert", "nnmmbjs", Constants.STUDENT)); //create student type user
		System.out.println(admin2.addUser("linda", "ujend", Constants.STUDENT)); //create student type user
		
		System.out.println(admin2.addResource("Thinking in C++", Constants.BOOK)); //add a book
		System.out.println(admin2.addResource("Thinking in C++", Constants.BOOK)); //add the same book again, should fail and print -1
		System.out.println(admin2.addResource("History of Science", Constants.BOOK)); //add a book
		System.out.println(admin2.addResource("Lord of the Rings", Constants.BOOK)); //add a book
		System.out.println(admin2.addResource("Harry Potter and the Deathly Hallows", Constants.BOOK)); //add a book
		System.out.println(admin2.addResource("Digital Fortress", Constants.BOOK)); //add a book
		System.out.println(admin2.addResource("Introduction to Algorithms", Constants.BOOK)); //add a book
		
		System.out.println(admin2.addResource("Introduction to Differential Equations", Constants.COURSE_PACK)); //add a course pack
		System.out.println(admin2.addResource("Iqbal's Urdu Poetry", Constants.COURSE_PACK)); //add a course pack
		System.out.println(admin2.addResource("Science and Civilization", Constants.COURSE_PACK)); //add a course pack
		
		System.out.println(admin2.addResource("Time", Constants.MAGAZINE)); //add a magazine
		
		lib.getLibraryStats(); //should print 12 users, 6 books, 3 course packs and 1 magazine. none issued
		admin2.logout();
		
		/***************************Test 2***************************/
		System.out.println("Starting Test 2\n");
		Faculty bill = (Faculty) lib.users.get(2); //get the pointer to the person named bill
		System.out.println(bill.login());
		
		Student carlos = (Student) lib.users.get(5); //get the handle to the student names carlos
		System.out.println(carlos.login()); //try logging in carlos, should fail, two persons cannot login at the same time
		
		System.out.println(bill.tryIssue(lib.resources.get(0).getResourceID())); //issue the book named Thinking in C++, should succeed
		bill.logout();
		
		/**************************Test 3******************************/
		System.out.println("Starting Test 3\n");
		System.out.println(carlos.login());
		System.out.println(bill.tryIssue(lib.resources.get(3).getResourceID())); //should fail, bill is a logged out user
		
		/**************************Test 4*******************************/
		System.out.println("Starting Test 4\n");
		System.out.println(carlos.tryIssue(lib.resources.get(1).getResourceID())); //issue the book named Hstory of science, should succeed
		System.out.println(carlos.tryIssue(lib.resources.get(0).getResourceID())); //issue the book Thinking in C++, should fail, request must be added to book's request list
		System.out.println(carlos.tryReturn(lib.resources.get(1).getResourceID())); //should succeed
		System.out.println(carlos.tryReturn(lib.resources.get(4).getResourceID()));  //should fail, havent issued this book
		carlos.logout();
		
		
		/***************************Test 5***********************************/
		System.out.println("Starting Test 5\n");
		System.out.println(bill.login()); //should fail, wrong password
		
		System.out.println(bill.login()); //should succeed
		System.out.println(carlos.tryReturn(lib.resources.get(4).getResourceID())); //retun the book Thinking in C++, carlos had made a request for this book, should automatically be issued to him
		bill.logout();
		System.out.println(carlos.login()); //login carlos
		carlos.viewIssued(); //Thinking in C++ should be in the list
		carlos.logout();
		
		System.out.println("Tests Complete\n");
		
		
		
		
	}
}