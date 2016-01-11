import java.io.Serializable;
import java.util.ArrayList;


public class Request  implements Serializable{

	
	
	ArrayList<String> start = new ArrayList<String>();
	ArrayList<String> end  = new ArrayList<String>();
	int port;
	MyProtocol state;
	boolean status;
	int ID;
	boolean workstatus=false;
	byte[] hash;
	
}
