import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Serializable {
	// DatagramSocket sender;
	// DatagramPacket recPacket;
	String ownip;
	String ownport;
	String serverip;
	String serverport;
	Request currentReq = new Request();
	byte[] recieving = new byte[1024];
	int IDcount;
	int currentState;
	long lastpingreuest;

	ArrayList<Work> workers = new ArrayList<Work>();
	ArrayList<Request> requesters = new ArrayList<Request>();

	// WorkerState currentState = new WorkerState();

	public Server() throws IOException {

	}

	public void runServer(String port) throws SocketException {
		IDcount = 0;
		// recPacket = new DatagramPacket(recieving, recieving.length);
		ownip = "localhost";
		ownport = port;

		startReceivingThread();
		startPingThread();

	}

	public void runServerFromObject() throws SocketException {
		/*
		 * IDcount = 0; //recPacket = new DatagramPacket(recieving,
		 * recieving.length); ownip = "localhost"; ownport = port;
		 */
		for (int i = 0; i < workers.size(); i++) {
			workers.get(i).lastping = System.currentTimeMillis();
			startPingWorker(workers.get(i).ID, workers.get(i).port);
		}
		lastpingreuest = System.currentTimeMillis();
		startReceivingThread();
		startPingThread();

	}

	public void writeObject() throws IOException {
		// File file = new File("crashlog.s");
		FileOutputStream fo = new FileOutputStream("crashlog.s");

		ObjectOutputStream out = new ObjectOutputStream(fo);
		out.writeObject(this);
		out.close();
		fo.close();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			File file = new File("crashlog.s");
			Server sr = null;
			if (file.exists()) {
				System.out.println("Reading from Crash Log");
				FileInputStream fio = new FileInputStream("crashlog.s");
				ObjectInputStream reader = new ObjectInputStream(fio);
				sr = (Server) reader.readObject();
				reader.close();
				fio.close();
				sr.runServerFromObject();
			} else {
				sr = new Server();
				sr.runServer(args[0]);

			}

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
						writeObject();
						recPacket = new DatagramPacket(recieving,
								recieving.length);

						reciever.receive(recPacket);
						byte[] recProt = recPacket.getData();
						MyProtocol mp = new MyProtocol();
						mp.decodePacket(recProt);
						int command = mp._cmd;
						System.out.println(command + "");
						switch (command) {
						case 0:
							recievePing(mp);
							break;
						case 1:
							acceptWorker(mp);
							break;
						case 3:
							recieveACK(mp);
							break;
						case 4:
							doneNotFound(mp);
							break;
						case 5:
							doneFound(mp);
							break;
						case 6:
							notDone(mp);
							break;
						case 7:
							cancelJob(mp);
							break;
						case 8:
							crackRequest(mp);
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

	public void startPingThread() {
		// System.out.println("***Server Initiated***");
		new Thread() {
			@Override
			public void run() {
				while (true) {
					for (int i = 0; i < workers.size(); i++) {
						if (System.currentTimeMillis()
								- workers.get(i).lastping > 3000
								&& workers.get(i).working == true) {
							System.out.println("TimeOut for Worker ID="
									+ workers.get(i).ID);

							currentReq.start.add(0, workers.get(i).state._end);
							currentReq.end.add(0, workers.get(i).end);
							workers.remove(i);
							break;
						}
						if (workers.get(i).ack == 1
								&& System.currentTimeMillis()
										- workers.get(i).ackTime > 5000) {
							workers.get(i).ackNo++;
							resendJob(workers.get(i));
							if (workers.get(i).ackNo > 3) {
								workers.remove(i);
								break;
							}

						}
					}
					if (System.currentTimeMillis() - lastpingreuest > 15000
							&& currentReq.workstatus) {
						System.out.println("Request Client Timeout");
						cancelAll();
						currentReq.workstatus = false;
						lastpingreuest = System.currentTimeMillis();
						if (requesters.size() > 0) {
							System.out.println("Queued Request started");
							currentReq = requesters.get(0);
							currentReq.workstatus = false;
							crackRequest(requesters.get(0).state);
							requesters.remove(0);
						}
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.print("");
				}
			}
		}.start();
	}

	public void startPingWorker(final int id, final int port) {

		new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						// for(int i=0 ; i<workers.size();i++){
						InetAddress IPAddress = InetAddress.getByName(ownip);
						byte[] joi = new MyProtocol()
								.createPacket(
										15440,
										id,
										0,
										"xxxxxx",
										"xxxxxx",
										hexStringToByteArray("123456789123456789123456"));
						DatagramPacket packet = new DatagramPacket(joi,
								joi.length, IPAddress, port);
						DatagramSocket sender = new DatagramSocket();
						sender.send(packet);
						// }

						Thread.sleep(2500);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void cancelJob(MyProtocol mp) {
		File file = new File("MyObject.sr");
		file.delete();
		cancelAll();
	}

	public void recieveACK(MyProtocol mp) {
		int id = mp._id;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).ID == id) {
				workers.get(i).lastping = System.currentTimeMillis();
				workers.get(i).ack = 2;
				System.out.println("ACK for Job Recieved: | From Worker=" + id
						+ " | start=" + mp._start + " | end=" + mp._end
						+ " | hash=" + mp._hash);
				startPingWorker(workers.get(i).ID, workers.get(i).port);

			}
		}

	}

	public void crackRequest(MyProtocol mp) {
		if (!currentReq.workstatus) {
			lastpingreuest = System.currentTimeMillis();
			Request req = new Request();
			req.workstatus = true;
			req.ID = IDcount;
			IDcount++;
			req.hash = mp._hash;
			req.state = mp;
			req.port = Integer.valueOf(mp._end);
			req.status = true;
			//requesters.add(req);
			currentReq = req;
			currentReq.start.clear();
			currentReq.end.clear();

			String sample = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

			for (int i = 0; i < 61; i++) {
				String st = "", end = "";
				for (int j = 0; j < 6; j++) {
					st = st + String.valueOf(sample.charAt((i)));
					end = end + String.valueOf(sample.charAt((i + 1)));
				}
				//System.out.println(st + "/" + end);
				currentReq.start.add(st);
				currentReq.end.add(end);

			}

			try {
				distributeRequest();
				requestACK();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Request Already Exist: Queueing");
			Request r = new Request();
			r.state = mp;
			requesters.add(r);

		}
	}

	public void distributeRequest() throws Exception {
		int totalworkers = workers.size();

		currentState = 6;
		for (int i = 0; i < totalworkers; i++) {
			String st = currentReq.start.remove(0);
			String end = currentReq.end.remove(0);
			System.out.println(st + " To " + end);
			InetAddress IPAddress = InetAddress.getByName(ownip);
			MyProtocol mp = new MyProtocol();
			byte[] joi = mp.createPacket(15440, workers.get(i).ID, 2, st, end,
					currentReq.hash);
			mp.decodePacket(joi);
			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, workers.get(i).port);
			workers.get(i).state = mp;
			workers.get(i).ack = 1;
			workers.get(i).working = true;
			workers.get(i).ackNo = 1;
			workers.get(i).end = end;
			workers.get(i).ackTime = System.currentTimeMillis();
			DatagramSocket sender = new DatagramSocket();
			sender.send(packet);
			System.out.println("Distributed and sent");

		}
		/*
		 * if (totalworkers < 1) { System.out.println("Zero Workers"); return; }
		 * currentState = 6; int div = 61 / totalworkers; String sample =
		 * "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; for
		 * (int i = 0; i < totalworkers; i++) { String st = ""; String end = "";
		 * 
		 * for (int j = 0; j < 6; j++) { st = st +
		 * String.valueOf(sample.charAt(div * (i))); end = end +
		 * String.valueOf(sample.charAt(div * (i + 1))); }
		 * 
		 * System.out.println(st + " To " + end); InetAddress IPAddress =
		 * InetAddress.getByName(ownip); MyProtocol mp = new MyProtocol();
		 * byte[] joi = mp.createPacket(15440, workers.get(i).ID, 2, st, end,
		 * currentReq.hash); mp.decodePacket(joi); DatagramPacket packet = new
		 * DatagramPacket(joi, joi.length, IPAddress, workers.get(i).port);
		 * workers.get(i).state = mp; workers.get(i).ack = 1;
		 * workers.get(i).working = true; workers.get(i).ackNo = 1;
		 * workers.get(i).ackTime = System.currentTimeMillis(); DatagramSocket
		 * sender = new DatagramSocket(); sender.send(packet);
		 * System.out.println("Distributed and sent"); }
		 */
	}

	public void resendJob(Work w) {
		try {
			InetAddress IPAddress = InetAddress.getByName(ownip);

			byte[] joi;

			joi = w.state.createPacket(15440, w.state._id, w.state._cmd,
					w.state._start, w.state._end, w.state._hash);

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, w.port);
			w.ackTime = System.currentTimeMillis();
			DatagramSocket sender = new DatagramSocket();
			sender.send(packet);
			System.out.println(" Job sent Again");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void acceptWorker(MyProtocol mp) {
		Work w = new Work();
		w.ID = IDcount;
		IDcount++;
		w.status = true;
		w.port = Integer.valueOf(mp._end);
		w.state = mp;
		w.working = false;
		workers.add(w);
		System.out.println("Worker Added | " + "ID=" + w.ID + " | Port="
				+ w.port);
		updateRequest();
	}

	public void updateRequest() {

		if (currentReq.workstatus) {
			realocate();
		}

	}

	public void realocate() {
		if (currentReq.start.size() > 0) {
			try {
				String st = currentReq.start.remove(0);
				String end = currentReq.end.remove(0);
				System.out.println(st + " To " + end);
				InetAddress IPAddress;

				IPAddress = InetAddress.getByName(ownip);

				MyProtocol mp = new MyProtocol();
				byte[] joi = mp.createPacket(15440,
						workers.get(workers.size() - 1).ID, 2, st, end,
						currentReq.hash);
				mp.decodePacket(joi);
				DatagramPacket packet = new DatagramPacket(joi, joi.length,
						IPAddress, workers.get(workers.size() - 1).port);
				workers.get(workers.size() - 1).state = mp;
				workers.get(workers.size() - 1).ack = 1;
				workers.get(workers.size() - 1).working = true;
				workers.get(workers.size() - 1).ackNo = 1;
				workers.get(workers.size() - 1).ackTime = System
						.currentTimeMillis();
				workers.get(workers.size() - 1).end = end;

				DatagramSocket sender = new DatagramSocket();
				sender.send(packet);
				System.out.println("Distributed and sent");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void requestACK() throws Exception {
		InetAddress IPAddress = InetAddress.getByName(ownip);
		byte[] joi = new MyProtocol().createPacket(15440, currentReq.ID, 3,
				"xxxxxx", "xxxxxx",
				hexStringToByteArray("123456789123456789123456"));
		DatagramPacket packet = new DatagramPacket(joi, joi.length, IPAddress,
				currentReq.port);
		DatagramSocket sender = new DatagramSocket();
		sender.send(packet);

	}

	public void recievePing(MyProtocol mp) {
		lastpingreuest = System.currentTimeMillis();
		try {
			InetAddress IPAddress = InetAddress.getByName(ownip);
			byte[] joi;

			joi = new MyProtocol().createPacket(15440, currentReq.ID,
					currentState, "xxxxxx", "xxxxxx",
					hexStringToByteArray("123456789123456789123456"));

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, currentReq.port);
			DatagramSocket sender = new DatagramSocket();
			sender.send(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doneNotFound(MyProtocol mp) {
		int id = mp._id;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).ID == id) {
				workers.get(i).state = mp;
				workers.get(i).lastping = System.currentTimeMillis();
				workers.get(i).ack = 0;

				workers.get(i).working = false;
				// workers.remove(i);
				if (!currentReq.workstatus) {
					System.out.println("Ping From Worker: " + id);
				} else {
					System.out.println("Done Not Found From Worker: " + mp._id);
				}
				break;
			}
		}

	}

	public void doneFound(MyProtocol mp) {
		int id = mp._id;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).ID == id) {
				workers.get(i).state = mp;
				String passWord = mp._start;
				workers.get(i).lastping = System.currentTimeMillis();

				workers.get(i).working = false;
				// workers.remove(i);
				currentState = 5;
				workers.get(i).ack = 0;
				if (currentReq.workstatus) {

					cancelAll();
					notifyRequest(mp);
					System.out.println("Done AND Found From Worker: " + mp._id
							+ " | Password=" + mp._start);
				} else {

					System.out.println("Ping From Worker: " + id);
				}
				break;
			}
		}

	}

	public void cancelAll() {

		for (int i = 0; i < workers.size(); i++) {
			try {
				InetAddress IPAddress = InetAddress.getByName(ownip);
				byte[] joi;

				joi = new MyProtocol().createPacket(15440, workers.get(i).ID,
						7, "xxxxxx", "xxxxxx",
						hexStringToByteArray("123456789123456789123456"));
				MyProtocol mp = new MyProtocol();
				mp.decodePacket(joi);
				workers.get(i).state = mp;
				workers.get(i).lastping = System.currentTimeMillis();
				workers.get(i).ack = 0;
				workers.get(i).working = false;

				DatagramPacket packet = new DatagramPacket(joi, joi.length,
						IPAddress, workers.get(i).port);
				DatagramSocket sender = new DatagramSocket();
				sender.send(packet);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void notifyRequest(MyProtocol mp) {
		try {
			InetAddress IPAddress = InetAddress.getByName(ownip);
			byte[] joi;

			joi = new MyProtocol().createPacket(15440, currentReq.ID, 5,
					mp._start, "xxxxxx",
					hexStringToByteArray("123456789123456789123456"));

			DatagramPacket packet = new DatagramPacket(joi, joi.length,
					IPAddress, currentReq.port);
			DatagramSocket sender = new DatagramSocket();
			sender.send(packet);
			currentReq.workstatus = false;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void notDone(MyProtocol mp) {
		int id = mp._id;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.get(i).ID == id) {
				workers.get(i).state = mp;
				workers.get(i).lastping = System.currentTimeMillis();
				if (currentReq.workstatus) {
					System.out.println("Not Done From Worker: " + mp._id
							+ " | LastPass=" + mp._end);
				} else {
					System.out.println("Ping From Worker: " + id);
				}
				break;
			}
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
