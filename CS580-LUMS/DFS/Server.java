import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Serializable {
	// DatagramSocket sender;
	// DatagramPacket recPacket;
	String ownip;
	String ownport;
	String serverip;
	String serverport;
	byte[] recieving = new byte[1024];
	int IDcount;
	int currentState;
	long lastpingreuest;

	ArrayList<Client> clients = new ArrayList<Client>();
	ArrayList<String> files = new ArrayList<String>();
	ArrayList<Integer> openW = new ArrayList<Integer>();
	ArrayList<Integer> openR = new ArrayList<Integer>();

	// WorkerState currentState = new WorkerState();

	public Server() throws IOException {

		collectFiles();
		System.out.println("# of Files: " + files.size());
	}

	public void runServer(String port) throws SocketException {
		IDcount = 0;
		// recPacket = new DatagramPacket(recieving, recieving.length);
		ownip = "localhost";
		ownport = port;

		startReceivingThread();
		// startPingThread();

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			File file = new File("crashlog.s");
			Server sr = null;
			/*
			 * if (file.exists()) {
			 * System.out.println("Reading from Crash Log"); FileInputStream fio
			 * = new FileInputStream("crashlog.s"); ObjectInputStream reader =
			 * new ObjectInputStream(fio); sr = (Server) reader.readObject();
			 * reader.close(); fio.close(); sr.runServerFromObject(); } else {
			 */
			sr = new Server();

			sr.runServer("10340");

			// }

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startReceivingThread() {
		System.out.println("***Server Initiated***");
		new Thread() {
			@Override
			public void run() {
				DatagramSocket reciever = null;
				try {
					reciever = new DatagramSocket(Integer.parseInt(ownport));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DatagramPacket recPacket;

				while (true) {
					try {
						// writeObject();
						recieving = new byte[1024];
						recPacket = new DatagramPacket(recieving,
								recieving.length);

						reciever.receive(recPacket);
						byte[] recProt = recPacket.getData();
						MyProtocol mp = new MyProtocol();
						mp.decodePacket(recProt);
				
						int command = mp._cmd;
						System.out.println(command + "");
						switch (command) {
						case 1:
							createFile(mp, recPacket);
							break;
						case 2:
							openFile(mp, recPacket);
							break;
						case 3:
							readFile(mp, recPacket);
							break;
						case 4:
							writeFile(mp, recPacket);
							break;
						case 5:
							closeFile(mp, recPacket);
							break;

						default:
							System.out.println("Invalid Command from server!!");
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	protected void closeFile(MyProtocol mp, DatagramPacket cli)
			throws Exception {
		// TODO Auto-generated method stub
		int fd = mp._fd;
		mp._magic = fd;
		for (int c = 0; c < clients.size(); c++) {
			if (clients.get(c).fno == fd
					&& clients.get(c).port == cli.getPort()) {
				int stat = clients.get(c).status;
				clients.remove(c);
				removeFile(stat, fd);
				mp._fd = 1;
				DatagramSocket sender = new DatagramSocket();
				cli.setData(mp.createPacket());
				sender.send(cli);
				return;

			}
		}

		mp._fd = -1;
		DatagramSocket sender = new DatagramSocket();
		cli.setData(mp.createPacket());
		sender.send(cli);

	}

	void removeFile(int stat, int fd) {
		if (stat == 0) {
			for (int i = 0; i < openR.size(); i++) {
				if (openR.get(i) == fd) {
					openR.remove(i);
					return;
				}
			}
		} else {
			for (int i = 0; i < openW.size(); i++) {
				if (openW.get(i) == fd) {
					openW.remove(i);
					return;
				}
			}
		}
	}

	protected void writeFile(MyProtocol mp, DatagramPacket cli)
			throws Exception {
		// TODO Auto-generated method stub

		int fd = mp._fd;
		String filename = getFile(fd);

		if (filename == null) {
			// invalid fd
			mp._fd = -1;
		} else {
			if (validClientWrite(cli.getPort(), fd)) {
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(filename, true)));
				switch (mp._mode) {
				case 0:

					out.println(mp.cn.trim());
					out.close();

					sendToReaders(fd, mp);
					mp._fd = 1;
					break;
				case 1:
					out.println(mp.cn.trim());
					out.close();
					mp._fd = 1;
					DatagramSocket sender = new DatagramSocket();
					cli.setData(mp.createPacket());
					sender.send(cli);
					sendToReaders(fd, mp);

					return;
					// break;
				case 2:
					out.println(mp.cn.trim());
					out.close();

					sendToReaders(fd, mp);
					mp._fd = 1;
					break;
				default:
					System.out.println("Invalid Mode");
					mp._fd = -1;
					break;

				}

			} else {
				mp._fd = -2;
				// file not open for write or in Read Mode
			}

		}

		System.out.println(mp._fd);

		DatagramSocket sender = new DatagramSocket();
		cli.setData(mp.createPacket());
		sender.send(cli);

	}

	void sendToReaders(int fd, MyProtocol mp) throws Exception {
		String filename = getFile(fd);
		mp.cn = readFileAsString(filename);
		mp._cmd=10;
		for (Client c : clients) {
			if (c.status == 0 && c.fno == fd) { // inread mode
				DatagramSocket sender = new DatagramSocket();
				mp._fd = fd;
				c.dp.setData(mp.createPacket());
				sender.send(c.dp);
			}
		}
	}

	boolean validClientWrite(int port, int fd) {

		for (Client c : clients) {
			if (c.port == port && fd == c.fno && c.status == 1) {
				return true;
			}
		}
		return false;
	}

	protected void readFile(MyProtocol mp, DatagramPacket cli)
			throws IOException {
		// TODO Auto-generated method stub
		int fd = mp._fd;
		mp._magic = fd;
		String filename = getFile(fd);

		if (filename == null) {
			// invalid fd
			mp._fd = -1;
		} else {
			if (validClient(cli.getPort(), fd)) {
				String content = readFileAsString(filename);
				mp.cn = content;
				mp._fd = 1;
			} else {
				mp._fd = -2;
				// file not open
			}

		}
		DatagramSocket sender = new DatagramSocket();
		cli.setData(mp.createPacket());
		sender.send(cli);

	}

	private String readFileAsString(String filePath) throws IOException {
		StringBuffer fileData = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
		}
		reader.close();
		return fileData.toString();
	}

	boolean validClient(int port, int fd) {

		for (Client c : clients) {
			if (c.port == port && fd == c.fno) {
				return true;
			}
		}
		return false;
	}

	String getFile(int hash) {
		for (String file : files) {
			if (file.hashCode() == hash) {
				return file;
			}

		}
		return null;
	}

	protected void openFile(MyProtocol mp, DatagramPacket cli) throws Exception {
		// TODO Auto-generated method stub
		String filename = mp.cn.trim() + ".dfs";
		System.out.println("**"+filename+"**");
		int mode = mp._mode; // 0 Read 1 Write

		DatagramSocket sender = new DatagramSocket();
		if (fileExist(filename)) {
			System.out.println("File Exist : " + filename);

			if (isOpen(filename)) {
				mp._fd = -2;
			} else {
				mp._fd = filename.hashCode();
				clients.add(new Client(cli.getPort(), mode, mp._fd, cli));
				if (mode == 1) {
					openW.add(filename.hashCode());
				} else {
					openR.add(filename.hashCode());
				}
			}

		} else {
			mp._fd = -1;
		}

		cli.setData(mp.createPacket());
		sender.send(cli);

	}

	boolean isOpen(String filename) {
		for (int file : openW) {
			if (file == filename.hashCode()) {
				return true;

			}

		}
		return false;
	}

	protected void createFile(MyProtocol mp, DatagramPacket cli)
			throws Exception {
		// TODO Auto-generated method stub
		String filename = mp.cn.trim() + ".dfs";
		System.out.println("**"+filename+"**");
	//	System.out.println(cli.getPort());

		DatagramSocket sender = new DatagramSocket();

		if (fileExist(filename)) {
			System.out.println("File Already Exist : " + filename);
			mp._fd = -1;

		} else {
			try {
				PrintWriter writer = new PrintWriter(filename, "UTF-8");

				writer.close();
				// System.out.println("File Already Exist : "+filename);
				mp._fd = 1;
				files.add(filename);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		cli.setData(mp.createPacket());
		sender.send(cli);

	}

	boolean fileExist(String filename) {
		for (String file : files) {
			if (file.equalsIgnoreCase(filename)) {
				return true;
			}

		}
		return false;
	}

	void collectFiles() {
		String path = ".";

		String files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {

			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				if (files.endsWith(".dfs") || files.endsWith(".DFS")) {
					System.out.println(files);
					this.files.add(files);
				}
			}
		}

	}

	public String byte2String(DataInputStream is, int len) {
		char[] chr = new char[len];
		for (int i = 0; i < len; i++) {
			try {
				chr[i] = is.readChar();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		}
		return String.valueOf(chr);

	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
