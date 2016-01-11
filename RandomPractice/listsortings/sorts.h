#ifndef __SORTS_H
#define __SORTS_H

#include <iostream>
#include <vector>
#include <cstdio>
#include <cstdlib>
#include <ctime>

using namespace std;
struct ListItem
{
    long value;
    ListItem *next;
    ListItem *prev;

    ListItem(long theVal)
    {
        this->value = theVal;
        this->next = NULL;
        this->prev = NULL;
    }
};
vector<long> InsertionSort(vector<long> nums);
vector<long> HeapSort(vector<long> nums);
vector<long> MergeSort(vector<long> nums);
vector<long> BucketSort(vector<long> nums, int max);

/* 1 for median of three. 2 for random pivot */
vector<long> QuickSortArray(vector<long> nums, int pivot_type);
vector<long> QuickSortList(vector<long> nums, int pivot_type);

#endif
