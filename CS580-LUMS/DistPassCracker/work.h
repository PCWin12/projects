#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
class Work{

public:
	int ID;
	message state;
	bool status   ;   //connected or not;
	bool working ;
	long lastping;
	int ack; //// 0 not sent , 1 job sent ack not rec , 2 ok 
	int ackNo;
	long ackTime;
	char end[KEYLEN];
	struct sockaddr_in temp_addr;
	Work(){
		ack=0;
		ackNo=0;
	}
};
