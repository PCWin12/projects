import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	public static void main(String[] arg0) throws IOException {
		int CurrentUserType = 0;
		Library Lums = Library.getInstance("LUMS");
		boolean Done = true;
		while (Done) {
			System.out
					.println("*Welcome to Library Management System*\n Select from the following options:\n"
							+ "- For viewing system stats press 1\n"
							+ "- For logging into the system as administrator, press 2\n"
							+ "- For faculty login press 3\n"
							+ "- For student login press 4\n>>");

			InputStreamReader in = new InputStreamReader(System.in);
			BufferedReader read = new BufferedReader(in);
			String s = "";

			s = read.readLine();

			if (s.equals("2")) {
				boolean temp = false;
				String uname = "";
				// if(CurrentUserType != Constants.ADMIN){
				System.out.println("Enter UserName:");
				uname = read.readLine();
				System.out.println("Enter Password:");
				String password = read.readLine();
				Admin admin = new Admin(uname, password, Constants.ADMIN);
				temp = admin.login();
				// }
				boolean tempDone = true;
				if (temp) {
					// || CurrentUserType == Constants.ADMIN ) {
					CurrentUserType = Constants.ADMIN;
					while (tempDone) {
						System.out
								.println("Welcome <"
										+ uname
										+ ">\nChoose from the following options:\n- For adding an administrator account to the system, press 1"
										+ "\n- For adding a faculty account to the system, press 2"
										+ "\n- For adding a student account to the system, press 3"
										+ "\n- For adding a book to the system, press 4"
										+ "\n- For adding a course pack to the system, press 5"
										+ "\n- For adding a magazine to the system, press 6"
										+ "\n- For removing a user account from the system, press 7"
										+ "\n- For removing a resource from the system,press 8"
										+ "\n- For logging out of the system, press 9 ");
						String inputAdmin = read.readLine();

						try {
							int inputINT = Integer.parseInt(inputAdmin);
							String username_temp, password_temp, name_temp;
							switch (inputINT) {
							case 1:
								print("Adding Admin Account\nEnter Username:");
								username_temp = read.readLine();
								print("Enter Password:");
								password_temp = read.readLine();
								admin.addUser(username_temp, password_temp,
										Constants.ADMIN);
								break;
							case 2:
								print("Adding Faculty Account\nEnter Username:");
								username_temp = read.readLine();
								print("Enter Password:");
								password_temp = read.readLine();
								admin.addUser(username_temp, password_temp,
										Constants.FACULTY);
								break;
							case 3:
								print("Adding Student Account\nEnter Username:");
								username_temp = read.readLine();
								print("Enter Password:");
								password_temp = read.readLine();
								admin.addUser(username_temp, password_temp,
										Constants.STUDENT);
								break;
							case 4:
								print("Adding Book...\nEnter Name:");
								name_temp = read.readLine();
								admin.addResource(name_temp, Constants.BOOK);
								break;
							case 5:
								print("Adding CoursePack...\nEnter Name:");
								name_temp = read.readLine();
								admin.addResource(name_temp,
										Constants.COURSE_PACK);
								break;
							case 6:
								print("Adding Magazine...\nEnter Name:");
								name_temp = read.readLine();
								admin.addResource(name_temp, Constants.MAGAZINE);
								break;
							case 7:
								print("Removing User Account...Select from following:");
								for (LibraryUser libu : Lums.users) {
									Main.print(libu.getUserID() + "");
								}
								name_temp = read.readLine();
								admin.removeUser(Integer.parseInt(name_temp));
								break;
							case 8:
								print("Removing Resources...Select from following:");
								for (LibraryResource libu : Lums.resources) {
									Main.print(libu.getResourceID() + "");
								}
								name_temp = read.readLine();
								admin.removeResource(Integer
										.parseInt(name_temp));
								break;
							case 9:
								tempDone = false;
								admin.logout();
								break;
							default:
								System.out.println("Selection does not Exist");
								break;

							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid Input");
						}
					}
				} else {
					System.out.println("Wrong UserName/Password");
				}

			} else if (s.equals("4")) {
				System.out.println("Enter UserName:");
				String uname = read.readLine();
				System.out.println("Enter Password:");
				String password = read.readLine();
				Student std1 = new Student(uname, password);
				if (std1.login()) {
					int stdID = -1;
					int i=0;
					for (LibraryUser libu : Lums.users) {
						if (libu.equals(uname, password, Constants.STUDENT)) {
							std1 = (Student) libu;
							System.out.println("Logged In");
							stdID = i;
						}
						i++;
					}

					// ===========================================================
					boolean tempDone = true;
					CurrentUserType = Constants.STUDENT;
					while (tempDone) {
						Main.print("Welcome <" + uname + ">"
								+ "\nChoose from the following options:"
								+ "\n- For borrowing a resource, press 1"
								+ "\n- For returning a resource, press 2"
								+ "\n- For reuesting a resource, press 3"
								+ "\n- For viewing issued books, press 4"
								+ "\n- For viewing pending requests, press 5"
								+ "\n- For viewing you fines, press 6"
								+ "\n- For deleting a request, press 7"
								+ "\n- For logging out of the system, press 8");
						String inputStd = read.readLine();

						try {
							int inputINT = Integer.parseInt(inputStd);
							String input_temp;
							switch (inputINT) {
							case 1:
								print("Borrowing Resource...Select from Following");
								Lums.printResources();
								input_temp = read.readLine();
								std1.tryIssue(Integer.parseInt(input_temp));
								Lums.users.set(stdID, std1);
								break;
							case 2:
								print("Returning Resource...Select from Following");
								std1.viewIssued();
								input_temp = read.readLine();
								std1.tryReturn(Integer.parseInt(input_temp));
								Lums.users.set(stdID, std1);
								break;
							case 3:
								print("Requesting Resource...Select from Following");
								Lums.printResources();
								input_temp = read.readLine();
								std1.addRequest(Integer.parseInt(input_temp));
								Lums.users.set(stdID, std1);
								break;
							case 4:
								print("Showing Issued Books...");
								std1.viewIssued();	
								break;
							case 5:
								print("Showing Pending Requests...");
								std1.viewRequests();
								break;
							case 6:
								print("Showing Fines...");
								std1.viewFines();
								break;
							case 7:
								print("Deleting Requests...Select from following");
								std1.viewRequests();
								input_temp = read.readLine();
								std1.withdrawRequest(Integer.parseInt(input_temp));
								Lums.users.set(stdID, std1);
								break;
							case 8:
								tempDone = false;
								std1.logout();
								break;
							default:
								System.out.println("Selection does not Exist");
								break;

							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid Input");
						}
					}
					// ================================================

				}

			} else if (s.equals("3")) {
				System.out.println("Enter UserName:");
				String uname = read.readLine();
				System.out.println("Enter Password:");
				String password = read.readLine();
				Faculty fac1 = new Faculty(uname, password);
				if (fac1.login()) {
					int stdID = -1;
					int i=0;
					for (LibraryUser libu : Lums.users) {
						if (libu.equals(uname, password, Constants.FACULTY)) {
							fac1 = (Faculty) libu;
							System.out.println("Logged In");
							stdID = i;
						}
						i++;
					}

					// ===========================================================
					boolean tempDone = true;
					CurrentUserType = Constants.FACULTY;
					while (tempDone) {
						Main.print("Welcome <" + uname + ">"
								+ "\nChoose from the following options:"
								+ "\n- For borrowing a resource, press 1"
								+ "\n- For returning a resource, press 2"
								+ "\n- For reuesting a resource, press 3"
								+ "\n- For viewing issued books, press 4"
								+ "\n- For viewing pending requests, press 5"
								+ "\n- For viewing you fines, press 6"
								+ "\n- For deleting a request, press 7"
								+ "\n- For logging out of the system, press 8");
						String inputStd = read.readLine();

						try {
							int inputINT = Integer.parseInt(inputStd);
							String input_temp;
							switch (inputINT) {
							case 1:
								print("Borrowing Resource...Select from Following");
								Lums.printResources();
								input_temp = read.readLine();
								fac1.tryIssue(Integer.parseInt(input_temp));
								Lums.users.set(stdID, fac1);
								break;
							case 2:
								print("Returning Resource...Select from Following");
								fac1.viewIssued();
								input_temp = read.readLine();
								fac1.tryReturn(Integer.parseInt(input_temp));
								Lums.users.set(stdID, fac1);
								break;
							case 3:
								print("Requesting Resource...Select from Following");
								Lums.printResources();
								input_temp = read.readLine();
								fac1.addRequest(Integer.parseInt(input_temp));
								Lums.users.set(stdID, fac1);
								break;
							case 4:
								print("Showing Issued Books...");
								fac1.viewIssued();	
								break;
							case 5:
								print("Showing Pending Requests...");
								fac1.viewRequests();
								break;
							case 6:
								print("Showing Fines...");
								fac1.viewFines();
								break;
							case 7:
								print("Deleting Requests...Select from following");
								fac1.viewRequests();
								input_temp = read.readLine();
								fac1.withdrawRequest(Integer.parseInt(input_temp));
								Lums.users.set(stdID, fac1);
								break;
							case 8:
								tempDone = false;
								fac1.logout();
								break;
							default:
								System.out.println("Selection does not Exist");
								break;

							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid Input");
						}
					}
					// ================================================

				}

			}
		}
	}

	public static void print(String p) {
		System.out.println(p);
	}
}
