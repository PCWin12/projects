import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class CoursePack extends LibraryResource implements Borrowable{

	
	
	boolean available = true;
	ArrayList<LibraryUser> requests = new ArrayList<LibraryUser>();
	String IssueDate;
	String returnDate;
	int issuer ;
	
	public void withdrawRequest(int user){
		LibraryUser temp ;
		for(int i=0 ; i<requests.size();i++){
			temp = requests.get(i);
			if(temp.getUserID()==user)
			{
				requests.remove(i);
				return;
			}
		}
	}
	
	public CoursePack(String CoursePackName, int id){
		super.resourceID = id;
		super.resourceName = CoursePackName;
		super.type = Constants.COURSE_PACK;
	
	}
	@Override
	public void viewRequests() {
		// TODO Auto-generated method stub
		Main.print("Requests made by: ");
		for( LibraryUser temp:requests){
			Main.print(temp.userName);
		}
		
	}
	public void addRequest(LibraryUser user)
	{
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
		if(available){
			available=!available;
			issuer = userID;
			Calendar currentDate = Calendar.getInstance();
			SimpleDateFormat formatter=  new SimpleDateFormat("MM/dd/yyyy");
			String dateNow = formatter.format(currentDate.getTime());
			setIssueDate(dateNow);
			Main.print("CoursePack Issued to you");
		}
		else
			Main.print("CoursePack is already issued");
		return !available;
	}

	@Override
	public boolean returnResource() {
		// TODO Auto-generated method stub
		return false;
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
		
			c.add(Calendar.DATE, 1);  // number of days to add
			return sdf.format(c.getTime());
	
		
	}

	
	
	
}
