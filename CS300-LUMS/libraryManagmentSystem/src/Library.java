import java.util.ArrayList;

/**
 * This is class which holds all the information regarding the users, resources,
 * requests in it
 * 
 */
public class Library implements LibraryInfo {

	/**
	 * the main library object. There will be only one intantiation on this
	 * object.
	 */
	public boolean Login = false;

	public void setLogin(boolean temp) {
		Login = temp;
	}

	static Library lib;
	/**
	 * this will keep the record of all the resources (Books, CoursePacks,
	 * Magazines) in it
	 */
	public ArrayList<LibraryResource> resources;
	/**
	 * This will keep the record of all the library system users (Students,
	 * Faculty, Admin) in it
	 */
	public ArrayList<LibraryUser> users;

	/**
	 * The constructor of the Library Class
	 */
	private Library(String name) {

		/**
		 * Initiallize the resources and admin arrays
		 */
		resources = new ArrayList<LibraryResource>();
		users = new ArrayList<LibraryUser>();

		/**
		 * There is a default admin users which needs to be added to the system
		 * when the system first starts This admin user will login and add more
		 * users, resources to the system NOTE: Whenever a new user is
		 * initiallized, its pointer is added to the <b>user</b> array of the
		 * Library class to keep track of it your methods should add the users
		 * to this array whenever a new user is initiallized
		 */
		Admin admin = new Admin("admin", "admin", Constants.ADMIN);
		admin.userID = 0;
		users.add(admin); // add the new admin user to the list of the users

	}

	/**
	 * Get instance method. Makes sure there is only one instantiation of this
	 * class
	 * 
	 * @param the
	 *            name of the library
	 * @return returns the initiallized library object
	 */

	/**
	 * Singleton class necessary for one instance of Library..This concept was
	 * explained in the lecture.
	 */
	public static Library getInstance(String name) {
		if (lib == null) {
			lib = new Library(name);
		}
		return lib;
	}

	public String getLibraryName() {
		return "My Library";
	}

	public void getLibraryStats() {
	}

	public void printResources() {
		for (LibraryResource libr : resources) {
			Main.print(libr.getResourceID() + " " + libr.getResourceName()
					+ " " + libr.getResourceType());
		}
	}

	public void printUsers() {
		for (LibraryUser libr : users) {
			Main.print(libr.getUserID() + " " + libr.geyUserName() + " "
					+ libr.getUserType());
		}
	}

	public boolean loginCheck(String username, String password, int type) {
		for (LibraryUser lUser : users) {
			if (lUser.equals(username, password, type))
				return true;
		}
		return false;
	}
}
