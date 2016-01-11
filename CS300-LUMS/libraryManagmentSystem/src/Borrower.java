
/**
 * The Borrower interface must be implemented by the Student and Faculty class
 * 
 */
public interface Borrower {

    /**
     * will print the fines due on the screen for the current user
     */
    void viewFines();

    /**
     * will print all the pending requests this user has made, the requests can be for multiple resources
     */
    void viewRequests();

    /**
     * should print on the screen the names and ID's of all the resources that this user has issued
     */
    void viewIssued();

    /**
     * view the issue dates for all the resources the current user has issued
     */
    void viewIssueDates();

    /**
     * view the due dates for all the resources the current user has issued
     * the due date must be 15 days after the issue date for the student and 30 days for the faculty
     */
    void viewDueDates();

    /**
     *  should print the names and ID's of all the issued resources by this user which are currenlty overdue (their return date has passed)
     */
    void viewOverdue();

    /**
     * will try to issue a resource with given resource id to the current user.
     * shouldnt make a request if the same person has issued the book
     * Remember: for checking availability, this function should call the checkAvailability() method in the Borrowable interface
     * for actually issueing the resource, this function will call the issueResource() method in the Borrowable interface
     * @param resourceID give in the resource id of the Book/ReadingPack which needs to be issued
     * @return if the resource is available, issue it to the user and return true, else make a request and return false
     */
    boolean tryIssue(int resourceID);

    /**
     *
     * @param resourceID give in the resource id of the Book/ReadingPack which needs to be returned
     * @return will try to return the resource with given ID, if successful, return 1, else return false
     * again, for checking if the resource is issued to this particular user, you will call the checkIssuer() method of borrowable interface
     * for returning the resource, you must call the returnResource() method of the Borrowable interface
     * also, while returning, check the fines if the item is returned after the due date
     */
    boolean tryReturn(int resourceID);

    /**
     *
     * @param resourceID the unique resourceID of the Book/CoursePack whose pending request we want to withdraw
     * @return if the requests exists, withdraw it and return 1, if request does not exist, return false
     */
    boolean withdrawRequest(int resourceID);
}
