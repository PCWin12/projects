#include <iostream>
#include <vector>
#include <cstdio>
#include <cstdlib>
#include <ctime>
#include "sorts.cpp"
using namespace std;

/* 
Number theory to the rescue.
 * Generates a permutation of [0 .. d-1]
 * Where n<d and both are prime
 */
vector<long> GenerateRandom(long n, long d)
{
    vector<long> nums;
    long k, residue;
    for (k = 0; k < d; k++)
    {
        residue = (long) (((long long)k * (long long) n) % d);
        nums.push_back(residue);
    }
    return nums;
}

vector<long> GenerateSorted(long n)
{
    vector<long> nums;
    for (long k = 0; k < n; k++)
    {
        nums.push_back(k);
    }
    return nums;
}

vector<long> GenerateReverseSorted(long n)
{
    vector<long> nums;
    for (long k = n; k > 0; k--)
    {
        nums.push_back(k);
    }
    return nums;
}

vector<long> GenerateAlmostSorted(long n, int s)
{
    vector<long> nums;
    srand ( time(NULL) );
    for (long k = n; k > 0; k--)
    {
        nums.push_back(k);
    }
    int k1, k2;
    long tmp;
    for (size_t i = 0; i < s; i++)
    {
        k1 = rand() % n;
        k2 = rand() % n;
        tmp = nums[k2];
        nums[k2] = nums[k1];
        nums[k1] = tmp;
    }
    return nums;
}

int main() 
{
int n,d;
cout<<"\n\n*************** Sorting *******************\n\n"<<endl;
cout<<"Enter value for n and d where n<d and both are primes" <<endl;
cout<< "d : >>";
cin>>d;
cin.ignore();
cout<<"n : >>";
cin>>n;
cout<<"Enter the Pivot type :"<<endl;
//int pivot_type;

//cout<< "1 -Medians of pivot\n2 -Random Pivot "<<endl;
//cin.ignore();

//cin>>pivot_type;


cout<<"Sorting in Progress..."<<endl;
cout<<"\n\n**With Random Input**\n\n "<<endl;

vector<long> temp   = GenerateRandom((long)n, (long)d);
vector<long> tempi,temph,tempb,tempq1,tempq2,tempq3, tempq4,tempm;
tempi = InsertionSort(temp);
temph = HeapSort(temp);
tempm = MergeSort(temp);
tempb = BucketSort(temp, d-1);
tempq1 = QuickSortArray(temp,1);
tempq2 = QuickSortList(temp,1);
tempq3 = QuickSortArray(temp,2);
tempq4 = QuickSortList(temp,2);


cout<<"Sorting in Progress.."<<endl;
cout<<"\n\n**With Sorted Input**\n\n "<<endl;

 temp   = GenerateSorted((long)n);
tempi = InsertionSort(temp);
temph = HeapSort(temp);
tempm = MergeSort(temp);
tempb = BucketSort(temp, d-1);
tempq1 = QuickSortArray(temp,1);
tempq2 = QuickSortList(temp,1);
tempq3 = QuickSortArray(temp,2);
tempq4 = QuickSortList(temp,2);


cout<<"Sorting in Progress.."<<endl;
cout<<"\n\n**With Reverse Sorted Input**\n\n "<<endl;

 temp   = GenerateReverseSorted((long)n);
tempi = InsertionSort(temp);
temph = HeapSort(temp);
tempm = MergeSort(temp);
tempb = BucketSort(temp, d-1);
tempq1 = QuickSortArray(temp,1);
tempq2 = QuickSortList(temp,1);
tempq3 = QuickSortArray(temp,2);
tempq4 = QuickSortList(temp,2);



cout<<"Sorting in Progress.."<<endl;
cout<<"\n\n**With Almost Sorted Input**\n\n"<<endl;

 temp   = GenerateAlmostSorted((long)n, int (n/10));
tempi = InsertionSort(temp);
temph = HeapSort(temp);
tempm = MergeSort(temp);
tempb = BucketSort(temp, d-1);
tempq1 = QuickSortArray(temp,1);
tempq2 = QuickSortList(temp,1);
tempq3 = QuickSortArray(temp,2);
tempq4 = QuickSortList(temp,2);



}
