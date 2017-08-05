#ifndef _BufferManager_H_
#define _BufferManager_H_
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <vector>
#include <iostream>
#include <list>
#define BlockSize 4096
using namespace std;
//the structure of a block 
struct Block
{
	string tablename;
	char* record;
	bool written;
	bool accessed;
	int offset; 
};
//the class of the buffermanager
class MBuffer{
	public:
		Block* Buffer[64];//64 blocks in the buffer
	public:
		MBuffer(){
			int i;
			for(i=0;i<64;i++)
				Buffer[i]=NULL;
		};
		bool Init();//Initialize the buffer
		Block* GBlock();//Get a new block 
		Block* GetBlock(string tablename, int offset, int flag);//Get a block from the buffer 
	 	bool Sche1(string tablename, int offset);//to schedule the buffer when the block is from file
	 	bool Sche2(string tablename, int offset);//to schedule the buffer when the block is not from file
	 	bool Drop(string tablename);//to delete the blocks of the dropped table
	 	bool Exchange(string tablename, int offset, Block* Repalced);//exchange the block between the buffer and the file 
        void clearBuffer();//clear the buffer
	 	~MBuffer();
};
int Block_num (string file_name);//calculate the number of block of a file
#endif 
