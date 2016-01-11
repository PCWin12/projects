#include "Hash.h"
#include <vector>
#include <iostream>
#include <cstring>
using namespace std;



Hash::Hash(int func, vector<string> words)
{
	struct timeval start, end, diff;
	type = func;
	for(int i=0; i<98999; i++)
	{
		arr[i] = NULL;
	}
	if (func == 1)
	{
		cout<<"--For Chaining"<<endl;
		gettimeofday(&start, NULL);
		insert1(words);
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout <<"---Took : "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
	}
	else
	{
		cout<<"--For Chaining"<<endl;
		gettimeofday(&start, NULL);
		insert2(words);
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout <<"---Took : "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
	}

}
int Hash::hashfunc1(string word)
{
	int sum =0;
	for (int i=0; i<strlen(word.c_str()); i++)
	{
		sum = sum + (int)word[i];
	}
	return sum;
}

int Hash::hashfunc2(string word)
{
	int sum =0;
	for (int i=0; i<strlen(word.c_str()); i++)
	{
		sum = sum + (i+1)*((int)word[i]);
	}
	return sum;
}


void Hash::insert1(vector<string> words)
{
	int sum;
	int N;
	for (int i=0; i<words.size(); i++)
	{

		sum = hashfunc1(words[i]);
		N = sum % 98999;
		ListItem* temp = new ListItem(words[i]);
		if(arr[N] == NULL)
		{
			arr[N] = temp;
		}
		else
		{
			temp->next = arr[N];
			arr[N] = temp;
		}
	}
}

void Hash::insert2(vector<string> words)
{
	int sum;
	int N;
	for (int i=0; i<words.size(); i++)
	{

		sum = hashfunc2(words[i]);
		N = sum % 98999;
		ListItem* temp = new ListItem(words[i]);
		if(arr[N] == NULL)
		{
			arr[N] = temp;
		}
		else
		{
			temp->next = arr[N];
			arr[N] = temp;
		}
	}
}


void Hash::lookup(string word)
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
	int N= sum % 98999;
	if(arr[N] == NULL)
	{
		cout<<"Word Not Found"<<endl;
	}
	else
	{
		ListItem * temp;
		temp = arr[N];
		int count;
		while(temp!=NULL)
		{
			if(strcmp(word.c_str(),(temp->word).c_str())==0)
			{
				cout<<"Word Found"<<endl;
				count =1;
				break;
			}
			temp  = temp->next;
		}
		if(count!=1)
		{
			cout<<"Word Not found"<<endl;
		}
	}
}
void Hash::lookup1(string word)
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
	int N= sum % 98999;
	if(arr[N] == NULL)
	{}
	else
	{
		ListItem * temp;
		temp = arr[N];
		int count;
		while(temp!=NULL)
		{
			if(strcmp(word.c_str(),(temp->word).c_str())==0)
			{
				count =1;
				break;
			}
			temp  = temp->next;
		}
		if(count!=1)
		{
		}
	}
}

