#include <string.h>
#include <iostream>
using namespace std;
struct PassCracker{
	char arr[62];
	char *end;
	PassCracker(char charSet[] , char ch_end[]){
	strcpy(arr , charSet);
//	cout<<arr<<endl;
	end = new char[strlen(ch_end)];
	strcpy(end,ch_end);
	//cout<<end<<endl;
	}
	
	void increment(){
		int ind = strlen(end)-1;
	while(ind>=0){
		if(*(end+ind)==arr[strlen(arr)-1])
		{
			//cout<<sizeof(arr)<<endl;
			if(ind ==0){
				end = new char[strlen(end)+1];
				for(int i_temp = 0 ; i_temp<=strlen(end) ; i_temp++){
					*(end+i_temp) = arr[0];
				}
				break;
			}
			else{
			*(end+ind) = arr[0];
			ind--;
//			cout<<"1break"<<endl;
			}
		}else{
//			cout<<*(end+ind)<<" : "<<find(arr,*(end+ind))<<endl;		
			*(end+ind)=arr[find(arr,*(end+ind))+1];
			break;
		}
	}
	
	
	};
	int find(char a[]  , char b){
//		cout<<sizeof(a)<<endl;
		for (int i=0;i<strlen(a) ; i++){
	//		cout<<a[i]<<" > " <<b<<endl;
			if(a[i] == b)
				return i;
		}
		return 0;
	}
	char* toString(){
	return end;
	}
	
};
