#ifndef __LIST_CPP
#define __LIST_CPP
#include "list.h"
#include <cstdlib>
#include <cstring>
#include <iostream>
using namespace std;

List::List()
{
	head = NULL;
}


List::List(const List& otherList)
{
	head = NULL;
	ListItem *temp = otherList.head;
	//for(int i=0;i<j ; i++)
	while(temp !=NULL)	
	{
		InsertAtTail(temp->from , temp->node , temp->x , temp->y , temp->weight);
		temp = temp->next;
	}
}


/*List::~List()
{
	if ( head == NULL )
	{
	return;
	}
	ListItem * temp = head;
	ListItem * temp2;
	while ( temp->next != NULL)
	{
		temp2 = temp->next;
		delete[] temp;
		temp = temp2;
	}
	delete[] temp;
	
	head = NULL;
}
*/

void List::InsertAtHead(string fromItem ,string item, double x , double y, double w)
{
      ListItem * temp; 
      temp= new ListItem(fromItem , item ,x,y,w);
	if(head!=NULL)
	{
	temp->next = head;
	temp->prev = NULL;
	head->prev = temp;
	//temp->node = item;
	head = temp;
	}
	else
	{
	head = temp;
	head->next = NULL;
	head->prev = NULL;
	}

}



void List::InsertAtTail(string fromItem,string item , double x , double y, double w)
{
	ListItem * temp = new ListItem(fromItem , item ,x,y,w) ;
	ListItem * temp2;
	if(head == NULL)
	{
		head = temp;
		head->next = NULL;
		head->prev = NULL;
	}
	else
	{	
		temp2 = head;
		while(temp2->next!= NULL)
		{
			temp2  = temp2->next;
		}
		temp->node = item;
		temp->next = NULL;
		temp->prev = temp2;
		temp2->next = temp;
	}
}


ListItem* List::GetHead()
{
return head;
}


ListItem* List::GetTail()
{
	ListItem * temp;
	if( head ==NULL)
		return head;
	else
	{
		temp = head;
	 	while(temp->next!= NULL)
		{
			temp = temp->next;
		}
	}
	ListItem * tail = NULL;
	tail = temp;
	return temp;
}


ListItem *List::SearchFor(string item)
{
	ListItem * temp;
	temp = head;
	while( temp !=NULL)
	{
		if ( !strcmp((temp->node).c_str() , item.c_str() ) )
		{
			return temp;
			break;
		}
		temp = temp->next;
	}
}

int List::Length()
{
	int length = 0;
	ListItem * temp;
	temp = head;
	while(temp!= NULL)
	{
		cout<<"------"<<temp->node<<" "<<temp->x<<" "<<temp->y<<" Weight->"<<temp->weight<<endl;
		length=length+1;
		temp = temp->next;
	}
	return length;
}




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
        if(temp2->weight > temp1->weight)
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
void List::Sort()
{
	head = MergeSortList(head);
} 

void List::DeleteHead()
{
	if(head!=NULL && head->next!=NULL)
	{
		ListItem * temp;
		temp =  head;
		head = temp->next;
		head->prev = NULL;
	}
	else
	{
		head = NULL;
	}
	
}
#endif

