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
			w.join(args[0], args[1]);

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
		byte[] joi = new MyProtocol().createPacket(15440, 0, 1, "xxxxxx", "00"
				+ ownport, hexStringToByteArray("123456789123456789123456"));
		DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(port));
		sender.send(packet);

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
						// jobBreaker = false;
						recPacket = new DatagramPacket(recieving,
								recieving.length);

						reciever.receive(recPacket);
						byte[] recProt = recPacket.getData();
						MyProtocol mp = new MyProtocol();
						mp.decodePacket(recProt);
						int command = mp._cmd;
						System.out.println(command + "");
						switch (command) {

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

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}.start();
	}

	public void cancelJob(MyProtocol mp) {
		jobBreaker = false;
		System.out
				.println("Password Found by some Other Worker OR Job Cancelled OR Job Finished");
		currentState = new WorkerState();
	}

	public void enrollJob(MyProtocol mp) {
		System.out.println("Job Enrolling");
		jobBreaker = true;
		currentState.packetserver = mp;
		currentState.ID = mp._id;
		currentState.pass = mp._start;
		try {
			sendACKJob();
			startCracking();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startCracking() throws Exception {
		this.runit();
	}

	public void runit() {
		new Thread() {
			public void run() {
				System.out.println("Cracking Started");
				currentState.done = 6;
				String strt = currentState.packetserver._start;
				String end = currentState.packetserver._end;
				char[] charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
						.toCharArray();
				PassCeacker bf = new PassCeacker(charset, strt.toCharArray());

				String attempt = bf.toString();
				while (jobBreaker) {
					if (checkHash(attempt, currentState.packetserver._hash)) {
						System.out.println("Password Found: " + attempt);
						currentState.done = 5;
						found(attempt);

						break;

					} else if (attempt.equals(end)) {
						currentState.done = 4;
						doneNotFound();
						// return;
						break;
					}
					attempt = bf.toString();
					currentState.pass = attempt;
					// System.out.println("" + attempt);
					bf.increment();
				}
				/*
				 * if(jobBreaker){ //currentState.done = 5; }
				 */}
		}.start();

	}

	private boolean checkHash(String pass, byte[] _hash) {
		// TODO Auto-generated method stub
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update(pass.getBytes());

		byte byteData[] = md.digest();
		return md.isEqual(byteData, _hash);

		// return false;

	}

	public void found(String pass) {
		System.out.println("Sending Pass Word to Server");
		try {
			InetAddress IPAddress = InetAddress.getByName(serverip);
			byte[] joi;

			joi = new MyProtocol().createPacket(15440, currentState.ID, 5,
					currentState.pass, currentState.packetserver._end,
					currentState.packetserver._hash);

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, Integer.parseInt(serverport));
			sender.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doneNotFound() {
		try {
			InetAddress IPAddress = InetAddress.getByName(serverip);
			byte[] joi;
			joi = new MyProtocol().createPacket(15440, currentState.ID,
					currentState.done, currentState.packetserver._start,
					currentState.pass,
					hexStringToByteArray("123456789123456789123456"));

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, Integer.parseInt(serverport));
			sender.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendACKJob() throws Exception {
		System.out.println("Sending Ack to server");
		InetAddress IPAddress = InetAddress.getByName(serverip);
		byte[] joi = new MyProtocol()
				.createPacket(15440, currentState.ID, 3,
						currentState.packetserver._start,
						currentState.packetserver._end,
						currentState.packetserver._hash);
		DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
				Integer.parseInt(serverport));
		sender.send(packet);

	}

	public void ping(MyProtocol mp) {

		try {
			InetAddress IPAddress = InetAddress.getByName(serverip);
			byte[] joi;
			System.out.println(currentState.ID + "/" + currentState.done + "/"
					+ currentState.packetserver._start + "/"
					+ currentState.pass);

			joi = new MyProtocol().createPacket(15440, currentState.ID,
					currentState.done, currentState.packetserver._start,
					currentState.pass,
					hexStringToByteArray("123456789123456789123456"));

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, Integer.parseInt(serverport));
			sender.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		}
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
