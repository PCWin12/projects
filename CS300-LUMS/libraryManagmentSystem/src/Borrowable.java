
/**
 * This interface should be implemented by the Book and CoursePack classes
 * 
 */
public interface Borrowable {

    /**
     * should print the list of pending requests that have been made for the given resource
     */
    void viewRequests();

    /**
     * check if the given item is availab;e in the library
     * @return should return 0 if the resource is available, return 1 if issued (not available)
     */
    boolean checkStatus();

    /**
     * will issue the resource with the given ID to the user with the given ID
     * @param userID unique id of the user to whom we want to issue the resource
     * @return return true if successful else return false
     *
     */
    boolean issueResource(int userID); 

    /**
     * return the resource to the library
     * NOTE: if there are pending requests for this resource, automatically issue it to the first requester
     * @return return true if successful else return false
     */
    boolean returnResource();

    /**
     * set the issue date of the resource to the given date the date format is mm/dd/yyyy
     * @param date date in the mm/dd/yyyy format
     */
    void setIssueDate(String date);

    /**
     *
     * @return returns the issue date of the resource. returns empty string is not issued
     */
    String getIssueDate();

    /**
     *
     * @return returns the return date of the resource. returns empty string is not issued
     */
    String getReturnDate();
}
