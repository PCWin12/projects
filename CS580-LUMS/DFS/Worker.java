import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Worker {

	/**
	 * @param args
	 */
	DatagramSocket sender;
	DatagramPacket recPacket;
	String ownip;
	String ownport;
	String serverip;
	String serverport;
	byte[] recieving = new byte[1024];
	WorkerState currentState = new WorkerState();
	volatile boolean jobBreaker = true;

	public Worker() throws IOException {
		sender = new DatagramSocket();
		recPacket = new DatagramPacket(recieving, recieving.length);
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		ownip = "localhost";
		ownport = getRandomNumber(4);
		System.out.println("Your Port: " + ownport);
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
			Worker w = new Worker();
			w.join("localhost", "10340");

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
		//new MyProtocol().createPacket(magic, id, cmd, fd, mode, len, filename, content)
	
		String content = "okokokokokok";
		byte[] cn = new byte[16*1024];
		cn=content.getBytes();
		
		
		byte[] joi = new MyProtocol().createPacket(15440, 0, 0, 0,0,0,"okokok1");
		DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(port));
		sender.send(packet);

		
		
	 joi = new MyProtocol().createPacket(15440, 0, 1, 0,1,0,"okokok1");
		 packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(port));
		sender.send(packet);
		
		
		
		
		
		joi = new MyProtocol().createPacket(15440, 0, 3, 131650784,0,0,"Ali Gulzar okokokok ok ok ok o ko k ok");
	 packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(port));
		sender.send(packet);
		
		
		
		
		
	}

	public void startReceivingThread() {
		new Thread() {
			@Override
			public void run() {
				DatagramSocket reciever = null;
				try {
					reciever = new DatagramSocket();
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
						
						System.out.println("New:\n"+mp._fd + "");
						System.out.println(mp.cn);
					/*	switch (command) {

						case 2:
							enrollJob(mp);
							break;

						case 0:
							ping(mp);
							break;

						case 7:
							cancelJob(mp);
							break;
						default:
							System.out.println("Invalid Command from server!!");
						}
*/
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();
	}
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[16*1024];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

}
