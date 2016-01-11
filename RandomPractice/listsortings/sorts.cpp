#ifndef __SORTS_CPP
#define __SORTS_CPP

#include <iostream>
#include <vector>
#include <cstdio>
#include <cstdlib>
#include <ctime>
#include "hash.h"
#include "sorts.h"

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

vector<long> InsertionSort(vector<long> nums)
{	 long arr[nums.size()];
        for(int c =0 ;c<nums.size();c++)
        {
                arr[c] = nums[c];
        }
	struct timeval start, end, diff;
	long temp;
	int j;
	gettimeofday(&start, NULL);
	for (int i=2;i<nums.size();i++)
	{
		temp = arr[i];
		j = i-1;
		while(j>0 && arr[j]>temp)
		{	
			arr[j+1] = arr[j];
			j = j-1;
		}
		arr[j+1] = temp;
	}
	gettimeofday(&end, NULL);
	timeval_subtract(&diff, &end, &start);

	cout << "Insertion Sort Took :  "<< diff.tv_sec << " seconds and "<<diff.tv_usec << " microseconds."<<endl;
	vector<long>temp1;
        for(int c =0 ;c<nums.size();c++)
        {
                temp1.push_back(arr[c]);
        }

return temp1;
}

void InsertionSortint(long arr[],int rand[] )
{
	 long temp;
	int temp2;
        int j;
        for (int i=2;i<3;i++)
        {
                temp = arr[i];
           	temp2 = rand[i];
		     j = i-1;
                while(j>0 && arr[j]>temp)
                {
                        arr[j+1] = arr[j];
        		rand[j+1] = rand[j];          
		      j = j-1;
                }
                arr[j+1] = temp;
		rand[j+1] = temp2;
        }
}

void HeapMaintain(long arr[],int n, int size)
{
	int max;
	int left = 2*n;
	int right = 2*n +1;
	if( left<= size && arr[left]>arr[n])
	{
		max = left;
	}
	else
	{
	max = n;
	}
	if(right<=size && arr[right] > arr[max])
	{
		max = right;
	}
	if( max!=n)
	{
		long temp;
 		temp = arr[n] ;
		arr[n] = arr[max];
		arr[max] = temp;
		HeapMaintain(arr , max , size);
	}
}
void HeapCreate(long arr[], int size)
{
	for(int i = size/2 ; i>=1 ;i--)
	{
		HeapMaintain(arr,i, size);
	}
}

		


vector<long> HeapSort(vector<long> nums)
{struct timeval start, end, diff;

	int n = nums.size();

	long arr[n+1];

	for(int i=1 ; i<n+1 ; i++)
	{
		arr[i] = nums[i-1];
	}
	gettimeofday(&start, NULL);
	long temp;
	HeapCreate(arr,n+1);
	int SizeHeap = n+1;
	for(int i=n+1; i>=2;i--)
	{
		 temp = arr[1];
		arr[1] = arr[i];
		arr[i] = temp;
		 SizeHeap = SizeHeap -1;
		HeapMaintain(arr,1,SizeHeap);
	
	}
	gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
	cout << "Heap Sort Took :  "<< diff.tv_sec << " seconds and "<<diff.tv_usec << " microseconds."<<endl;

	 vector<long> temp2;

        for(int i=1; i<n+1 ; i++)
        {
                temp2.push_back(arr[i]);
        }
return temp2;


}
/*void ArrayMerge(long arr[],int low ,int middle,int high ,int n)
{  
	long temp[n];
    int i,j,k;
  i=low; j=middle; k=low;
  while (i<middle && j<high) { 
    if (arr[i]<=arr[j]) {temp[k]=arr[i]; i=i+1;} 
    else {temp[k]=arr[j]; j = j+1;}
    k = k+1;
  }
  while (i<middle) { 
    temp[k]=arr[i]; i = i+1; k = k+1;
  }
  while (j<high) { 
    temp[k]=arr[j]; j = j+1; k = k+1;
  }
  for (k=low; k<high; k++) { 
    arr[k]=temp[k]; 
  }
}
*/
ListItem *listMerge(ListItem *temp1, ListItem *temp2)
{
	ListItem * temp3;
	if(temp2 == NULL)
	{
		return temp1;
	}
	if(temp1 == NULL)
	{
		return temp2;
	}
	if(temp2->value>temp1->value)
	{
                temp3 = temp1;
                temp3->next = listMerge(temp1->next , temp2);
        }
        else
        {
                temp3 = temp2;
                temp3->next = listMerge(temp1,temp2->next);
        }

return temp3;
}
ListItem * MergeSortList(ListItem * head)
{
        if(head==NULL || head->next == NULL)
        {
                return head;
        }
        ListItem* temp1 = head;
        ListItem * temp2 = head->next;
        while(temp2 !=NULL && temp2->next!=NULL)
        {
                head = head -> next;
                temp2 = (head -> next) ->next;
        }
        temp2 = head->next;
        head->next = NULL;
        ListItem * temp3 = MergeSortList(temp1);
	ListItem * temp4 = MergeSortList(temp2);
	ListItem * temp5 = listMerge(temp3,temp4);
return temp5;
}

vector<long> MergeSort(vector<long> nums)
{
	ListItem * head = new ListItem(nums[0]);
	for(int i =1 ; i<nums.size() ; i++)
	{
		ListItem *temp1 = new ListItem(nums[i]);
		temp1->next = head;
		head = temp1;		
	}
	
	struct timeval start, end, diff;
	gettimeofday(&start, NULL);
	ListItem * SortedHead = MergeSortList(head);
	gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
	cout << "Merge Sort Took :  "<< diff.tv_sec << " seconds and "<<diff.tv_usec << " microseconds."<<endl;
	vector<long> temp2;
	ListItem * temp = SortedHead;
	for(int i =0 ; i<nums.size() ; i++)
	{
		temp2.push_back(temp->value);
		temp  = temp->next;
	}
return temp2;
}		



vector<long> MergeSortindex(vector<long> nums)
{
        ListItem * head = new ListItem(nums[0]);
        for(int i =1 ; i<nums.size() ; i++)
        {
                ListItem *temp1 = new ListItem(nums[i]);
                temp1->next = head;
                head = temp1;
        }

        ListItem * SortedHead = MergeSortList(head);
        ListItem * temp = SortedHead;
        vector<long> temp2;
        
        for(int i =0 ; i<nums.size() ; i++)
        {
                temp2.push_back(temp->value);
                temp  = temp->next;
        }
return temp2;
}		
		
vector<long> BucketSort(vector<long> nums, int max)
{
	ListItem * arr[max+1];
	for(int i=0; i<max+1; i++)
	{
		arr[i] = NULL;
	}
	struct timeval start, end, diff;
	gettimeofday(&start, NULL);
	insert(arr,nums);
	for(int i =0 ; i<max+1 ; i++)
	{
		MergeSortList(arr[i]);
	}
	gettimeofday(&end, NULL);
			timeval_subtract(&diff, &end, &start);
					cout << "Bucket Sort Took : "<<diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
					
					
	ListItem *  temp;
	vector<long> temp2;

	for(int i=0; i<max+1 ; i++)
	{
		temp = arr[i];
		while(temp !=NULL)
		{
			temp2.push_back(temp->value);
				temp = temp->next;
		}
	}
return temp2;
}

int pivot(long arr[],int high , int low, int pivotType)
{
	if(pivotType ==1)
	{
		long temp[3];
		int random[3];
		for(int i=0 ; i<3 ; i++)
		{
			random[i] = rand() %(high - low +1) +low;
			temp[i] = arr[random[i]];
		}
		InsertionSortint(temp,random);
		return random[1];
	}
	else
	{
		
		int random = rand() %(high - low +1) +low;
		return random;
	}
}
void QuickSortArr(long arr[] , int pivotType ,int low , int high)
{
	int pivotIndex  = pivot(arr, high, low , pivotType);
	int i = low;
	int j = high;
	 long temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
	int pivotKey  = arr[pivotIndex];

	while(i<j)
	{
		while(arr[j]>=pivotKey)
		{
			j--;
		}
		while(arr[i]<=pivotKey)
		{
			i++;
		}
		if(i<=j)
		{
			 long temp = arr[i];
       			 arr[i] = arr[j];
		        arr[j] = temp;
			i++;
			j--;
		}
	}	
	if(j - low >=1)
	QuickSortArr(arr,pivotType ,low ,j );
	if(high - i >=1)
	QuickSortArr(arr,pivotType ,i ,high);
}  		
		
/* 1 for median of three. 2 for random pivot */
vector<long> QuickSortArray(vector<long> nums, int pivot_type)
{


struct timeval start, end, diff;
	int n = nums.size();
	long arr[n];

	for( int i = 0; i<n;i++)
	{
		arr[i] = nums[i];
	}
	gettimeofday(&start, NULL);
	QuickSortArr(arr , pivot_type,0,n-1);
	gettimeofday(&end, NULL);
			timeval_subtract(&diff, &end, &start);
	if(pivot_type ==1)
	cout << "Quick Sort Using Array (Medians of Pivot) Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
	else
	cout << "Quick Sort Using Array (Random Pivot) Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
	
	 vector<long>temp1;
        for(int c =0 ;c<n;c++)
        {
                temp1.push_back(arr[c]);
        }

return temp1;


}
ListItem*  QuickSortLinkedList(ListItem* head , int pivotType , int size)
{
	if(head == NULL)
		return head;
	long pivot1;
	if(pivotType == 1)
        {
                int random[3];
      
		ListItem * head1 = NULL;
                vector<long> random1;
                for(int i=0 ; i<3 ; i++)
                {
                 
                        random[i] = rand() % size;
                        random1.push_back(random[i]);
                }
		random1 = MergeSortindex(random1);
		int j=0;
		int h=0;
			ListItem * temp = head;
			
		while(temp!=NULL)
		{
			if(j  == (int) random1[h])
			{	
				while(j == (int) random1[h])
				{
					h++;
					ListItem * temp1  = new ListItem(temp->value);
					temp1->next = head1;
					head1 = temp1;
					if(h==3)
					break;
				}
				if(h==3)
				break;
				                                        
			}
			j++;
			temp = temp->next;
		}
		head1 = MergeSortList(head1);
		if(size>=3)
		pivot1 = (head1->next)->value;
		else
		pivot1 = head1->value;
        }
        else
        {

                int random = rand() %(size) ;
                 int j=0;
                int h=0;
                        ListItem * temp = head;

                while(temp!=NULL)
                {
                        if(j  == random)
                        {     
                                pivot1 = temp->value;
				
				break;
                        }
                        j++;
                        temp = temp->next;
                }

        }

	ListItem * headRight = NULL;
	ListItem * headLeft = NULL;
	ListItem * leftTail;
	ListItem * headPivot = NULL;
	ListItem * pivotTail;
	ListItem * temp = head;
	int leftSize = 0;
	int rightSize = 0;
	int pivotSize =0;
	while(temp!=NULL)
	{
		if(temp->value > pivot1 )
		{
			ListItem * temp3 = new ListItem ( temp->value);
			temp3->next = headRight;
			headRight = temp3;
			rightSize++;
		}
		else if( temp->value == pivot1)
		{
			ListItem * temp5 = new ListItem ( temp->value);
                        if(pivotSize ==0)
				pivotTail = temp5;
			temp5->next = headPivot;
                        headPivot = temp5;
                        pivotSize++;
		}
		else
		{
			 ListItem * temp4 = new ListItem ( temp->value);
                       if(leftSize ==0)
                                leftTail = temp4;

			 temp4->next = headLeft;
                        headLeft = temp4;
			leftSize++;
		}
		temp  = temp->next;
	}
	if(leftSize ==0)
	{
		headRight = QuickSortLinkedList(headRight, pivotType , rightSize);
		pivotTail->next = headRight;
		return headPivot;
	}
	else
	{
		 headRight = QuickSortLinkedList(headRight, pivotType , rightSize);
                 headLeft = QuickSortLinkedList(headLeft, pivotType , leftSize);
		temp = headLeft;
		while(temp->next != NULL)
		{
			temp = temp->next;
		}
		temp->next= headPivot;
		pivotTail->next = headRight;
                return headLeft;
	}
}
vector<long> QuickSortList(vector<long> nums, int pivot_type)
{
	ListItem * head = NULL;
	int n = nums.size();
	for(int i=0 ; i<n ; i++)
	{
		ListItem* temp = new ListItem(nums[i]);
		temp->next = head;
		head = temp;
	}
	struct timeval start, end, diff;
	
	gettimeofday(&start, NULL);
	head = QuickSortLinkedList(head,pivot_type,n);
	gettimeofday(&end, NULL);
	                        timeval_subtract(&diff, &end, &start);
	      if(pivot_type ==1)
	     cout << "Quick Sort Using Linked List (Medians of Pivot) Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec <<" microseconds."<<endl;
	        else
	    cout << "Quick Sort Using Linked List (Random Pivot) Took :  "<< diff.tv_sec << " seconds and "<<diff.tv_usec << " microseconds."<<endl;
	                                                        
	ListItem * temp2 = head;
	vector<long> temp3;
	 for(int i=0 ; i<n ; i++)
	{
		temp3.push_back(temp2->value);
		temp2= temp2->next;
	
        }
return temp3;
}		
#endif

