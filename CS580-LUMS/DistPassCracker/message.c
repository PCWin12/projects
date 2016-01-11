#include<stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include "message.h"

void *print_message_function( void *ptr )  
{
	struct message* m;  
	m = (struct message *) ptr;  
	printf("Magic No. %d\n", m->Magic);
	printf("Client_ID. %d\n", m->Client_ID);
	printf("Command. %d\n", m->Command);
	printf("Key_Range_Start. %s\n", m->Key_Range_Start);
	printf("Key_Range_End. %s\n", m->Key_Range_End);
	printf("Hash. %s\n", m->Hash_Val);
	return ptr;
}

