import java.io.*;
import java.net.*;

class Sender {
	public static void main(String args[]) throws Exception {

		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Enter Port Number to Send the Message: ");
		String port_temp = input.readLine();
		int port = Integer.parseInt(port_temp); // Port where the message will
												// be sent
		Reciever serverRecieve = new Reciever();
		System.out.println("Enter Port Number to Recieve the Message: ");
		port_temp = input.readLine();
		Reciever.port = Integer.parseInt(port_temp); // the Receiving port
		serverRecieve.runReceivingThread(); // Start the receiving thread
		System.out.println("Chat Started. Type 'bye' to Exit");
		while (true) {

			DatagramSocket sender = new DatagramSocket();

			InetAddress IPAddress = InetAddress.getByName("127.0.0.1");
			byte[] message = new byte[1024];
			String txt = input.readLine();
			System.out.print("\nMe : " + txt + "\n");
			message = txt.getBytes();
			
			DatagramPacket packet = new DatagramPacket(message, message.length,
					IPAddress, port);

			sender.send(packet);

			sender.close();
			if (txt.equals("bye")) {
				serverRecieve.finishThread();
				System.out.println("Chat Ended by you");
				System.exit(0);
				break;
				
			}
		}
		
	}

}
