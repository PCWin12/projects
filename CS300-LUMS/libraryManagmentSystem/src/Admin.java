public class Admin extends LibraryUser implements AdminInterface {

	public Admin(String userName, String password, int type) {
		super.password = password;
		super.userName = userName;
		super._type = type;

		// userID
	}

	@Override
	public int addUser(String userName, String password, int type) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		if (type == Constants.STUDENT) {
			lib.users.add(new Student(userName, password));
			return 1;
		} else if (type == Constants.FACULTY) {
			lib.users.add(new Faculty(userName, password));
			return 1;
		} else if (type == Constants.ADMIN) {

			lib.users.add(new Admin(userName, password, type));
			return 1;
		} else {
		}

		return 0;
	}

	@Override
	public boolean removeUser(int userID) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		int i = 0;
		for (LibraryUser temp : lib.users) {
			if (temp.getUserID() == userID) {
				int type = lib.users.get(i)._type;
				if (type == Constants.STUDENT) {
					Student std = (Student) lib.users.get(i);
					std.deleteAll();
				} else if (type == Constants.FACULTY) {
					Faculty std = (Faculty) lib.users.get(i);
					std.deleteAll();

				} else {
				}
				lib.users.remove(i);
				return true;
			}
			i++;
		}
		Main.print("User Not Found!");
		return false;
	}

	@Override
	public int addResource(String name, int type) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		int id = lib.resources.size() + 1;
		for (LibraryResource libr : lib.resources) {
			if (libr.resourceName.equals(name)) {
				Main.print("Resource Name already exist");
				return -1;
			}
		}
		if (type == Constants.BOOK) {
			lib.resources.add(new Book(name, id));
			return 1;
		} else if (type == Constants.COURSE_PACK) {
			lib.resources.add(new CoursePack(name, id));
			return 1;
		} else if (type == Constants.MAGAZINE) {
			lib.resources.add(new Magazine(name, id));
			return 1;
		} else {

		}
		return id;
	}

	@Override
	public boolean removeResource(int resourceID) {
		// TODO Auto-generated method stub
		Library lib = Library.getInstance("LUMS");
		int i = 0;
		for (LibraryResource libr : lib.resources) {
			if (libr.getResourceID() == resourceID) {
				if (libr.type == Constants.BOOK) {
					Book temp_book = (Book) libr;
					if (!temp_book.checkStatus()) {
						lib.resources.remove(i);
						return true;
					} else {
						Main.print("Can not be removed. Resource issued");
					}
				} else if (libr.type == Constants.COURSE_PACK) {
					CoursePack temp_book = (CoursePack) libr;
					if (!temp_book.checkStatus()) {
						lib.resources.remove(i);
						return true;
					} else {
						Main.print("Can not be removed. Resource issued");
					}

				} else {
					lib.resources.remove(i);
					return true;

				}

			}
			i++;
		}
		Main.print("Resource not found");
		return false;
	}

}
