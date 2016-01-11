#include "message.c"
#include "workerstate.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <iostream>
#include "passCracker.h"
#include <sys/time.h>
#include <stdio.h>
#include <unistd.h>
using namespace std;

#define SERVERPORT "10340"	// the port users will be connecting to
#define MAXBUFLEN 100

 
struct threadrec {
struct addrinfo *p ;
int sockfd ;
};
	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;
	int numbytes;
	pthread_t thread2 , thread1, thread3;
	int port;
	char * hash;
//============
	long requestSent = -1;
	long lastACK = -1;
	long lastping = -1;
	// ============

	int id;	
	
void *startPingThread(void *ptr);
void *startRecievingThread(void *ptr);
void *startPingServer(void *ptr);
void submitRequest(struct addrinfo *p , int sockfd );
void recieveACK(message mp);
	
	
long getTime(){
    struct timeval start;
    long seconds;    
   gettimeofday(&start, NULL);   
   return start.tv_sec;
}
int main(int argc, char *argv[]){
	if (argc != 4) {
		fprintf(stderr,"usage: request_client <server_hostname> <server_port> <hash>\n");
		exit(1);
	}

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;
	hash = argv[3];
	if ((rv = getaddrinfo(argv[1], argv[2], &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		//return 1;
		exit(1);
	}

	// loop through all the results and make a socket
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("talker: socket");
			continue;
		}

		break;
	}

	if (p == NULL) {
		fprintf(stderr, "talker: failed to bind socket\n");
		//return 2;
		exit(1);
	}
	
	submitRequest(p,sockfd);
	

}
void submitRequest(struct addrinfo *p , int sockfd ){
	cout<<"Submitting Request"<<endl;
	struct message m1;
	m1.Magic = 15440;
	m1.Client_ID = 0;
	m1.Command = HASH;
	strcpy(m1.Key_Range_Start, "xxxxxx");
	strcpy(m1.Key_Range_End, "xxxxxx");
	strcpy(m1.Hash_Val, hash);
//startRecievingThread();
	char buffer[sizeof(struct message)];
    memcpy(buffer, &m1, sizeof(struct message));
    sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen); 
//	pthread_t thread1;
	struct threadrec *t_r;
	t_r = (struct threadrec *)NULL;
	t_r = (struct threadrec *) malloc(sizeof(struct threadrec));
	t_r->p = p;
	t_r->sockfd = sockfd;
	cout<<".....Rec Thread Started"<<endl;

	pthread_create( &thread1, NULL, startRecievingThread, (void*)t_r) ;  
	requestSent = getTime();
	cout<<"Rec Thread Started"<<endl;
	pthread_join( thread1, NULL);
	//startRecievingThread();
}

void recieveACK(message mp) {
		lastACK = getTime();
		id = mp.Client_ID;
		cout<<"ACK for Job Recieved: | Hash= "<< mp.Hash_Val<<endl;
		lastping = getTime();
		pthread_create( &thread2, NULL, startPingServer, NULL) ;  
		pthread_create( &thread3, NULL, startPingThread, NULL) ;  
	}
void notDone(message mp) {
		lastping = getTime();
		cout<<"Server Still Working... "<<endl;

}
void doneFound(message mp) {
		lastping = getTime();
		cout<<"Password Found | Ans="<< mp.Key_Range_Start<<endl;
		exit(1);
}
void *startPingThread(void *ptr) {
		while (true) {
				
				if (getTime() - lastping > 15) {
					cout<<"TimeOut for Server\nTerminating...."<<endl;
					exit(1);
					break;
				}
				sleep(4);
				//System.out.println("Ping Server...");
	}
return NULL;
}
void *startPingServer(void *ptr) {
	cout<<"Start Ping Server"<<endl;
	struct message m1;
	m1.Magic = 15440;
	m1.Client_ID = id;
	m1.Command = PING;
	strcpy(m1.Key_Range_Start, "xxxxxx");
	strcpy(m1.Key_Range_End, "xxxxxx");
	strcpy(m1.Hash_Val, hash);
//startRecievingThread();
	char buffer[sizeof(struct message)];
    memcpy(buffer, &m1, sizeof(struct message));
   

	while (true) {
			sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen);
			sleep(3);
			cout<<"Ping Server..."<<endl;
	
	}
return ptr;
}

void *startRecievingThread(void *ptr){
cout<<"In Thread"<<endl;
struct threadrec *t = (struct threadrec *)ptr;
	char buf[sizeof(struct message)];
	while(true){

	  int n = recvfrom(t->sockfd, buf, MAXBUFLEN-1, 0, (t->p)->ai_addr, &(t->p)->ai_addrlen);
		if (n < 0)  {
		//error
		}
		else{
			message tmp;
			memcpy(&tmp, buf, sizeof(tmp));
			//cout<<"listener: "<<tmp.Command<<endl;
			//int command = tmp.Command;
					
			switch (tmp.Command) {

						case 3:
							cout<<"Ack recieved "<<endl;
							recieveACK(tmp);
							break;
						case 5:
							cout<<"Password Found"<<endl;
							doneFound(tmp);
							break;
						case 6:
							cout<<"Not Done"<<endl;
							notDone(tmp);
							break;
						default:
					//		System.out.println("Invalid Command from server!!");
							printf("Invalid Command \"%s\"\n", tmp.Command);
						
						}
	
	
	
		}
	  
	}
  
return ptr;
}
