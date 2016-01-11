import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class RequestClient {

	private DatagramSocket sender;
	private DatagramPacket recPacket;
	private byte[] recieving = new byte[1024];
	private String serverport;
	private String serverip;
	private String ownport;

	// ============
	long requestSent = -1;
	long lastACK = -1;
	long lastping = -1;
	// ============

	int id;

	public RequestClient() throws SocketException {
		sender = new DatagramSocket();
		recPacket = new DatagramPacket(recieving, recieving.length);
		ownport = getRandomNumber(4);
	}

	private static Random rnd = new Random();

	public static String getRandomNumber(int digCount) {
		StringBuilder sb = new StringBuilder(digCount);
		for (int i = 0; i < digCount; i++)
			sb.append((char) ('0' + rnd.nextInt(10)));
		return sb.toString();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			RequestClient rc = new RequestClient();
			rc.submitRequest(args[0], args[1], args[2]);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startReceivingThread() {
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

				while (true) {
					try {

						recPacket = new DatagramPacket(recieving,
								recieving.length);

						reciever.receive(recPacket);
						byte[] recProt = recPacket.getData();
						MyProtocol mp = new MyProtocol();
						mp.decodePacket(recProt);
						int command = mp._cmd;
						System.out.println(command + "");
						switch (command) {

						case 3:
							recieveACK(mp);
							break;
						case 5:
							doneFound(mp);
							break;
						case 6:
							notDone(mp);
							break;

						case 7:
							// cancelJob(mp);
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

	public void recieveACK(MyProtocol mp) {
		lastACK = System.currentTimeMillis();
		id = mp._id;
		System.out.println("ACK for Job Recieved: | Hash=" + mp._hash);
		lastping = System.currentTimeMillis();
		startPingServer(Integer.parseInt(serverport));
		startPingThread();
	}

	public void submitRequest(String host, String port, String hash)
			throws Exception {
		byte[] has = hexStringToByteArray(hash);
		serverport = port;
		serverip = host;
		startReceivingThread();
		InetAddress IPAddress = InetAddress.getByName(host);
		byte[] joi = new MyProtocol().createPacket(15440, 0, 8, "xxxxxx", "00"
				+ ownport, has);
		DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(port));
		sender.send(packet);
		requestSent = System.currentTimeMillis();
		

	}

	public void startPingServer(final int port) {

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						
						// for(int i=0 ; i<workers.size();i++){
						InetAddress IPAddress = InetAddress.getByName(serverip);
						byte[] joi = new MyProtocol().createPacket(15440, id,
								0, "xxxxxx", "xxxxxx",
								hexStringToByteArray("123456789123456789123456"));
						DatagramPacket packet = new DatagramPacket(joi,
								joi.length, IPAddress, port);
						sender.send(packet);
						// }

						Thread.sleep(3000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void startPingThread() {
		// System.out.println("***Server Initiated***");
		new Thread() {
			@Override
			public void run() {
				while (true) {
					
					if (System.currentTimeMillis() - lastping > 15000) {
						System.out.println("TimeOut for Server\nTerminating");
						System.exit(0);
						break;
					}
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Ping Server...");
				}
			}
		}.start();
	}

	public void notDone(MyProtocol mp) {
		lastping = System.currentTimeMillis();
		System.out.println("Server Still Working... ");

	}

	public void doneFound(MyProtocol mp) {
		lastping = System.currentTimeMillis();
		System.out.println("Password Found | Ans=" + mp._start);
		System.exit(0);
	}
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
