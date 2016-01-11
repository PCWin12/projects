#include "Hash2.h"
#include <vector>
#include <iostream>
#include <cstring>
using namespace std;


Hash2::Hash2(int func, vector<string> words)
{
	struct timeval start, end, diff;
	arr = new ListItem2*[7];
	currentSize =7;
	numberItems = 0;
	type = func;
	for(int i=0; i<currentSize; i++)
	{
		arr[i] = NULL;
	}
	if (func == 1)
	{
		cout<<"--For Open Addressing (Initial Size = 7)"<<endl;
		gettimeofday(&start, NULL);
		insert1(words);
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;

	}
	else
	{
		cout<<"--For Open Addressing (Initial Size = 7)"<<endl;
		gettimeofday(&start, NULL);
		insert2(words);
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;

	}

};
int Hash2::hashfunc1(string word)
{
	int sum =0;
	for (int i=0; i<strlen(word.c_str()); i++)
	{
		sum = sum + (int)word[i];
	}
	return sum;
}

int Hash2::hashfunc2(string word)
{
	int sum =0;
	for (int i=0; i<strlen(word.c_str()); i++)
	{
		sum = sum + (i+1)*((int)word[i]);
	}
	return sum;
}




void Hash2::insert1(vector<string> words)
{
	int sum;
	int N;
	for (int i=0; i<words.size(); i++)
	{
		if((i/currentSize) * 100 > 75)
		{
			resizing();
		}
here:
		sum = hashfunc1(words[i]);
		N = sum % currentSize;
		ListItem2* temp = new ListItem2(words[i]);
		if(arr[N] == NULL)
		{
			arr[N] = temp;
		}
		else
		{
			while(arr[N] !=NULL)
			{
				N = N+1;
				if(N>=currentSize)
				{
					resizing();
					goto here;
				}

			}
			arr[N] = temp;

		}
	}
}

void Hash2::insert2(vector<string> words)
{
	int sum;
	int N;
	for (int i=0; i<words.size(); i++)
	{
		if((i/currentSize) * 100 > 75)
		{
			resizing();
		}
here2:

		sum = hashfunc2(words[i]);
		N = sum % currentSize;
		ListItem2* temp = new ListItem2(words[i]);
		if(arr[N] == NULL)
		{
			arr[N] = temp;
		}
		else
		{
			while(arr[N] !=NULL)
			{
				N = N+1;
				if(N>=currentSize)
				{
					resizing();
					goto here2;
				}
			}
			arr[N] = temp;
		}
	}
}


void Hash2::lookup(string word)
{
	int sum;
	if(type ==1)
	{
		sum = hashfunc1(word);
	}
	else
	{
		sum = hashfunc2(word);
	}
	int N= sum % currentSize;
	if(arr[N] == NULL)
	{
		cout<<"Word Not Found"<<endl;
	}
	else
	{
		int count=0;
		while(arr[N]!=NULL )
		{
			if(strcmp(word.c_str(),(arr[N]->word).c_str())==0)
			{
				cout<<"Word Found"<<endl;
				count =1;
				break;
			}
			N=N+1;
			if(N>=currentSize)
				break;
		}
		if(count!=1)
		{
			cout<<"Word Not found"<<endl;
		}
	}
}
void Hash2::lookup1(string word)
{
	int sum;
	if(type ==1)
	{
		sum = hashfunc1(word);
	}
	else
	{
		sum = hashfunc2(word);
	}
	int N= sum % currentSize;
	if(arr[N] == NULL)
	{
	}
	else
	{
		int count=0;
		while(arr[N]!=NULL )
		{
			if(strcmp(word.c_str(),(arr[N]->word).c_str())==0)
			{
				count =1;
				break;
			}
			N=N+1;
			if(N>=currentSize)
				break;
		}
		if(count!=1)
		{
		}
	}
}


void Hash2::resizing()
{

	vector<string> temp;
	for(int i=0 ; i<currentSize ; i++)
	{
		if(arr[i]!=NULL)
		{
			temp.push_back(arr[i]->word);
		}
	}
	currentSize = 2*currentSize + 1;
	delete[] arr;
	arr = new ListItem2* [currentSize];
	for(int i=0; i<currentSize; i++)
	{
		arr[i] = NULL;
	}

	if(type ==1 )
		insert1(temp);
	else
		insert2(temp);
}



