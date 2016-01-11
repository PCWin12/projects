#ifndef __HASH2_H
#define __HASH2_H
#include <cstdlib>
#include <vector>
#include <string.h>
using namespace std;
struct ListItem2
{
	string word;


	ListItem2(string theword)
	{
		this->word  = theword;
	}
};


class Hash2
{
	public:

		int type;
		int numberItems;
		int currentSize;
		ListItem2 **arr ;

		// Constructor
		Hash2(int func, vector<string> words);


		int hashfunc1(string word);
		int hashfunc2(string word);
		void insert1(vector<string> words);
		void insert2(vector<string> words);
		void lookup(string temp);
		void resizing();
		void lookup1(string temp);

};

#endif
