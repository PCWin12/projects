
#ifndef __LIST_H
#define __LIST_H

#include<cstdlib>
#include<string.h>
#include<iostream>
using namespace std;

/* This class just holds a single data item. */
struct ListItem
{
    string node;
	string from;	
    double x,y;

    double weight;
    ListItem *next;
    ListItem *prev;

    ListItem(string thefrom,string theVal, double theX , double theY, double theW)
    {
	this ->from = thefrom;
        this->node = theVal;
        this->x = theX;
        this->y =  theY;
        this->weight = theW;
        this->next = NULL;
        this->prev = NULL;
    }
};

/* This is the generic List class */
class List
{
    ListItem *head;

public:

    // Constructor
    List();

    // Copy Constructor
    List(const List& otherList);

    // Destructor
//    ~List();

    // Insertion Functions
    void InsertAtHead(string fromItem , string item , double x , double y, double w);
    void InsertAtTail(string fromItem ,string item, double x , double y, double w);
//    void InsertAfter(T toInsert, T afterWhat);

    // Lookup Functions
    ListItem *GetHead();
    ListItem *GetTail();
    ListItem *SearchFor(string item);

    // Deletion Functions
//    void DeleteElement(T item);
    void DeleteHead();
    //void DeleteTail();
	void Sort();
    // Utility Functions
    int Length();
};

#endif
