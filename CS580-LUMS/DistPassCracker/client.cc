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
using namespace std;

#define SERVERPORT "10340"	// the port users will be connecting to
#define MAXBUFLEN 100

void *startRecievingThread(void *ptr);
void join(struct addrinfo *p , int sockfd );
 bool checkHash(char *pass, char *hash);
 void sendACKJob();
 void enrollJob(message m);
 void *startCracking(void *ptr);
 
 
struct threadrec {
struct addrinfo *p ;
int sockfd ;
};

	volatile bool jobBreaker;
	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;
	int numbytes;
	pthread_t thread2 , thread1;
	char *serverport;
	char *serverhost;
	


	WorkerState *currentstate = new WorkerState();
int main(int argc, char *argv[]){
	jobBreaker = true;

	if (argc != 3) {
		fprintf(stderr,"usage: worker_client <hostname> <port>\n");
		exit(1);
	}
	serverport = argv[2];
	serverhost = argv[1];
	

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;

	if ((rv = getaddrinfo(argv[1], argv[2], &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		//return 1;
		exit(1);
	}

	// loop through all the results and make a socket
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			//perror("talker: socket");
			continue;
		}

		break;
	}

	if (p == NULL) {
		fprintf(stderr, "worker: failed to bind socket\n");
		//return 2;
		exit(1);
	}
	
	join(p,sockfd);
	
return 0;
}
void join(struct addrinfo *p , int sockfd ){
	cout<<"Joining"<<endl;
	struct message m1;
	m1.Magic = 15440;
	m1.Client_ID = 0;
	m1.Command = REQUEST_TO_JOIN;
	strcpy(m1.Key_Range_Start, "aaaa");
	strcpy(m1.Key_Range_End, "9999");
	strcpy(m1.Hash_Val, crypt(m1.Key_Range_Start, "aa"));
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
	//cout<<".....Rec Thread Started"<<endl;

	pthread_create( &thread1, NULL, startRecievingThread, (void*)t_r) ;  
	//cout<<"Rec Thread Started"<<endl;
	pthread_join( thread1, NULL);
	//startRecievingThread();
}

void enrollJob(message m){
	cout<<"EnrollJob"<<endl;
	
	//cout<<"Message Arrived: "<<m.Hash_Val<<endl;
	//cout<<"Message Arrived: "<<m.Key_Range_Start<<endl;
	//out<<"Message Arrived: "<<m.Key_Range_End<<endl;
	jobBreaker = true;
	currentstate->packetserver=m;
	currentstate->ID = m.Client_ID;
	strcpy(currentstate->pass,m.Key_Range_Start);
	sendACKJob();
	//startCracking();
	//pthread_t thread2;
	pthread_create( &thread2, NULL, startCracking, NULL);  
	
	//pthread_join( thread1, NULL);




}
void sendACKJob(){
		printf("Sending ACK to \"%s\"\n", "Server");
		char buffer[sizeof(struct message)];
		struct message temp_msg = currentstate->packetserver;
		temp_msg.Command = ACK_JOB; 
		memcpy(buffer, &temp_msg, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen);
}
void ping(message m){
cout<<"Ping Request from Server"<<endl;
cout<<currentstate->ID<<" # "<<currentstate->done<<" # "<<(currentstate->packetserver).Key_Range_Start<<" # "<<currentstate->pass<<endl;
		char buffer[sizeof(struct message)];
		struct message temp_msg = currentstate->packetserver;
		temp_msg.Command = currentstate->done;
		strcpy(temp_msg.Key_Range_End , currentstate->pass);
		memcpy(buffer, &temp_msg, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen);
}
void cancelJob(message m){
//cout<<"cancelJob"<<endl;
jobBreaker = false;
cout<<"Password Found by some Other Worker OR Job Cancelled OR Job Finished"<<endl;
currentstate = new WorkerState();
// free(currentstate)

//currentstate = (WorkerState *) malloc(sizeof(WorkerState));
}
void found(char *pass){
cout<<"Sending password to server"<<endl;
	    char buffer[sizeof(struct message)];
		struct message temp_msg = currentstate->packetserver;
		temp_msg.Client_ID = currentstate->ID;		
		temp_msg.Command = DONE_FOUND;
		strcpy(temp_msg.Key_Range_Start, pass);
		memcpy(buffer, &temp_msg, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen);


}
void donenotfound(){
cout<<"Password Not Found"<<endl;
	    char buffer[sizeof(struct message)];
		struct message temp_msg = currentstate->packetserver;
		temp_msg.Client_ID = currentstate->ID;		
		temp_msg.Command = currentstate->done;
		strcpy(temp_msg.Key_Range_End, currentstate->pass);
		memcpy(buffer, &temp_msg, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, p->ai_addr, p->ai_addrlen);


}

 bool checkHash(char *pass, char *hash) {
	
	char *this_hash = crypt(pass , "ab");
	if(strcmp(this_hash , hash) == 0)
	{
		return true;
	}else
	{
		return false;
	}
}

void *startCracking(void *ptr){
	//struct PassCracker bf;

	currentstate->done=NOT_DONE;
	
	cout<<"Cracking Started"<<endl;
	char start[strlen((currentstate->packetserver).Key_Range_Start)];
	
	
	strcpy(start,(currentstate->packetserver).Key_Range_Start);
	//cout<<start<<endl;
	char *end = new char[strlen((currentstate->packetserver).Key_Range_End)];
	

	strcpy(end,(currentstate->packetserver).Key_Range_End);
		//cout<<strlen(end)<<endl;
	char set[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
    PassCracker bf(set,start);
	cout<<bf.toString()<<endl;
	char*  attempt= bf.toString();
//	cout<<"PassBreaker Init"<<endl;
	
	while (jobBreaker) {
					if (checkHash(attempt, (currentstate->packetserver).Hash_Val)) {
						cout<<"Password Found: "<<attempt<<endl;
						currentstate->done = DONE_FOUND;
					//	char *password = new char[strlen(attempt)];
						found(attempt);
						break;
					}else if( strcmp(attempt,end)==0){
						currentstate->done= DONE_NOT_FOUND;
						donenotfound();
						break;
					}
					attempt = bf.toString();
					//printf("%d\n",attempt);
				//	cout<<attempt<<endl;
					strcpy(currentstate->pass , attempt);
					bf.increment();
				}


return NULL;
}
void *startRecievingThread(void *ptr){
//cout<<"In Thread"<<endl;
struct threadrec *t = (struct threadrec *)ptr;
	char buf[sizeof(struct message)];
	while(true){

	  int n = recvfrom(t->sockfd, buf,sizeof(struct message), 0, (t->p)->ai_addr, &(t->p)->ai_addrlen);
		if (n < 0)  {
		cout<<"error"<<endl;
		}
		else{
			message tmp;
			memcpy(&tmp, buf, sizeof(tmp));
			//cout<<"listener: "<<tmp.Command<<endl;
			//int command = tmp.Command;
					
			switch (tmp.Command) {

						case 0:
							ping(tmp);
							break;
						case 2:
							//cout<<"Enroll"<<endl;
							enrollJob(tmp);
							break;
						case 7:
							cancelJob(tmp);
							break;
						default:
					//		System.out.println("Invalid Command from server!!");
							printf("Invalid Command \"%s\"\n", tmp.Command);
						
						}
	
	
	
		}
	  
	}
  
return ptr;
}
/*
int main(int argc, char *argv[])
{
	
worker(argc,argv);


/*
	if ((numbytes = sendto(sockfd, argv[2], strlen(argv[2]), 0,
			 p->ai_addr, p->ai_addrlen)) == -1) {
		perror("talker: sendto");
		exit(1);
	}

	freeaddrinfo(servinfo);

	printf("talker: sent %d bytes to %s\n", numbytes, argv[1]);

	//	char buf[MAXBUFLEN];
	  int n = recvfrom(sockfd, buf, strlen(buf), 0, p->ai_addr, &p->ai_addrlen);
    if (n < 0) 
    {}
	else
    printf("Echo from server: %s", buf);


	//close(sockfd);

	return 0;
}
*/