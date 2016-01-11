import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Book extends LibraryResource implements Borrowable {

	boolean available = true;
	ArrayList<LibraryUser> requests = new ArrayList<LibraryUser>();
	String IssueDate;
	String returnDate;
	int issuer;

	public void withdrawRequest(int user) {
		LibraryUser temp;
		for (int i = 0; i < requests.size(); i++) {
			temp = requests.get(i);
			if (temp.getUserID() == user) {
				requests.remove(i);
				return;
			}
		}
	}

	public Book(String bookName, int id) {
		super.resourceID = id;
		super.resourceName = bookName;
		super.type = Constants.BOOK;
		
	}

	@Override
	public void viewRequests() {
		// TODO Auto-generated method stub
		Main.print("Requests made by: ");
		for (LibraryUser temp : requests) {
			Main.print(temp.userName);
		}

	}

	public void addRequest(LibraryUser user) {
		requests.add(user);
	}

	@Override
	public boolean checkStatus() {
		// TODO Auto-generated method stub
		return !available;
	}

	@Override
	public boolean issueResource(int userID) {
		// TODO Auto-generated method stub
		if (available) {
			available = !available;
			issuer = userID;
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			String dateNow = formatter.format(currentDate.getTime());
			setIssueDate(dateNow);
			Main.print("Book Issued to you");
		} else
			Main.print("Book is already issued");
		return !available;
	}

	@Override
	public boolean returnResource() {
		// TODO Auto-generated method stub
		available = true;
		issuer = 0;
		if (requests.size() > 0) {
			LibraryUser newuser = requests.remove(0);
			Library lib = Library.getInstance("LUMS");
			for (LibraryUser temp : lib.users) {
				if (temp.getUserID() == newuser.getUserID()) {
					if (temp._type == Constants.FACULTY) {
						Faculty std = (Faculty) temp;
						std.issuedResources.add(this);
						this.issueResource(temp.getUserID());
						temp = std;
					} else if (temp._type == Constants.STUDENT) {
						Student std = (Student) temp;
						std.issuedResources.add(this);
						this.issueResource(temp.getUserID());
						temp = std;
					}

				}
			}
		}
		return true;
	}

	@Override
	public void setIssueDate(String date) {
		IssueDate = date;
		// TODO Auto-generated method stub

	}

	@Override
	public String getIssueDate() {
		// TODO Auto-generated method stub
		return IssueDate;
	}

	@Override
	public String getReturnDate() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(IssueDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (issuer == Constants.FACULTY) {

			c.add(Calendar.DATE, 30); // number of days to add
			return sdf.format(c.getTime());

		} else {
			c.add(Calendar.DATE, 15); // number of days to add
			return sdf.format(c.getTime());
		}

	}

}
