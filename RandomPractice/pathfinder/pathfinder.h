#include "list.cpp"
#include<iostream>
#include<cstring>
#include "list.cpp"
#include "queue.cpp"
#include<vector>
using namespace std;
struct NodeArray
{
	string Node ;
	List Edge;
	double x,y;
	NodeArray(string theNode , double thex , double they)
	{
		this->x = thex;
		this->y = they;
		this->Node = theNode;
	}
};

vector<string> IncludeInMST( vector<string> temp , string a , string b)
{
	int reta,retb;
        reta = 0;
        retb =0;
        for(int i=0 ; i<temp.size() ; i++)
        {
                if(!strcmp(temp[i].c_str() , a.c_str()) )
                {
                        reta = 1;
                }
                if( !strcmp(temp[i].c_str() , b.c_str()) )
                {
                        retb = 1;
                }
        }
	 if(reta==0)
        {
                temp.push_back(a);
        }
        if( retb ==0)
        {
                temp.push_back(b);
        }


return temp;
}

int findInArr(vector<NodeArray> temp , string a)
{
	for( int i=0 ; i<temp.size() ; i++)
	{
		if(!strcmp(temp[i].Node.c_str() , a.c_str()) )
		{
			return i;
		}
	}
}
bool IsVisited(vector<string> temp , string a)
{
	for( int i=0 ; i<temp.size() ; i++)
        {
                if(!strcmp(temp[i].c_str() , a.c_str()) )
                {
                        return true;
                }
        }
return false;
}
bool EdgeInMST(vector<NodeArray> temp , string a , string b)
{
	vector<string> temp2;
	temp2.push_back(a);
	Queue Q ;
	ListItem* a1 = new ListItem(" " , a , 0 ,0, 0 );
	Q.Push(a1);
	ListItem* tempP , *tempP1;
	while(!Q.isEmpty())
	{
		tempP1 = Q.Pop();
		tempP = temp[findInArr(temp ,tempP1->node)].Edge .GetHead();
		while (tempP!=NULL)
		{
			if(!IsVisited(temp2, tempP->node) )
			{
				temp2.push_back(tempP->node);   //visit
				if(!strcmp(tempP->node.c_str() , b.c_str() ) )
				{
					return false;
				}
				Q.Push(tempP);
			}
			tempP = tempP->next;
			
		}
	}
	
return true;
}
	
vector<NodeArray> InsertionSort(vector<NodeArray> temp)
{
	double  temp2;
	int j;
	for (int i=1;i<temp.size();i++)
	{
		temp2 = (temp[i].Edge).GetHead() -> weight;
		cout<<temp2<<endl;
		NodeArray temp3 = NodeArray(temp[i].Node, temp[i].x ,  temp[i].y);
		temp3.Edge = temp[i].Edge;
		j = i-1;
		while(j>=0 && (temp[j].Edge).GetHead()->weight >temp2)
		{	
			temp[j+1] = temp[j];
			j = j-1;
		}
		temp[j+1] = temp3;

	}
return temp;
} 		


void InsertionSortItem(ListItem* temp[] , int size)
{
        double  temp2;
        int j;
	ListItem* temp3;
        for (int i=2;i<size;i++)
        {
                temp2 = temp[i] -> weight;
                temp3 = temp[i];
                j = i-1;
                while(j>0 && temp[j]->weight >temp2)
                {
                        temp[j+1] = temp[j];
                        j = j-1;
                }
                temp[j+1] = temp3;

        }

}

vector<NodeArray> AddEdge( string from, string to, vector<NodeArray> DataStorage, double w)
{
	string temp2;
	int fromI, toI;
	double fromX, fromY, toX, toY;
	for(int i=0 ; i<DataStorage.size() ; i++)
	{
		temp2 = DataStorage[i].Node;
		if(!strcmp( temp2.c_str() ,from.c_str()))
		{
			fromI = i;
			fromX = DataStorage[i].x;
			fromY = DataStorage[i].y;
		}
	}
	
	string temp3;
        for(int i=0 ; i<DataStorage.size() ; i++)
        {
                temp3 = DataStorage[i].Node;
                if(!strcmp( temp3.c_str() ,to.c_str()))
                {
                	toI = i;
                	toX = DataStorage[i].x;
                        toY = DataStorage[i].y;
                }
        }
        
        (DataStorage[fromI].Edge).InsertAtHead(DataStorage[fromI].Node , to,toX ,toY,w);
        (DataStorage[toI].Edge).InsertAtHead(DataStorage[toI].Node , from,fromX ,fromY,w);
return DataStorage;
}


