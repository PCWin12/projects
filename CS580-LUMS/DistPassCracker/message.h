/* message.h */

/* Messaging protocol constants and formats */

/* Command definition */

#define PING 				0
#define REQUEST_TO_JOIN		1
#define JOB 				2
#define ACK_JOB		 		3
#define DONE_NOT_FOUND		4
#define DONE_FOUND 			5
#define NOT_DONE			6
#define CANCEL_JOB			7
#define HASH				8
/* Keys and Hash values Maximum length */

#define KEYLEN				6
#define H_MAXLEN			24

struct	message {				/* message format of Password cracker protocol	*/
	unsigned int	Magic;		
	unsigned int	Client_ID;	
	unsigned int	Command;
	char	Key_Range_Start[KEYLEN];
	char	Key_Range_End[KEYLEN];
	char	Hash_Val[H_MAXLEN];
};
extern void *print_message_function( void *ptr );

