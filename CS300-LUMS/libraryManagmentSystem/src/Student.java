import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Student extends LibraryUser implements Borrower {

	ArrayList<LibraryResource> issuedResources = new ArrayList<LibraryResource>();
	ArrayList<LibraryResource> requestsMade = new ArrayList<LibraryResource>();

	public Student(String username, String _password) {
		super.userName = username;
		super.password = _password;
		super._type = Constants.STUDENT;
		Library lib = Library.getInstance("LUMS");

		super.userID = lib.users.size() + 1;

	}

	@Override
	public void viewFines() {
		// TODO Auto-generated method stub

		long fine = 0;
		for (LibraryResource temp : issuedResources) {

			if (temp.type == Constants.BOOK) {
				Book temp_Book = (Book) temp;
				String due = temp_Book.getReturnDate();
				long current = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date = new Date();
				try {
					date = sdf.parse(due);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (date.getTime() - current < 0) {
					fine = fine + ((current - date.getTime()) / 86400000) * 100;
				}
			} else {
				CoursePack temp_Book = (CoursePack) temp;
				String due = temp_Book.getReturnDate();
				long current = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date = new Date();
				try {
					date = sdf.parse(due);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (date.getTime() - current < 0) {
					fine = fine + ((current - date.getTime()) / 86400000) * 500;
				}
			}

		}
		Main.print("Total Fine : " + fine);
	}

	@Override
	public void viewRequests() {
		// TODO Auto-generated method stub
		Main.print("Reuests made by you");
		for (LibraryResource temp : requestsMade) {
			Main.print(temp.getResourceID() + "  " + temp.resourceName);
		}

	}

	@Override
	public void viewIssued() {

		// TODO Auto-generated method stub
		Main.print("Resources borrowded by you");
		String pr;
		for (LibraryResource temp : issuedResources) {

			if (temp.type == Constants.BOOK) {
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Type : Book";
			} else {
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Type : CoursePack";
			}
			Main.print(pr);
		}
	}

	@Override
	public void viewIssueDates() {
		// TODO Auto-generated method stub
		Main.print("Resources Issue Date");
		String pr;
		for (LibraryResource temp : issuedResources) {

			if (temp.type == Constants.BOOK) {
				Book temp_Book = (Book) temp;
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Issue Date : " + temp_Book.getIssueDate();
			} else {
				CoursePack temp_Course = (CoursePack) temp;
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Issue Date : " + temp_Course.getIssueDate();
			}
			Main.print(pr);

		}

	}

	@Override
	public void viewDueDates() {
		// TODO Auto-generated method stub
		Main.print("Resources Due Date");
		String pr;
		for (LibraryResource temp : issuedResources) {

			if (temp.type == Constants.BOOK) {
				Book temp_Book = (Book) temp;
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Due Date : " + temp_Book.getIssueDate();
			} else {
				CoursePack temp_Course = (CoursePack) temp;
				pr = temp.getResourceID() + "  " + temp.resourceName
						+ " Due Date : " + temp_Course.getIssueDate();
			}
			Main.print(pr);

		}
	}

	@Override
	public void viewOverdue() {
		// TODO Auto-generated method stub
		Main.print("Resources Overdue");

		for (LibraryResource temp : issuedResources) {

			if (temp.type == Constants.BOOK) {
				Book temp_Book = (Book) temp;
				String due = temp_Book.getReturnDate();
				long current = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date = new Date();
				try {
					date = sdf.parse(due);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (date.getTime() < current) {
					Main.print(temp.getResourceID() + "  " + temp.resourceName);
				}
			} else {
				CoursePack temp_Book = (CoursePack) temp;
				String due = temp_Book.getReturnDate();
				long current = System.currentTimeMillis();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				Date date = new Date();
				try {
					date = sdf.parse(due);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (date.getTime() < current) {
					Main.print(temp.getResourceID() + "  " + temp.resourceName);
				}
			}

		}

	}

	@Override
	public boolean tryIssue(int resourceID) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		ArrayList<LibraryResource> libR = lib.resources;
		int i = 0;
		for (LibraryResource temp : libR) {
			if (temp.getResourceID() == resourceID) {
				if (temp.type == Constants.BOOK) {
					Book tempB = (Book) temp;
					if (tempB.issueResource(super.userID)) {
						issuedResources.add(tempB);
						lib.resources.set(i, tempB);
						return true;
					}
				} else if (temp.type == Constants.COURSE_PACK) {
					CoursePack tempB = (CoursePack) temp;
					if (tempB.issueResource(super.userID)) {
						issuedResources.add(tempB);
						lib.resources.set(i, tempB);
						return true;
					}

				} else {
				}

			}
			i++;
		}
		Main.print("Resource not found");
		return false;
	}

	@Override
	public boolean tryReturn(int resourceID) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		ArrayList<LibraryResource> libR = issuedResources;
		int i=0;
		for (LibraryResource temp : libR) {
			if (temp.getResourceID() == resourceID) {
				if (temp.type == Constants.BOOK) {
					Book tempB = (Book) temp;
					if (tempB.returnResource()) {
						issuedResources.remove(findIndex(resourceID));
						int j=0;
						for (LibraryResource temp1 : lib.resources) {
							if (temp1.getResourceID() == resourceID) {
								lib.resources.set(j, tempB);	
							}
							j++;
						}
						
						return true;
					}
				} else if (temp.type == Constants.COURSE_PACK) {
					CoursePack tempB = (CoursePack) temp;
					if (tempB.issueResource(super.userID)) {
						issuedResources.remove(findIndex(resourceID));
						int j=0;
						for (LibraryResource temp1 : lib.resources) {
							if (temp1.getResourceID() == resourceID) {
								lib.resources.set(j, tempB);	
							}
							j++;
						}
						return true;
					}

				} else {
				}

			}
		i++;
		}
		Main.print("Resource not found!");
		return false;
	}

	int findIndex(int rid) {
		int i = 0;
		for (LibraryResource temp : issuedResources) {
			if (temp.getResourceID() == rid) {
				return i;
			}
			i++;
		}
		return -1;
	}

	int findRequestIndex(int rid) {
		int i = 0;
		for (LibraryResource temp : requestsMade) {
			if (temp.getResourceID() == rid) {
				return i;
			}
			i++;
		}
		return -1;
	}

	@Override
	public boolean withdrawRequest(int resourceID) {
	
		
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		ArrayList<LibraryResource> libR = lib.resources;
		int i = 0;
		for (LibraryResource temp : libR) {
			if (temp.getResourceID() == resourceID) {
				if (temp.type == Constants.BOOK) {
					Book tempB = (Book) temp;
					tempB.withdrawRequest(super.userID);
					lib.resources.set(i, tempB);
					requestsMade.remove(findRequestIndex(resourceID));
				} else if (temp.type == Constants.COURSE_PACK) {
					CoursePack tempB = (CoursePack) temp;
					tempB.withdrawRequest(super.userID);
					lib.resources.set(i, tempB);
					requestsMade.remove(findRequestIndex(resourceID));

				} else {
				}

			}
			i++;
		}
		Main.print("Resource not found");
		return false;
	}

	public void deleteAll() {

		for (LibraryResource libr : issuedResources) {
			tryReturn(libr.resourceID);
		}
		for (LibraryResource libr1 : requestsMade) {
			withdrawRequest(libr1.resourceID);
		}
	}

	public void addRequest(int id) {
		Library lib = Library.getInstance("LUMS");
		ArrayList<LibraryResource> libR = lib.resources;
		int i = -1;
		for (LibraryResource temp : libR) {
			i++;
			if (temp.getResourceID() == id) {
				if (temp.type == Constants.BOOK) {
					Book tempB = (Book) temp;
					if (tempB.checkStatus()) {
						tempB.addRequest(this);
						lib.resources.set(i, tempB);
					} else {
						Main.print("Book Available");
					}
					return;
				} else if (temp.type == Constants.COURSE_PACK) {
					CoursePack tempB = (CoursePack) temp;
					if (tempB.checkStatus()) {
						tempB.addRequest(this);
						lib.resources.set(i, tempB);

					} else {
						Main.print("CoursePack Available");
					}
					return;
				} else {
					Main.print("Magazine Can not be issued");
					return;
				}

			}
		}
		Main.print("Resource not found");

	}

}
