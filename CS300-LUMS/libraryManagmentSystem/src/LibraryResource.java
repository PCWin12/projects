/**
 * The is the parent class of all Library Resources
 */
public class LibraryResource {

	/**
	 * name of a library resource, must be unique
	 */
	String resourceName;
	public int type;
	/**
	 * unique id of a library resource
	 */
	protected int resourceID;

	/**
	 * 
	 * @return gives the unique id of the resource
	 */
	public int getResourceID() {
		return this.resourceID;
	}

	public String getResourceName() {
		return resourceName;
	}

	public String getResourceType() {
		if (type == Constants.BOOK)
			return "BOOK";
		else if (type == Constants.COURSE_PACK) {
			return "CoursePack";
		} else {
			return "Magazine";
		}

	}
}
