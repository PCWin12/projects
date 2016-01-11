import java.io.Serializable;


public class Work implements Serializable {

	
	
	int ID;
	MyProtocol state;
	boolean status   ;   //connected or not;
	int port;
	boolean working ;
	long lastping;
	int ack=0; //// 0 not sent , 1 job sent ack not rec , 2 ok 
	int ackNo=0;
	long ackTime;
	String end;
}


