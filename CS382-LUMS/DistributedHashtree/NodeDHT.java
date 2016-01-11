import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class NodeDHT {

	public String iP;
	public int port;
	public NodeDHT predecessor;
	public NodeDHT successor;
	public int Key;
	public ArrayList<File_Key> files = new ArrayList<File_Key>();

	public NodeDHT(String _ip, int _port) { // Function same as Create Node
											// **Create()**
		iP = _ip;
		port = _port;
		String temp = iP + port;
		Key = temp.hashCode();
		successor = this;
		predecessor = this;
	}

	// Function same as the **Join(n)**
	public void joinNode(String port, String IP, DatagramPacket pack)
			throws IOException {
		String keyS = IP + port;
		System.out.println(port);
		int port_in = Integer.parseInt(port);
		int hash = keyS.hashCode();
		if (predecessor.Key == this.Key) {

			NodeDHT newNode = new NodeDHT(IP, port_in);
			// newNode.Key = Integer.parseInt(key);
			newNode.predecessor = this;
			newNode.successor = this;
			this.predecessor = newNode;
			this.successor = newNode;
			DatagramSocket socket = new DatagramSocket();
			byte[] message = new byte[1024];
			message = ("Joined*" + newNode.predecessor.iP + "*"
					+ newNode.predecessor.port + "*" + newNode.successor.iP
					+ "*" + newNode.successor.port + "*rand").getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(IP), Integer.parseInt(port)));
			socket.close();
			printDetails();

			return;

		} else if (hash > Key && hash < successor.Key) {
			NodeDHT newNode = new NodeDHT(IP, Integer.parseInt(port));

			newNode.predecessor = this;
			newNode.successor = this.successor;
			notifyAlert(newNode, successor);
			this.successor = newNode;

			// newNode.startReceivingThread();

			DatagramSocket socket = new DatagramSocket();
			byte[] message = new byte[1024];
			message = ("Joined*" + newNode.predecessor.iP + "*"
					+ newNode.predecessor.port + "*" + newNode.successor.iP
					+ "*" + newNode.successor.port + "*rand").getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(IP), Integer.parseInt(port)));
			socket.close();
			printDetails();
		} else if (hash > this.Key && this.Key > successor.Key) {
			NodeDHT newNode = new NodeDHT(IP, Integer.parseInt(port));

			newNode.predecessor = this;
			newNode.successor = this.successor;
			notifyAlert(newNode, successor);
			this.successor = newNode;

			// newNode.startReceivingThread();

			DatagramSocket socket = new DatagramSocket();
			byte[] message = new byte[1024];
			message = ("Joined*" + newNode.predecessor.iP + "*"
					+ newNode.predecessor.port + "*" + newNode.successor.iP
					+ "*" + newNode.successor.port + "*rand").getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(IP), Integer.parseInt(port)));
			socket.close();
			printDetails();

		} else {

			pack.setAddress(InetAddress.getByName(successor.iP));
			pack.setPort(successor.port);
			DatagramSocket socket = new DatagramSocket();
			socket.send(pack);
			socket.close();
		}
	}

	public void printDetails() {
		System.out.println("IP : " + iP);
		System.out.println("Port : " + port);
		System.out.println("Key : " + Key);
		System.out.println("Predecessr IP : " + predecessor.iP);
		System.out.println("Predecessr port : " + predecessor.port);
		System.out.println("Successor IP : " + successor.iP);

		System.out.println("Successor port : " + successor.port);

	}

	public void startReceivingThread() {
		new Thread() {
			@Override
			public void run() {
				DatagramSocket reciever = null;
				try {
					reciever = new DatagramSocket(port);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] txt = new byte[1024];
				String message = "";
				while (!message.equals("bye")) {

					txt = new byte[1024];

					DatagramPacket packet = new DatagramPacket(txt, txt.length);

					try {
						reciever.receive(packet);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					message = new String(packet.getData());
					// System.out.println(message);
					StringTokenizer st = new StringTokenizer(message, "*");
					String cmd = st.nextToken("*");

					if (cmd.equalsIgnoreCase("JOIN")) {
						try {
							String ip_cmd = st.nextToken("*");
							String port_cmd = st.nextToken("*");
							// String Key_cmd = st.nextToken("*");
							joinNode(port_cmd, ip_cmd, packet);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (cmd.equalsIgnoreCase("NOTIFY")) {
						String ip_cmd = st.nextToken("*"); // these lines
						String port_cmd = st.nextToken("*");
						NodeDHT newNode = new NodeDHT(ip_cmd,
								Integer.parseInt(port_cmd));
						Notify(newNode);

					} else if (cmd.equalsIgnoreCase("joined")) {
						String pre_ip_cmd = st.nextToken("*");
						String pre_port_cmd = st.nextToken("*");
						String suc_ip_cmd = st.nextToken("*");
						String suc_port_cmd = st.nextToken("*");
						NodeDHT succ = new NodeDHT(suc_ip_cmd,
								Integer.parseInt(suc_port_cmd));
						NodeDHT pre = new NodeDHT(pre_ip_cmd,
								Integer.parseInt(pre_port_cmd));
						setSuccessor(succ);
						setPredecessor(pre);

					} else if (cmd.equalsIgnoreCase("LEAVEPRE")) {
						String pre_ip_cmd = st.nextToken("*");
						String pre_port_cmd = st.nextToken("*");
						NodeDHT pre = new NodeDHT(pre_ip_cmd,
								Integer.parseInt(pre_port_cmd));
						setPredecessor(pre);

					} else if (cmd.equalsIgnoreCase("LEAVESUC")) {
						String suc_ip_cmd = st.nextToken("*");
						String suc_port_cmd = st.nextToken("*");
						NodeDHT succ = new NodeDHT(suc_ip_cmd,
								Integer.parseInt(suc_port_cmd));
						setSuccessor(succ);

					} else if (cmd.equalsIgnoreCase("PUT")) {
						String ip_cmd = st.nextToken("*");
						String port_cmd = st.nextToken("*");
						String text = st.nextToken("*");
						put(ip_cmd, Integer.parseInt(port_cmd), text);
					} else if (cmd.equalsIgnoreCase("GET")) {
						String ip_cmd = st.nextToken("*");
						String port_cmd = st.nextToken("*");
						String text = st.nextToken("*");
						get(ip_cmd, Integer.parseInt(port_cmd),
								Integer.parseInt(text));
					} else if (cmd.equalsIgnoreCase("TRANSFER") ) {
						String count = st.nextToken("*");
						int num = Integer.parseInt(count);
						for (int i = 0; i < num; i++) {
							String temp = st.nextToken("*");
							files.add(new File_Key(temp.hashCode(), temp));
						}
					} else if (cmd.equalsIgnoreCase("RETURN")) {
						String ip_cmd = st.nextToken("*");
						String port_cmd = st.nextToken("*");
						returnFiles(ip_cmd, Integer.parseInt(port_cmd));
					}else if (cmd.equalsIgnoreCase("PICK") ) {
						String count = st.nextToken("*");
						int num = Integer.parseInt(count);
						for (int i = 0; i < num; i++) {
							String temp = st.nextToken("*");
							files.add(new File_Key(temp.hashCode(), temp));
						}
					}
					System.out.println(message);

				}
				reciever.close();
				System.out.println("Node disconnect");
				System.exit(0);
			}

		}.start();
	}

	// Function works same as the **NOTIYFY(n)**
	public void notifyAlert(NodeDHT node, NodeDHT succ) {

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String message = "NOTIFY*" + node.iP + "*" + node.port + "*" + node.Key
				+ "*";
		try {
			socket.send(new DatagramPacket(message.getBytes(), message
					.getBytes().length, InetAddress.getByName(succ.iP),
					succ.port));

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		socket.close();
	}

	public void get(String _ip, int _port, int key_text) {

		if ((predecessor.Key < key_text && key_text <= this.Key)
				|| (predecessor.Key < key_text && this.Key < predecessor.Key)) {
			for (File_Key file : files) {
				if (file.key == key_text) {
					DatagramSocket socket = null;
					try {
						socket = new DatagramSocket();
					} catch (SocketException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					byte[] message = new byte[1024];
					message = ("File Retrieved\n" + file.text).getBytes();
					try {
						socket.send(new DatagramPacket(message, message.length,
								InetAddress.getByName(_ip), _port));
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					socket.close();

				}
			}

		} else {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] message = new byte[1024];
			message = ("GET*" + _ip + "*" + _port + "*" + key_text + "*rand")
					.getBytes();
			try {
				socket.send(new DatagramPacket(message, message.length,
						InetAddress.getByName(successor.iP), successor.port));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.close();
		}
	}

	// Function to put Files
	public void put(String _ip, int _port, String text) {
		int key_text = text.hashCode();
		boolean test = (predecessor.Key < key_text && key_text <= this.Key)
				|| (predecessor.Key < key_text && this.Key < predecessor.Key);
		if ((predecessor.Key < key_text && key_text <= this.Key)) {

			this.files.add(new File_Key(key_text, text));
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] message = new byte[1024];
			message = ("File Inserted in the Chord").getBytes();
			try {
				socket.send(new DatagramPacket(message, message.length,
						InetAddress.getByName(_ip), _port));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.close();

		} else if ((predecessor.Key < key_text && this.Key < predecessor.Key)) {
			this.files.add(new File_Key(key_text, text));
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] message = new byte[1024];
			message = ("File Inserted in the Chord").getBytes();
			try {
				socket.send(new DatagramPacket(message, message.length,
						InetAddress.getByName(_ip), _port));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.close();

		} else {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket();
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			byte[] message = new byte[1024];
			message = ("PUT*" + _ip + "*" + _port + "*" + text + "*rand")
					.getBytes();
			try {
				socket.send(new DatagramPacket(message, message.length,
						InetAddress.getByName(successor.iP), successor.port));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socket.close();
		}
	}

	// Function to Implement **LEAVE()**
	public void leave() {
		if (successor.port == this.port && this.iP.equals(successor.iP)) {
			System.out.println("Left Chord");
			System.exit(0);
			return;
		}

		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] message = new byte[1024];
		message = ("LEAVEPRE*" + this.predecessor.iP + "*"
				+ this.predecessor.port + "*rand").getBytes();
		try {
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(successor.iP), successor.port));
			message = ("LEAVESUC*" + this.successor.iP + "*"
					+ this.successor.port + "*rand").getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(predecessor.iP), predecessor.port));
			String temp = "TRANSFER*" + files.size();
			for (File_Key file : files) {
				temp = temp + "*" + file.text;
			}
			temp = temp + "*rand";
			message = temp.getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(successor.iP), successor.port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();
		System.out.println("Left Chord");
		System.exit(0);

	}

	private void Notify(NodeDHT newNode) {
		this.predecessor = newNode;

	}

	public void setSuccessor(NodeDHT succ) {
		this.successor = succ;
	}

	public void setPredecessor(NodeDHT pre) {
		this.predecessor = pre;
	}

	public void returnFiles(String _ip, int _port) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] message = new byte[1024];
		try {
			int size =0;
			for (File_Key file : files) {
				if(file.key<(_ip+_port).hashCode())
				{
					size++;
				//	temp = temp + "*" + file.text;
				}
				}
			String temp = "PICK*" + size;
			for (File_Key file : files) {
				if(file.key<(_ip+_port).hashCode())
				{
					temp = temp + "*" + file.text;
				}
				}
			temp = temp + "*rand";
			message = temp.getBytes();
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(_ip), _port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();
	}

	public void stabilize() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] message = new byte[1024];
		message = ("RETURN*" + this.iP + "*" + this.port + "*rand").getBytes();
		try {
			socket.send(new DatagramPacket(message, message.length, InetAddress
					.getByName(successor.iP), successor.port));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		socket.close();

	}
}
