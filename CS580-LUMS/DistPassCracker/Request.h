#include <vector>

class Request{
public:
	std::vector<char*> start;
	std::vector<char*> end;
	int port;
	message state;
	bool status;
	int ID;
	bool workstatus;
	char * hash;
	struct sockaddr_in temp_addr;
	Request(){
	workstatus  = false;
	}
	
};

