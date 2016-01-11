import java.net.DatagramPacket;

public class Client {
	public int port;
	public int status;
	public int fno;
	public DatagramPacket dp;

	Client(int p, int st, int fn , DatagramPacket d) {
		port = p;
		status = st;
		fno = fn;
		d=dp;
	}
}
