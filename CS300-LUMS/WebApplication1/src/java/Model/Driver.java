package Model;

import java.io.PrintWriter;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;

public class Driver {

//Driver drives the Library System. It gives IO interface to use and control Library!
    Scanner sc;					// 	to scan user input initialized in main function.
    public Library myLibrary;		//	Library instant initialized in main function.

    //Constructor for Driver
    public Driver(String libName) {
        //sc = new Scanner(System.in);
        myLibrary = Library.getInstance(libName);
    }

    /**
     * **************************************
     */
    /**
     * ****** USER Login IO Interface *******
     */
    /**
     * **************************************
     */
    public boolean loginIO(String name, String pass, int type, PrintWriter out) {
        LibraryUser user = myLibrary.findUser(name);
        // out.println("Checking data");
        //If user is not found, RETURN with message!
        if (user == null) {
            out.println("The username or password was not correct!\n");
            return false;
        } else if (!user.login(name, pass)) {
            out.println("The username or password was not correct!\n");

            //if user login fails
            return false;
        } //If some Faculty or Student logs in with correct username and password, and is not Admin,
        //Then, login should not be attempted!
        //Or everyone should adhere to their interface... No Sniffing :P
        else if (user.type != type) {
            out.println("The username or password was not correct!");
            return false;
        }


        out.println("You are logged in");

        /**
         * ******** The User Has Logged In ***************
         */
        boolean done = false;
        int input;
        String userInput;
        return true;

    }
}