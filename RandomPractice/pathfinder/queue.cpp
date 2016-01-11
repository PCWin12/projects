#ifndef __QUEUE_CPP
#define __QUEUE_CPP
#include "queue.h"
#include <cstring>
#include <iostream>
using namespace std;

Queue::Queue()
{
}
/*{

	 List  l = temp;;
        int le = l.Length();

        ListItem* arr[le];
        for(int i=0; i<le ; i++)
        {
                arr[i]= (l.GetHead());
                l.DeleteHead();

        }
        for(int i=0; i<le ; i++)
        {
                Push(arr[i]);

        }


}

*/
Queue::~Queue()
{
}
void Queue::changeQ(string a  , double num)
{
	ListItem* temp = list.GetHead();
                while(temp!=NULL)
                {
                        if(!strcmp(temp->node.c_str() , a.c_str() ) )
                        {
                               temp->x = num;
				temp->y = num;
				temp ->weight = num;
                        }
                        temp = temp->next;
                }
}

bool Queue::InQueue(string a)
{
		ListItem* temp = list.GetHead();
		while(temp!=NULL)
		{
			if(!strcmp(temp->node.c_str() , a.c_str() ) )
			{
				return true;
			}
			temp = temp->next;
		}
return false;

}
ListItem * Queue::removeMin()
{
	list.Sort();
	ListItem* temp = list.GetHead();
        list.DeleteHead();
        return temp;
}

bool Queue::isEmpty()
{
	if(list.GetHead() == NULL)
	return true;
	else
	return false;

}
void Queue::Push(ListItem * temp)
{
	list.InsertAtTail(temp->from ,temp->node , temp->x,temp->y,temp->weight);
	
}
ListItem* Queue::Peek()
{
	return list.GetHead();
		
}


ListItem* Queue::Pop()
{
	ListItem* temp = list.GetHead();
	list.DeleteHead();
	return temp;
}
int Queue::Length()
{
	int len = list.Length();
	return len;
	
}

#endif
