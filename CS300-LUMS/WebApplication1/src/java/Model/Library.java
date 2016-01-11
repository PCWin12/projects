package Model;

import controller.DbConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is class which holds all the information regarding the users, resources,
 * requests in it
 *
 */
public class Library implements LibraryInfo {

    String libraryName;
    static Calendar calendar = new GregorianCalendar();
    /**
     * the main library object. There will be only one intantiation on this
     * object.
     */
    static Library lib;
    // The IDs for resources ... They are kept unique and accessed using Library class
    static int nextResID = 1001;
    static int nextUserID = 14100180;
    static int nextFineID = 0;		//next fine ID... check  void updateFines() function
    Set<Fine> toBeFined;	//  keeps record of fines which are to be checked by the library
    //	They are related to those resources only which are ISSUED to users...
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
        calendar = Calendar.getInstance();
        this.libraryName = name;
        toBeFined = new HashSet<Fine>();

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
          }

    /**
     * Get instance method. Makes sure there is only one instantiation of this
     * class
     *
     * @param the name of the library
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
        return this.libraryName;
    }

   
    /**
     * ****************************************
     */
    /**
     * ***Library users search functions*******
     */
    /**
     * ****************************************
     */
    LibraryUser findUser(String name) {
        System.out.println("Infunction");
        try {
            Connection con = new DbConnection().getConnection();
            // Result set get the result of the SQL query
            ResultSet rs = null;
            try {
                rs = con.createStatement().executeQuery("SELECT * FROM library_user where username='" + name+"'");
            
            if (rs.first()) {
                 System.out.println("Infunction Yes");
                LibraryUser u1 = new LibraryUser();
                u1.type = rs.getInt("type");

                u1.userName = rs.getString("username");

                u1.password = rs.getString("password");
                u1.userID = rs.getInt("uid");
                rs.close();
                con.close();
                return u1;
            }
            rs.close();
                con.close();
            //Checks all the users by their userNames
//            for (int i = 0; i < this.users.size(); i++) {
//                if (users.get(i).userName.equals(name)) {
//                    return users.get(i);
//                }
//            }

        } catch (SQLException ex) {
            Logger.getLogger(Library.class.getName()).log(Level.SEVERE, null, ex);
        }} catch (Exception e) {
            }

        return null;

    }

    @Override
    public void getLibraryStats() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
