#include<iostream>
#include<cstring>
#include<fstream>
#include<vector>
#include "Hash.cpp"
#include "Hash2.cpp"
using namespace std;
int main()
{
	cout<<"\n\n******* HASH TABLES *******\n\n"<<endl;
	vector<string> words;
	int sum=0;
here:
	cout<<"Enter the name of the data file:"<<endl<<">>";
	string nameFile;
	cin>>nameFile;
	ifstream dataFile;
	dataFile.open (nameFile.c_str());
	if(dataFile.fail( ) )
	{
		cout << "Data File Not Found\n\n\n";
		goto here;
	}
	else
	{
		string temp;
		getline(dataFile,temp,'\n');
		words.push_back(temp);
		while(!dataFile.eof())
		{
			getline(dataFile,temp,'\n');
			if(temp[0] == '\0')
				break;
			words.push_back(temp);
			sum =sum +1;

		}
	}
	vector<string> lookwords;

here5:
	cout<<"\nEnter the name of the Look Up  file:"<<endl<<">>";
	string nameFileL;
	cin>>nameFileL;

	ifstream dataFileL;
	dataFileL.open (nameFileL.c_str());
	if(dataFileL.fail( ) )
	{
		cout << "Data File Not Found\n\n\n";
		goto here5;
	}
	else
	{
		string temp;
		getline(dataFileL,temp,'\n');
		lookwords.push_back(temp);
		while(!dataFileL.eof())
		{
			getline(dataFileL,temp,'\n');
			if(temp[0] == '\0')
				break;
			lookwords.push_back(temp);

		}
	}


	struct timeval start, end, diff;

	int s;
here4:
	cout<<"\nFor Running Time Comparison. Enter - 1 "<<endl;
	cout<<"For insert/look up operations . Enter - 2"<<endl<<">>";
	cin.ignore();
	cin>>s;
	if(s<1 || s>2)
		goto here4;
	if(s==	1)
	{

		cout<<"\nFunction 1 - Sum of  ASCII values"<<endl;
		cout<<"Function 2 - Sum of ASCII values multiplied with the weightage"<<endl<<">>";
		cout<<"Loading with function 1 "<<endl;
		Hash myData11(1,words);

		Hash2 myData21(1,words);
		cout<<"Loading with function 2 "<<endl;
		Hash myData12(2,words);

		Hash2 myData22(2,words);



		cout<<"Look Up with function 1 "<<endl;
		cout<<"-- For Chaining"<<endl;
		gettimeofday(&start, NULL);
		for(int i =0 ; i<lookwords.size(); i++)
		{
			myData11.lookup1(lookwords[i]);
		}
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
		cout<<"--Open Addressing"<<endl;
		gettimeofday(&start, NULL);
		for(int i =0 ; i<lookwords.size(); i++)
		{
			myData21.lookup1(lookwords[i]);
		}
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
		cout<<"Look UP with function 2 "<<endl;
		cout<<"-- For Chaining"<<endl;
		gettimeofday(&start, NULL);
		for(int i =0 ; i<lookwords.size(); i++)
		{
			myData12.lookup1(lookwords[i]);
		}
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<< diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
		cout<<"--Open Addressing"<<endl;
		gettimeofday(&start, NULL);
		for(int i =0 ; i<lookwords.size(); i++)
		{
			myData21.lookup1(lookwords[i]);
		}
		gettimeofday(&end, NULL);
		timeval_subtract(&diff, &end, &start);
		cout << "---Took :  "<<diff.tv_sec << " seconds and " <<diff.tv_usec << " microseconds."<<endl;
		return 0;

	}


	int f;
here3:
	cout<<"\nEnter the Hash Function to use :\n"<<endl;
	cout<<"1 - Sum of  ASCII values"<<endl;
	cout<<"2 - Sum of ASCII values multiplied with the weightage"<<endl<<">>";
	cin.ignore();
	cin>>f;
	if(f<1 ||f>2)
		goto here3;

	Hash myData(f,words);
	Hash2 myData2(f,words);
	int c;
	string look;
here2:
	cout<<"\nEnter operations to perform: (1/2)\n"<<endl;
	cout<<"1 - Look Up\n2 - Insert"<<endl<<">>";
	cin>>c;
	if(c<1 ||c>2)
		goto here2;
	if(c==1)
	{
		cout<<"Enter the word to search:"<<endl<<">>";
		cin.ignore();
		cin>>look;
		cout<<"For Chaining: ";
		myData.lookup(look);
		cout<<"For Open Addressing: ";

		myData2.lookup(look);

	}
	else
	{
		cout<<"Enter the word to insert:"<<endl<<">>";
		cin.ignore();
		cin>>look;
		vector<string> temp;
		temp.push_back(look);

		if(myData.type == 1)
			myData.insert1(temp);
		else
			myData.insert2(temp);
		if(myData.type == 1)
			myData2.insert1(temp);
		else
			myData2.insert2(temp);

	}

	goto here2;

	return 0;
}

