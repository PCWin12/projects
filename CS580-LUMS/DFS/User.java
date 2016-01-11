import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;

public class User {

	/**
	 * @param args
	 */
	transient boolean wait;
	DatagramSocket sender;
	DatagramPacket recPacket;
	String ownip;
	String ownport;
	String serverip;
	String serverport;
	byte[] recieving = new byte[1024];
	ArrayList<FD> files = new ArrayList<FD>();
	ArrayList<FD> pendingWrites = new ArrayList<FD>();

	private static Random rnd = new Random();

	public static String getRandomNumber(int digCount) {
		StringBuilder sb = new StringBuilder(digCount);
		for (int i = 0; i < digCount; i++)
			sb.append((char) ('0' + rnd.nextInt(10)));
		return sb.toString();
	}

	public User() throws IOException {
		sender = new DatagramSocket();
		recPacket = new DatagramPacket(recieving, recieving.length);
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		ownip = "localhost";
		ownport = getRandomNumber(4);
		System.out.println("Your Port: " + sender.getLocalPort());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			User u = new User();
			u.join("localhost", "10340");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void join(String host, String port) throws Exception {
		serverport = port;
		serverip = host;
		startReceivingThread();
		InetAddress IPAddress = InetAddress.getByName(host);
		// new MyProtocol().createPacket(magic, id, cmd, fd, mode, len,
		// filename, content)
		String menu = "------------DFS Menu-------------\n1-Create File\n2-Open File\n3-Read File\n4-Write File\n5-Close File\n6-Quit\n";
		boolean done = false;
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader read = new BufferedReader(in);
		String s = "";
		byte[] joi;
		DatagramPacket packet;
		byte[] recProt;
		MyProtocol mp;
		while (!done) {
			System.out.println(menu);
			s = read.readLine();
			int sel = -1;
			try {
				sel = Integer.valueOf(s);
			} catch (NumberFormatException e) {
				System.out.println("Invalid Selection");
				continue;
			}
			if (sel < 1 || sel > 6) {
				System.out.println("Invalid Selection");
				continue;
			}

			switch (sel) {
			case 1: // FILE CREATE
				print("Please Enter the FileName :(.dfs will be appended) \n");
				s = read.readLine();

				joi = new MyProtocol().createPacket(15440, 0, sel, 0, 0, 0, s);
				packet = new DatagramPacket(joi, joi.length, IPAddress,
						Integer.parseInt(port));
				sender.send(packet);
				

			//	recPacket = new DatagramPacket(recieving, recieving.length);
				//wait = true;
			//	sender.receive(recPacket);
			
				

				break;
			case 2: // /// FILE OPEN

				print("Please Enter the FileName : \n");
				s = read.readLine();
				
				print(s);
				print("Enter '1' for Write and '0' for Read :");
				String mode = read.readLine();
				int m = 0;
				try {
					m = Integer.valueOf(mode);
				} catch (NumberFormatException e) {
					print("Invalid Mode");
					continue;
				}
				joi = new MyProtocol().createPacket(15440, 0, sel, 0, m, 0, s);
				packet = new DatagramPacket(joi, joi.length, IPAddress,
						Integer.parseInt(port));
				sender.send(packet);

				//recPacket = new DatagramPacket(recieving, recieving.length);
			
				break;
			case 3: // //FILE READ
				print("Please Enter the FileName : \n");
				s = read.readLine() + ".dfs";
				print(s);
				// et(magic, id, cmd, fd, mode, len, filename, content)
				joi = new MyProtocol().createPacket(15440, 0, sel,
						s.hashCode(), 0, 0, s);
				packet = new DatagramPacket(joi, joi.length, IPAddress,
						Integer.parseInt(port));
				sender.send(packet);

				

				break;
			case 4: // WRITE FILE
				print("Please Enter the FileName : \n");
				s = read.readLine()+".dfs";
				print(s);
				print("Enter Mode: \n0-Blocking\n1-Non-Blocking\n2-Disconnected");
				String mod = read.readLine();
				print("Enter the Content to write in the file");
				String writecont = read.readLine();
				int m1 = 0;
				try {
					m1 = Integer.valueOf(mod);
				} catch (NumberFormatException e) {
					print("Invalid Mode");
					continue;
				}
				if (m1 == 2) {
						writeLocal(s.hashCode() , writecont);
						//joi = new MyProtocol().createPacket(15440, 0, sel,
							//	s.hashCode(), 0, 0, writecont);
					//	packet = new DatagramPacket(joi, joi.length, IPAddress,
						//		Integer.parseInt(port));
						//pendingWrites.add(packet);
						//pendingWrites(new FD())
				} else {
					// et(magic, id, cmd, fd, mode, len, filename, content)
					// mp.createPacket(magic, id, cmd, fd, mode, len, content)
					joi = new MyProtocol().createPacket(15440, 0, sel,
							s.hashCode(), m1, 0, writecont);
					packet = new DatagramPacket(joi, joi.length, IPAddress,
							Integer.parseInt(port));
					sender.send(packet);
					wait=true;
					print("Waiting for writes");
					/*while(wait){
						System.out.print("");
					}*/
				
				}

				break;
			case 5:
				print("Please Enter the FileName : \n");
				s = read.readLine()+".dfs";
				print(s);
				doPending(s.hashCode(),InetAddress.getByName(serverip)
						 , serverport);
				joi = new MyProtocol().createPacket(15440, 0, sel, s.hashCode(), 0, 0, s);
				packet = new DatagramPacket(joi, joi.length, IPAddress,
						Integer.parseInt(port));
				sender.send(packet);

				
				break;
			case 6:
				System.exit(0);
				break;
			default:
				continue;
				// break;
			}

		}

	}

	void doPending(int fd ,InetAddress IPAddress , String port ) throws Exception{
		for(int i=0 ; i<pendingWrites.size() ; i++){
			if(pendingWrites.get(i).fd == fd){
				byte []joi = new MyProtocol().createPacket(15440, 0, 4, fd, 0, 0, pendingWrites.get(i).content);
				DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
						Integer.parseInt(port));
				sender.send(packet);
			}
		}
		
		
	}
	void writeLocal(int fd , String con){
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).fd == fd && files.get(i).mode == 1) {
				FD temp = files.get(i);
				FD newf = new FD(temp.fd , temp.mode);
				newf.content = con;
				pendingWrites.add(newf);
				
				
				temp.content = temp.content+con;
				files.set(i, temp);
				
			}

		}
		//print("File Not Open or Not in Wrtie Mode or Does not Exist");
	}
	void setDataFile(String cn, int fd) {
		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).fd == fd && files.get(i).mode == 0) {
				FD temp = files.get(i);
				temp.content = cn;
				files.set(i, temp);
			}

		}
	}

	void timeoutCheck() {

		final long time = System.currentTimeMillis();
		new Thread() {
			public void run() {

				while (wait) {
					try {
						sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (System.currentTimeMillis() - time > 5000) {
						print("Time out\n !!Server not respnding!!");
						wait = false;
						System.exit(0);
					}
				}

			}
		}.start();
		return;
	}

	boolean isReadable(String filename) {
		for (FD f : files) {
			if (f.fd == filename.hashCode() && f.mode == 0) {
				return true;
			}

		}
		return false;

	}

	boolean isWritable(String filename) {
		for (FD f : files) {
			if (f.fd == filename.hashCode() && f.mode == 1) {
				return true;
			}

		}
		return false;

	}

	public void startReceivingThread() {
		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						// jobBreaker = false;
						recPacket = new DatagramPacket(recieving,
								recieving.length);

					
						sender.receive(recPacket);
						byte[] recProt = recPacket.getData();
						MyProtocol mp = new MyProtocol();
						mp.decodePacket(recProt);
						int command = mp._cmd;

						switch(command){
						case 1:
							if (mp._fd == 1) {
								print("File Created");

							} else {
								print("Filename Already Exist");
								// continue;
							}
							break;
						case 2:
							if (mp._fd == -2) {
								print("File Already Opened in Write Mode");

							} else if (mp._fd == -1) {
								print(" File Does Not Exist");
								// continue;
							} else {
								print("File Opened");
								files.add(new FD(mp._fd, mp._mode));
							}
							break;
						case 3:
							if (mp._fd == -2) {
								// if (isReadable(s) || isWritable(s)) {
								print("File Not OPened");
								// }

							} else if (mp._fd == -1) {
								print(" File Does Not Exist");
								// continue;
							} else {
								print("File Reading...\n Content : ");
								// files.add(new FD(mp._fd, m));
								setDataFile(mp.cn, mp._magic);
								print(mp.cn);
							}
							
							break;
						case 4:
							if (mp._fd == -2) {
								// if (isReadable(s) || isWritable(s)) {
								print("File Not OPened");
								// }

							} else if (mp._fd == -1) {
								print(" File Does Not Exist");
								// continue;
							} else {
								
								print("File Write Done");
							}
							wait=false;
							break;
						case 5:
							if (mp._fd == 1) {
								print("File Closed");
								
								
							} else {
								print("File not Exist or not Opened");
								// continue;
							}
								
							break;
						case 10:
							String cn = mp.cn;
							int fd = mp._fd;
							setDataFile(cn, fd);
							break;
						}

					//	System.out.println("New:\n" + mp._fd + "");
						// System.out.println(mp.cn);
						/*
						 * switch (command) {
						 * 
						 * case 2: enrollJob(mp); break;
						 * 
						 * case 0: ping(mp); break;
						 * 
						 * case 7: cancelJob(mp); break; default:
						 * System.out.println("Invalid Command from server!!");
						 * }
						 */
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();
	}

	void print(String str) {
		System.out.println(str+"\n");
	}
}
