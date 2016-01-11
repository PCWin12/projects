#ifndef __HASH_H
#define __HASH_H
#include <cstdlib>
#include <vector>
#include <string.h>
#include <sys/time.h>
using namespace std;
int timeval_subtract (struct timeval *result, struct timeval *x, struct
                      timeval *y)
{
	/* Perform the carry for the later subtraction by updating y. */
	if (x->tv_usec < y->tv_usec)
	{
		int nsec = (y->tv_usec - x->tv_usec) / 1000000 + 1;
		y->tv_usec -= 1000000 * nsec;
		y->tv_sec += nsec;
	}
	if (x->tv_usec - y->tv_usec > 1000000)
	{
		int nsec = (x->tv_usec - y->tv_usec) / 1000000;
		y->tv_usec += 1000000 * nsec;
		y->tv_sec -= nsec;
	}

	/* Compute the time remaining to wait.
	   tv_usec is certainly positive. */
	result->tv_sec = x->tv_sec - y->tv_sec;
	result->tv_usec = x->tv_usec - y->tv_usec;

	/* Return 1 if result is negative. */
	return x->tv_sec < y->tv_sec;
}
struct ListItem
{
	string word;
	ListItem *next;


	ListItem(string theword)
	{
		this->word  = theword;
		this->next = NULL;
	}
};


class Hash
{

	public:
		ListItem *arr[98999];
		int type;

		// Constructor
		Hash(int func, vector<string> words);
		int hashfunc1(string word);
		int hashfunc2(string word);
		void insert1(vector<string> words);
		void insert2(vector<string> words);
		void lookup(string temp);
		void lookup1(string temp);


};

#endif
