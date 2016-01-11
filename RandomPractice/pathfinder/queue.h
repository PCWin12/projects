#ifndef __QUEUE_H
#define __QUEUE_H
#include "list.cpp"

/* This is the generic Queue class */

class Queue
{
    List list;

public:

    // Constructor
    Queue();

    // Copy Constructor
//    Queue(const Queue& otherQueue);

    // Destructor
    ~Queue();
	bool isEmpty();
    // Required Methods
    void Push(ListItem* temp);
	void changeQ(string a , double num);
//    string fromItem , string item , double x , double y, double w);
	ListItem* removeMin();
    ListItem* Peek();
    ListItem* Pop();
    int Length();
	bool InQueue(string a);
};

#endif

