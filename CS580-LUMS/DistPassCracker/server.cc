/*
** listener.c -- a datagram sockets "server" demo
*/
#include <pthread.h>
#include "message.c"
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
#include "Request.h"

#include "workerstate.h"
#include <sys/time.h>
#include <stdio.h>
#include <unistd.h>

#define MYPORT "10340"	// the port users will be connecting to

#define MAXBUFLEN 100

#include <iostream>
#include "work.h"
using namespace std;

char *ownip;
char *ownport;
Request *currentReq = new Request();
int IDcount;
int currentState;
long lastpingrequest;
pthread_t thread1,thread2;
vector<Work> workers;
vector<Request> requesters;
int sockfd;
struct sockaddr_in temp;
struct addrinfo hints, *servinfo, *p;
// get sockaddr, IPv4 or IPv6:


struct threadrec {
struct addrinfo *p ;
int sockfd ;
};
struct pingWork {
struct sockaddr_in temp_addr;
int ID;
};



void *startPingThread(void *ptr);
void *startPingWorker(void *ptr);
void recievePing(message tmp  ,struct sockaddr_in temp_addr ) ;
void recieveACK(message mp);
void resendJob(Work w);
void doneFound(message tmp);
void doneNotFound(message tmp);
void notifyRequest(message tmp);
void cancelAll() ;
void crackRequest(message tmp , struct sockaddr_in temp_addr);
void requestACK() ;
void distributeReq();
void reallocate();
void updateRequest();
void acceptWorker(message tmp  ,struct sockaddr_in temp_addr);
void *startRecievingThread(void *ptr);
void notDone(message tmp);
void cancelJob(message tmp);






long getTime(){
   struct timeval start;
   long seconds;    
   gettimeofday(&start, NULL);   
   return start.tv_sec;
}


void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}

	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

int main(int argc , char *argv[])
{
	if(argc!=2){
		cout<<"Usage : server <PORT>"<<endl;
		exit(1);
	}
	ownport = argv[1];

	int rv;
	int numbytes;
	struct sockaddr_in their_addr;
	char buf[MAXBUFLEN];
	socklen_t addr_len;
	char s[INET6_ADDRSTRLEN];

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC; // set to AF_INET to force IPv4
	hints.ai_socktype = SOCK_DGRAM;
	hints.ai_flags = AI_PASSIVE; // use my IP

	if ((rv = getaddrinfo(NULL, ownport, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("listener: socket");
			continue;
		}

		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("listener: bind");
			continue;
		}

		break;
	}

	if (p == NULL) {
		fprintf(stderr, "listener: failed to bind socket\n");
		return 2;
	}

	freeaddrinfo(servinfo);

	//printf("listener: waiting to recvfrom...\n");
//////////////////////////////////
	struct threadrec *t_r;
	t_r = (struct threadrec *)NULL;
	t_r = (struct threadrec *) malloc(sizeof(struct threadrec));
	t_r->p = p;
	t_r->sockfd = sockfd;
	//cout<<".....Rec Thread Started"<<endl;

	pthread_create( &thread1, NULL, startRecievingThread, (void*)t_r) ;  
	cout<<"Recieving Thread Started"<<endl;
	pthread_create( &thread2, NULL, startPingThread, NULL) ;  
	pthread_join( thread2, NULL);
	pthread_join( thread1, NULL);

	//pthread_join( thread2, NULL);


/////////////////////////////////
	
	
	
	
/*	
while(true){
	addr_len = sizeof their_addr;
	if ((numbytes = recvfrom(sockfd, buf, MAXBUFLEN-1 , 0,
		(struct sockaddr *)&their_addr, &addr_len)) == -1) {
		perror("recvfrom");
		//exit(1);
	}

	message tmp;
    memcpy(&tmp, buf, sizeof(tmp));
	
		cout<<"Message Arrived: "<<tmp.Hash_Val<<endl;
		cout<<"Message Arrived: "<<tmp.Key_Range_Start<<endl;
		cout<<"Message Arrived: "<<tmp.Key_Range_End<<endl;
		// cout<<"Message Arrived: "<<tmp.Hash_Val<<endl;
		
	//struct message *ptr;
//	*ptr = tmp;

	
	
	//pthread_t thread2;
	//pthread_create( &thread2, NULL, print_message_function, (void*) ptr);  
	
	//pthread_join( thread2, NULL);
	/*cout<<"thread return: "<<tmp.Command<<endl;
	if(tmp.Command == 1){
		tmp.Magic = 15440;
		tmp.Client_ID = 1;
		tmp.Command = JOB;
		strcpy(tmp.Key_Range_Start, "aaaa");
		strcpy(tmp.Key_Range_End, "9999");
		strcpy(tmp.Hash_Val, crypt("apho", "ab"));
		char buffer[sizeof(struct message)];
		memcpy(buffer, &tmp, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &their_addr, addr_len);


	
	}
	
	//sendto(sockfd, buf, strlen(buf), 0, 
	  //     (struct sockaddr *) &their_addr, addr_len);



}*/
	close(sockfd);

	return 0;
}

void *startRecievingThread(void *ptr){
	cout<<"***Server Initiated***"<<endl;
	struct threadrec *t = (struct threadrec *)ptr;
	char buf[sizeof(struct message)];
	while(true){
	bzero(buf, sizeof(struct message));
     struct sockaddr_in temp_addr;
	 socklen_t addr_len = sizeof temp_addr;
	 int n = recvfrom(t->sockfd, buf, MAXBUFLEN-1 , 0,(struct sockaddr *)&temp_addr, &addr_len);
		if (n < 0)  {
		cout<<"error"<<endl;
		}
		else{
			message tmp;
			memcpy(&tmp, buf, sizeof(tmp));
		//	cout<<"listener: "<<tmp.Command<<endl;
			//int command = tmp.Command;
					
			switch (tmp.Command) {

						case PING:
							cout<<"Recieve Ping"<<endl;
							recievePing(tmp , temp_addr);
							break;
						case REQUEST_TO_JOIN:
							cout<<"Enroll"<<endl;
							acceptWorker(tmp, temp_addr);
							break;
						case ACK_JOB:
							cout<<"Ack Recieve"<<endl;
							recieveACK(tmp);
							break;
						case DONE_NOT_FOUND:
							cout<<"Done Not Found"<<endl;
							doneNotFound(tmp);
							break;
						case DONE_FOUND:
							cout<<"Done Found"<<endl;
							doneFound(tmp);
							break;
						case NOT_DONE:
							cout<<"Not Done"<<endl;
							notDone(tmp);
							break;
						case CANCEL_JOB:
							cout<<"Cancel Job"<<endl;
							cancelJob(tmp);
							break;
						case HASH:
							cout<<"Hash Job"<<endl;
							crackRequest(tmp, temp_addr);
							break;
						default:
					//		System.out.println("Invalid Command from server!!");
							printf("Invalid Command \"%s\"\n", tmp.Command);
						
						}
	
	
	
		}
	  
	}
  
return ptr;
}

void acceptWorker(message tmp  ,struct sockaddr_in temp_addr) {
		Work w;
		w.temp_addr = temp_addr;
		w.ID = IDcount;
		IDcount++;
		w.status = true;
		w.state = tmp;
		w.working = false;
		workers.push_back(w);
	printf("Worker Added | ID= %d | IP : %d : %ld", w.ID, w.temp_addr.sin_port , w.temp_addr.sin_addr.s_addr);
		updateRequest();
	}

void updateRequest(){

	if(currentReq->workstatus){
		reallocate();
	}
}

void reallocate(){
	if(currentReq->start.size() >0){
		char *temp = currentReq->start.front();
		char *st = new char[strlen(temp)];
		strcpy(st,temp);
		char *temp2 = currentReq->end.front();
		char *end = new char[strlen(temp)];
		strcpy(end,temp2);
		currentReq->start.erase(currentReq->start.begin());
		currentReq->end.erase(currentReq->end.begin());
		struct message m1;
		m1.Magic = 15440;
		m1.Client_ID = workers.back().ID;
		m1.Command = JOB;
		strcpy(m1.Key_Range_Start, st);
		strcpy(m1.Key_Range_End,end);
		strcpy(m1.Hash_Val, currentReq->hash);
		Work w = workers.back();
		workers.pop_back();
		w.state = m1;
		w.ack = 1;
		w.working = true;
		w.ackNo = 1;
		w.ackTime = getTime();
		strcpy(w.end , end);
		
		workers.push_back(w);
		
		socklen_t addr_len = sizeof (w.temp_addr);
		char buffer[sizeof(struct message)];
		memcpy(buffer, &m1, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &(w.temp_addr), addr_len);
		   
		   
		   
	}else{
	}


}
void reallocateAgain(int id){
	if(currentReq->start.size() >0){
		char *temp = currentReq->start.front();
		char *st = new char[strlen(temp)];
		strcpy(st,temp);
		char *temp2 = currentReq->end.front();
		char *end = new char[strlen(temp)];
		strcpy(end,temp2);
		currentReq->start.erase(currentReq->start.begin());
		currentReq->end.erase(currentReq->end.begin());
		struct message m1;
		m1.Magic = 15440;
		m1.Client_ID = workers.at(id).ID;
		m1.Command = JOB;
		strcpy(m1.Key_Range_Start, st);
		strcpy(m1.Key_Range_End,end);
		strcpy(m1.Hash_Val, currentReq->hash);
		Work w = workers.at(id);
		workers.erase(workers.begin()+id);
		w.state = m1;
		w.ack = 1;
		w.working = true;
		w.ackNo = 1;
		w.ackTime = getTime();
		strcpy(w.end , end);
		
		workers.insert(workers.begin()+id , w);
		
		socklen_t addr_len = sizeof (w.temp_addr);
		char buffer[sizeof(struct message)];
		memcpy(buffer, &m1, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &(w.temp_addr), addr_len);
		   
		   
		   
	}else{
		cout<<"No Job Left"<<endl;
	}


}
void distributeReq(){
	cout<<"Distributing Request"<<endl;
	int totalworkers = workers.size();
	currentState=6;
	for(int i=0 ; i<totalworkers ; i++){
		char *temp = currentReq->start.front();
		char *st = new char[strlen(temp)];
		strcpy(st,temp);
		char *temp2 = currentReq->end.front();
		char *end = new char[strlen(temp)];
		strcpy(end,temp2);
		currentReq->start.erase(currentReq->start.begin());
		currentReq->end.erase(currentReq->end.begin());
		struct message m1;
		m1.Magic = 15440;
		m1.Client_ID = workers.at(i).ID;
		m1.Command = JOB;
		strcpy(m1.Key_Range_Start, st);
		strcpy(m1.Key_Range_End,end);
		strcpy(m1.Hash_Val, currentReq->hash);
		
		Work w = workers.at(i);
		//cout<<"5"<<endl;
		w.state = m1;
		w.ack = 1;
		w.working = true;
		w.ackNo = 1;
	
		w.ackTime = getTime();
		//cout<<"6"<<endl;
		strcpy(w.end , end);
		workers.erase(workers.begin()+i);
		workers.insert(workers.begin()+i,w);
		
		socklen_t addr_len = sizeof (w.temp_addr);
		char buffer[sizeof(struct message)];
		memcpy(buffer, &m1, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &(w.temp_addr), addr_len);
		cout<<"Distributed and sent"<<endl;

		   
		   
	}
	

}
void requestACK() {

	cout<<"Ack to Request Client "<<endl;
		struct message m1;
		m1.Magic = 15440;
		m1.Client_ID = currentReq->ID;
		m1.Command = ACK_JOB;
		strcpy(m1.Key_Range_Start, "xxxxx");
		strcpy(m1.Key_Range_End,"xxxxx");
		strcpy(m1.Hash_Val,"12331232131");
		socklen_t addr_len = sizeof (currentReq->temp_addr);
		char buffer[sizeof(struct message)];
		memcpy(buffer, &m1, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &(currentReq->temp_addr), addr_len);
		cout<<"ACK sent Back to Request Clients"<<endl;

	}
void crackRequest(message tmp , struct sockaddr_in temp_addr){
	if(!currentReq->workstatus){
		lastpingrequest = getTime();
		Request req;
		req.workstatus = true;
		req.ID = IDcount;
		IDcount++;
		strcpy(req.hash,tmp.Hash_Val);
		req.state = tmp;
		req.status = true;
		req.temp_addr = temp_addr;
		*currentReq = req;
		currentReq->start.clear();
		currentReq->end.clear();
		char sample[] = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		for(int i=0;i<61 ; i++){
			char *st = new char[KEYLEN];    // Hard Coded 6 letter Password
			char *end = new char[KEYLEN];    // Hard Coded 6 letter Password
			for (int j=0 ; j<KEYLEN ; j++){
				*(st+j) = sample[i];
				*(end+j) = sample[i+1];
			}
			*(st+KEYLEN-1) = '\0';
			*(end+KEYLEN-1) = '\0';
			
			currentReq->start.push_back(st);
			currentReq->end.push_back(end);
			
		}
		
		
		distributeReq();
		requestACK();
		
	
	
	}else{
	
	cout<<"Request Already Exist: Queueing"<<endl;
			Request r;
			r.state = tmp;
			requesters.push_back(r);
	}

}
void cancelAll() {

		for (int i = 0; i < workers.size(); i++) {
	
			struct message m1;
			m1.Magic = 15440;
			m1.Client_ID = workers.at(i).ID;
			m1.Command = CANCEL_JOB;
			strcpy(m1.Key_Range_Start, "xxxxxx");
			strcpy(m1.Key_Range_End , "xxxxxx");
			strcpy(m1.Hash_Val, "xxxxxxxx");
			Work w = workers.at(i);
			w.state = m1;
			w.ack = 0;
			w.working = false;
			w.lastping = getTime();
			workers.erase(workers.begin()+i);
			workers.insert(workers.begin()+i,w);
				
				
			socklen_t addr_len = sizeof (w.temp_addr);
			char buffer[sizeof(struct message)];
			memcpy(buffer, &m1, sizeof(struct message));
			sendto(sockfd, buffer, sizeof(struct message), 0, 
			   (struct sockaddr *) &(w.temp_addr), addr_len);
			cout<<"Cancel To worker sent: "<<m1.Client_ID <<endl;
			
		}
}
void notifyRequest(message tmp) {
			struct message m1;
			m1.Magic = 15440;
			m1.Client_ID = currentReq->ID;
			m1.Command = DONE_FOUND;
			strcpy(m1.Key_Range_Start,tmp.Key_Range_Start);
			strcpy(m1.Key_Range_End , "xxxxxx");
			strcpy(m1.Hash_Val, "xxxxxxxx");
			socklen_t addr_len = sizeof (currentReq->temp_addr);
			char buffer[sizeof(struct message)];
			memcpy(buffer, &m1, sizeof(struct message));
			sendto(sockfd, buffer, sizeof(struct message), 0, 
			   (struct sockaddr *) &(currentReq->temp_addr), addr_len);
			cout<<"Cancel To Request Client sent"<<endl;
			currentReq->workstatus = false;
	}
void doneNotFound(message tmp) {
		int id = tmp.Client_ID;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.at(i).ID == id) {
				Work w = workers.at(i);
				w.state = tmp;
				w.ack = 0;
				w.working = false;
				w.lastping = getTime();
				workers.erase(workers.begin()+i);
				workers.insert(workers.begin()+i,w);
				// workers.remove(i);
				if (!currentReq->workstatus) {
					cout<<"Ping From Worker: "<<id<<endl;
				} else {
					cout<<"Done Not Found From Worker: "<< tmp.Client_ID<<endl;
					cout<<"Assigning New Job"<<endl;
					reallocateAgain(i);
				}
				break;
			}
		}

	}
void cancelJob(message tmp) {
		cancelAll();
}
void notDone(message tmp) {
		int id = tmp.Client_ID;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.at(i).ID == id) {
				
				
				Work w = workers.at(i);
				w.state = tmp;
				w.lastping = getTime();
				workers.erase(workers.begin()+i);
				workers.insert(workers.begin()+i,w);
				
				if (currentReq->workstatus) {
					printf("Not Done From Worker: %d  | LastPass=%s \n", tmp.Client_ID, tmp.Key_Range_End);
				} else {
					cout<<"Ping From Worker: "<<id<<endl;
				}
				break;
			}
		}

}

void doneFound(message tmp) {
		int id = tmp.Client_ID;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.at(i).ID == id) {
				char * passwd = new char[KEYLEN];
				Work w = workers.at(i);
				w.state = tmp;
				strcpy(passwd , tmp.Key_Range_Start);
				w.ack = 0;//
				w.working = false;//
				w.lastping = getTime();
				workers.erase(workers.begin()+i);
				workers.insert(workers.begin()+i,w);
				// workers.remove(i);
				currentState = 5;
				if (currentReq->workstatus) {

					cancelAll();
					notifyRequest(tmp);
					printf("Done AND Found From Worker: %d | Password= %s", tmp.Client_ID, tmp.Key_Range_Start);
				} else {

					cout<<"Ping From Worker: "<<id<<endl;
				}
				break;
			}
		}

	}
void resendJob(Work w) {
	struct message m1 = w.state;
	w.ackTime = getTime();
	socklen_t addr_len = sizeof w.temp_addr;
	char buffer[sizeof(struct message)];
	memcpy(buffer, &m1, sizeof(struct message));
	sendto(sockfd, buffer, sizeof(struct message), 0, 
	   (struct sockaddr *) &(w.temp_addr), addr_len);

	cout<<" Job sent Again"<<endl;;

}
void recieveACK(message mp) {
		int id = mp.Client_ID;
		for (int i = 0; i < workers.size(); i++) {
			if (workers.at(i).ID == id) {
				Work w = workers.at(i);
				w.ack = 2;//
				w.lastping = getTime();
				workers.erase(workers.begin()+i);
				workers.insert(workers.begin()+i,w);
				printf("ACK for Job Recieved: | From Worker= %d  | start= %s | end= %s | hash= %s" , id, mp.Key_Range_Start, mp.Key_Range_End, mp.Hash_Val);
				//startPingWorker(workers.get(i).ID, workers.get(i).port);
				struct pingWork *p_w;
				p_w = (struct pingWork *)NULL;
				p_w = (struct pingWork *) malloc(sizeof(struct pingWork));
				p_w->ID=w.ID ;
				p_w->temp_addr =w.temp_addr;
				//cout<<".....Rec Thread Started"<<endl;
				pthread_t thread_temp;
				pthread_create( &thread_temp, NULL, startPingWorker, (void*)p_w) ;  
				
			}
		}
}

void recievePing(message tmp  ,struct sockaddr_in temp_addr ) {
		lastpingrequest = getTime();
		tmp.Magic = 15440;
		tmp.Client_ID = currentReq->ID;
		tmp.Command = currentState;
		strcpy(tmp.Key_Range_Start, "xxxx");
		strcpy(tmp.Key_Range_End, "xxxx");
		strcpy(tmp.Hash_Val, crypt("apho", "ab"));
		socklen_t addr_len = sizeof temp_addr;
		char buffer[sizeof(struct message)];
		memcpy(buffer, &tmp, sizeof(struct message));
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &temp_addr, addr_len);
}

void *startPingWorker(void *ptr){
	//cout<<"***Ping Worker Initiated*** ID: "<< <<endl;
	struct pingWork *t = (struct pingWork *)ptr;
	char buf[sizeof(struct message)];
	cout<<"***Ping Worker Initiated*** ID: "<<t->ID<<endl;
			struct message m1;
			m1.Magic = 15440;
			m1.Client_ID = t->ID;
			m1.Command = PING;
			strcpy(m1.Key_Range_Start,"xxxxxx");
			strcpy(m1.Key_Range_End , "xxxxxx");
			strcpy(m1.Hash_Val, "xxxxxxxx");
			socklen_t addr_len = sizeof t->temp_addr;
			char buffer[sizeof(struct message)];
			memcpy(buffer, &m1, sizeof(struct message));
		
	while(true){
		sendto(sockfd, buffer, sizeof(struct message), 0, 
	       (struct sockaddr *) &(t->temp_addr), addr_len);
			sleep(2);
	  
	}
  
return ptr;
}
void *startPingThread(void *ptr) {
	while (true) {
			for (int i = 0; i < workers.size(); i++) {
				if (getTime()
						- workers.at(i).lastping > 3
						&& workers.at(i).working == true) {
					cout<<"TimeOut for Worker ID = "<<workers.at(i).ID<<endl;
					char *temp = workers.at(i).state.Key_Range_End;
					char *st = new char[strlen(temp)];
					strcpy(st,temp);
					char *temp1 = workers.at(i).end;
					char *end = new char[strlen(temp1)];
					strcpy(end,temp1);
					
					currentReq->start.insert(currentReq->start.begin(), st);
					currentReq->end.insert(currentReq->end.begin(),end);
					workers.erase(workers.begin()+i);
					break;
				}
				if (workers.at(i).ack == 1
						&& getTime()
								- workers.at(i).ackTime > 5) {
					Work w = workers.at(i);
					w.ackNo++;
					workers.erase(workers.begin()+i);
					workers.insert(workers.begin()+i,w);
					resendJob(w);
					if (workers.at(i).ackNo > 3) {
						workers.erase(workers.begin()+i);
						break;
					}

				}
			}
			if (getTime() - lastpingrequest > 15
					&& currentReq->workstatus) {
				cout<<"Request Client Timeout"<<endl;
				cancelAll();
				currentReq->workstatus = false;
				lastpingrequest = getTime();
				if (requesters.size() > 0) {
					cout<<"Queued Request started"<<endl;
					Request req;
					req = requesters.at(0);
					*currentReq = req; 
					currentReq->workstatus = false;
					crackRequest(requesters.at(0).state, requesters.at(0).temp_addr);
					requesters.erase(requesters.begin());
				}
			}
			sleep(2);
			cout<<endl;
	}
return ptr;
}