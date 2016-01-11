#include "pathfinder.h"
#include<fstream>
using namespace std;

/*

********** NOTE****************
1- Adjacnecy list is used to store the whole graph
2 - Graph will be printed in the following format

<Node1> <x> <y>
-----<Edge1> <x> <y> <weight>
-----<Edge2> <x> <y> <weight>
-----.
     .
<Node2> <x> <y>
-----<Edge1> <x> <y> <weight>
-----<Edge2> <x> <y> <weight>
-----.
     ..
.
.
.

*/

int main()
{

vector<NodeArray> DataStorage ;
vector<NodeArray> MST;
vector<NodeArray> DA;
vector<NodeArray> D;
here:
cout<<"\n\n************* Graphs *************"<<endl;
cout<<"(1) Choose a new graph data file"<<endl;
cout<<"(2) Find shortest path using Dijkstra's algorithm"<<endl;
cout<<"(3) Compute the minimal spanning tree using Kruksal's algorithm"<<endl;
cout<<"(4) Quit"<<endl;
cout<<"Enter choice:"<<endl<<">>";
int i;
//cin.ignore();
cin>>i;



	if(i == 1)     // Data entry code
	{
		string node , x , y;
		string type;
		double x1 , y1;
		string arcs = "ARCS";
		here1:
		cout<<"Please Enter The File Name : "<<endl<<">";
		string nameFile;
		cin.ignore();
		cin>>nameFile;
		ifstream dataFile;
		dataFile.open (nameFile.c_str());
		if(dataFile.fail( ) )
		{
			cout << "Data File Not Found\n\n\n";
			goto here1;
		}
		else

		{		getline(dataFile , type , '\n');	
				getline(dataFile,node,' ');
				getline(dataFile,x,' ');
				x1  = atof(x.c_str());	
				getline(dataFile,y,'\n');
				y1 = atof(y.c_str());
				NodeArray a = NodeArray(node,x1,y1);
				DataStorage.push_back(a);
				while(1)
				{
					getline(dataFile,node,' ');
	                                if(!strncmp(node.c_str() , arcs.c_str() , 4))
					{	break;
					}
					
					getline(dataFile,x,' ');
        	                        x1  = atof(x.c_str());
                	                getline(dataFile,y,'\n');
                        	        y1 = atof(y.c_str());
                        	        NodeArray a = NodeArray(node,x1,y1);
                        	        DataStorage.push_back(a);
                        	        
				}
					D = DataStorage;
					MST = DataStorage;
					char * pch;
					char * writable = new char[node.size() + 1];
					strcpy(writable,node.c_str());
					pch = strtok (writable,"\n");
						string temp;
					for(int j=0;j<1;j++)
					{
						pch = strtok (NULL, "\n");
						temp = pch;
						if(pch == NULL)
							break;
					}
				string to, from;
				double weight;
		              		getline(dataFile,to,' ');
                                        getline(dataFile,x,'\n');
                                        weight = atof(x.c_str());
                                      DataStorage =   AddEdge(temp,to,DataStorage, weight);
				while(!dataFile.eof())
				{
					getline(dataFile,from,' ');
					if(from[0] == '\0')
						break;
					getline(dataFile,to,' ');
					getline(dataFile,x,'\n');
					weight = atof(x.c_str());	
					DataStorage = AddEdge(from,to,DataStorage, weight);
				}
			DA = DataStorage;
			cout<<"\n\n ***Graph completed*** \n"<<endl;
			cout<<"Adjacency List Used with the following format: \n"<<endl;
			cout<<" <Node> <X> <Y> \n ------<Edge1> <X> <Y> <Weight>\n ------<Edge2> <X> <Y> <Weight>\n.\n.\n.\n"<<endl;
			cout<<"*****Graph Obtained**** "<<endl;			
			for(int i =0 ; i<DataStorage.size();i++)
			 {  cout<<DataStorage[i].Node<<" X->"<<DataStorage[i].x<<" Y->"<<DataStorage[i].y<<endl;
			 	(DataStorage[i].Edge).Length();
			         }       	
								
					

				
		}	
		goto here;
	}
	else if(i == 2)		// Shortest path code
	{
		cout<<"\n********Dijsktra's Algorithm********\n\n"<<endl;
		cout<<"Type "<<endl;
		for(int i=0; i<DA.size() ; i++)
		{
			cout<<" "<<i<<" For -> "<<DA[i].Node<<endl;
		}
		cin.ignore();
		int start , end;
		here2:
		cout<<"Enter Integer For Start Node\n>>"<<endl;
		cin>>start;
		cin.ignore();
		cout<<"Enter Integer For End Node\n>>"<<endl;
                cin>>end;
		if(start>DA.size()-1 || start<0)
		{
			cout<<"Node not found"<<endl;
			goto here2;
		}
		 if(end>DA.size()-1 || end<0)
                {
                        cout<<"Node not found"<<endl;
                        goto here2;
                }
		

			for(int i =0 ; i<D.size() ; i++)
			{
				if( start == i)
				{
					D[i].x = 0;
				}
				else
					D[i].x = 99999999;
			}
			Queue Q;
			for(int i=0 ; i<D.size() ; i++)
			{
				ListItem* a2 = new ListItem(" ", D[i].Node , D[i].x ,D[i].x ,D[i].x );
				Q.Push(a2);
			}
			ListItem* temp10 ;
			ListItem *tempP10 = NULL;
			
			while(!Q.isEmpty())
			{
					temp10 = Q.removeMin();
					tempP10 = (DA[findInArr(DA ,temp10->node)].Edge). GetHead();		
					while(tempP10!=NULL)
					{
						if(Q.InQueue(tempP10->node))
						{
							if( D[findInArr(D ,temp10->node)].x + tempP10->weight < D[findInArr(D ,tempP10->node)].x)
							{
								 D[findInArr(D ,tempP10->node)].x = D[findInArr(D ,temp10->node)].x + tempP10->weight ;
								Q.changeQ(tempP10->node , D[findInArr(D ,tempP10->node)].x );								
							}
						}
						tempP10 = tempP10 -> next;
					}
			}
				
			if(!strcmp(DA[end].Node.c_str() , DA[start].Node.c_str()) )
			{
				cout<<"**********************"<<endl;
                        cout<<"Shortest Path Distance: 0"<<endl;
                        cout<<"**********************"<<endl;
			}
			else
			{
			cout<<"**********************"<<endl;
			cout<<"Shortest Path Distance: "<<D[findInArr(D,DA[end].Node)].x<<endl;
			cout<<"**********************"<<endl;
			}
								
						













	}
	
	else if(i == 3)		//	MST code
	{

for(int i =0 ; i<DataStorage.size();i++)
                 {
                                if(MST[i].Edge.GetHead() != NULL)
                               {
					cout<<"##"<<endl;

                                           cout<<MST[i].Node<<" X->"<<MST[i].x<<" Y->"<<MST[i].y<<endl;
                                           (MST[i].Edge).Length();
                               }
                }

		vector<NodeArray>  tempData;
		tempData = DataStorage;
		vector<ListItem*> tempItem;
		for(int i=0; i<DataStorage.size();i++)
		{
			ListItem * tempA1 = (DataStorage[i].Edge).GetHead();
			while(tempA1!=NULL)
			{	
				tempItem.push_back(tempA1);
			
				tempA1 = tempA1-> next;
			}
		}
		
		int n = tempItem.size();
		ListItem* arr[n+1];
		for(int i=1 ;i<n+1 ; i++)
		{
			arr[i] = tempItem[i-1];
		}
		InsertionSortItem(arr , n+1);	
		
		int size_MST = DataStorage.size();
		string tempFrom , tempTo;
		for(int i=1 ; i<n+1 ; i++)
		{
			tempFrom = arr[i] -> from;
			tempTo = arr[i]->node;
			if(EdgeInMST(MST, tempFrom , tempTo) )
			{

				for(int j=0 ; j< size_MST ; j++)
				{
					if(!strcmp((MST[j].Node).c_str() , tempFrom.c_str() ) )
					{
						(MST[j].Edge).InsertAtHead(tempFrom , tempTo  , arr[i]->x , arr[i]->y , arr[i]->weight);
					}
				}
			}
		}
							
				
				
			
			 
			cout<<"\n\n\n******** Minimum Spanning Tree ********"<<endl;
			cout<<"Adjacency List Used with the following format: \n"<<endl;
                        cout<<" <Node> <X> <Y> \n ------<Edge1> <X> <Y> <Weight>\n ------<Edge2> <X> <Y> <Weight>\n.\n.\n.\n"<<endl;
			cout<<"\n\n\n******** Complete Minimum Spanning Tree Below ******** \n"<<endl;

		 for(int i =0 ; i<size_MST;i++)
                 {
				if(MST[i].Edge.GetHead() != NULL)
				{

		                           cout<<MST[i].Node<<" X->"<<MST[i].x<<" Y->"<<MST[i].y<<endl;
                		           (MST[i].Edge).Length();
                		} 
		}	
			cout<<endl;
			cout<<"\n******** Complete Minimum Spanning Tree Above(All Edges and Nodes in MST)******** \n"<<endl;


	}
	
	cout<<"********Program Closing********"<<endl;
return 0;
}
