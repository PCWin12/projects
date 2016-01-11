/**
 * This is the parent class of all the library users
 */
public class LibraryUser {

	/**
	 * The name of the user, must be unique
	 */

	String userName;
	/**
	 * password
	 */
	String password;
	/**
	 * The unique id of the user
	 */
	int userID;
	public int _type;
	public boolean loggedIn = false;

	public String geyUserName() {
		return userName;
	}

	public String getUserType() {
		if (_type == Constants.ADMIN)
			return "Admin";
		else if (_type == Constants.STUDENT) {
			return "Student";
		} else {
			return "Faculty";
		}

	}

	/**
	 * get the id of the current user
	 * 
	 * @return return the userID variable of this class
	 */
	int getUserID() {
		return userID;
	}

	/**
	 * 
	 * @param userName
	 *            the string username
	 * @param password
	 *            the string password
	 * @return returns true if the login is successful else returns false
	 */
	boolean login() {
		/**
		 * write your code here
		 */

		Library temp = Library.getInstance("LUMS");
		if (!temp.Login) {
			if (temp.loginCheck(this.userName, this.password, this._type)) {
				temp.setLogin(true);
				loggedIn = true;
				return true;
			}

		} else {
			Main.print("Only One User can login at a time");
			return false;
		}

		return false;
	}

	/**
	 * logs out the currently logged in user. Note that the system cannot have
	 * multiple users logged in at the same time
	 * 
	 * @return return true if logout successful else return false
	 */
	boolean logout() {
		Library temp = Library.getInstance("LUMS");
		if (loggedIn ) {
			temp.setLogin(false);
			loggedIn = false;
			return true;
		}
		/**
		 * 
		 * Write your code here
		 */
		Main.print("User not Logged In");
		return false;
	}

	public boolean equals(String user_Name, String pass, int typ) {
		if (user_Name.equals(userName) && password.equals(pass)
				&& typ == this._type) {
			return true;
		} else
			return false;
	}
}