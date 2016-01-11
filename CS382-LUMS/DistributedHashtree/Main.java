import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static String Ip_DHT;
	public static int port_DHT;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Joining DHT");
		System.out.println("Enter  your IP");
		String ip = input.readLine();
		System.out.println("Enter your Port");
		String port = input.readLine();

		System.out.println("Enter the IP in DHT");
		String ip_dht = input.readLine();
		Ip_DHT = ip_dht;
		System.out.println("Enter the Port in DHT");
		String port_dht = input.readLine();
		port_DHT = Integer.parseInt(port_dht);
		NodeDHT myNode = new NodeDHT(ip, Integer.parseInt(port));
		myNode.startReceivingThread();
		DatagramSocket sender = new DatagramSocket();
		int track = 0;
		while (true) {
			System.out
					.println("Type from the Following :\nPress 1 to Join\nPress 2 to Leave\nPress 3 to Print\nPress 4 to Put\nPress 5 to Get\nPress 6 to Stabilize");
			String cmd = input.readLine();

			if (cmd.equalsIgnoreCase("1") && track == 0) {
				track = 1;
				InetAddress IPAddress = InetAddress.getByName(ip_dht);
				byte[] message = new byte[1024];
				String txt = "JOIN*" + ip + "*" + port + "*rand";
				System.out.println("JOIN*" + ip + "*" + port + "*rand");
				message = txt.getBytes();

				String key = ip + port;
				System.out.println("" + key.hashCode());
				DatagramPacket packet = new DatagramPacket(message,
						message.length, IPAddress, Integer.parseInt(port_dht));

				sender.send(packet);
			} else if (cmd.equalsIgnoreCase("3")) {
				myNode.printDetails();
			} else if (cmd.equalsIgnoreCase("2")) {
				if(track == 0){
					System.out.println("Please Join First");
					continue;
				}
				myNode.leave();
			} else if (cmd.equalsIgnoreCase("4")) {
				if(track == 0){
					System.out.println("Please Join First");
					continue;
				}
				System.out.println("Type the Text which you want to put in the Chord :");
				String text = input.readLine();
				System.out.println("The Key for the File is : \n"+text.hashCode());
				myNode.put(myNode.iP , myNode.port , text);
				
				
				
			} else if (cmd.equalsIgnoreCase("5")) {
				if(track == 0){
					System.out.println("Please Join First");
					continue;
				}
				System.out.println("Type the key you want to get File against :");
				String text = input.readLine();
				myNode.get(myNode.iP, myNode.port, Integer.parseInt(text));
			} else if (cmd.equalsIgnoreCase("6")) {
				if(track == 0){
					System.out.println("Please Join First");
					continue;
				}
				myNode.stabilize();
			}else if (cmd.equalsIgnoreCase("bye")) {
				break;
			}
		}

		sender.close();
		System.exit(0);
	}
}
