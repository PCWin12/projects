import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.SocketException;

class Reciever {
	public static int port;

	public static void recieve() throws Exception {
		try {
			DatagramSocket reciever = new DatagramSocket(port);
			byte[] txt = new byte[1024];
			String message = "";
			while (!message.equals("bye")) {

				txt = new byte[1024];

				DatagramPacket packet = new DatagramPacket(txt, txt.length);

				reciever.receive(packet);

				message = new String(packet.getData());

				System.out.println("\nHim: " + message + "\n");

			}
			reciever.close();
			System.out.println("Chat Ended by the other user");
			System.exit(0);
		} catch (SocketException ex) {
			System.out.println("Port is not Free, Restart");
		}
		
	}

	Thread th;

	public void finishThread() {
		th.interrupt();
	}

	public void runReceivingThread() {
		th = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					recieve();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub

			}
		});
		th.start();
		// TODO Auto-generated method stub

	}
}
