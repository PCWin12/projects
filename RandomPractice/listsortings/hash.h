#ifndef __HASH_H
#define __HASH_H
#include <cstdlib>
#include <vector>
#include <string.h>
#include "sorts.h"
#include <sys/time.h>
using namespace std;


int hashfunc(long num)
{
	return (int)num;
}
		
void insert(ListItem* arr[],vector<long> nums)
{
	int N;
	for (int i=0; i<nums.size(); i++)
	{

		N = hashfunc(nums[i]);
		ListItem* temp = new ListItem(nums[i]);
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
#endif

