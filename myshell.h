#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h> 
#include <dirent.h> 
#include <ctype.h> 

#define MAX_BUFFER 1024 //一行中最多的字符数 
#define MAX_ARGS 64 //一条指令中最多的参数个数 
#define MAX_PATH 100 //一条路径中最多的字符数 
//输入重定向的结构体 
typedef struct
{
	char *filename;//重定向后的文件名 
	char opentype[3];//文件打开类型 
	char open[3];//重定向类型 
} Redirect;

extern char **environ;//环境变量 
//辅助函数 
int Trans(char *buf,char **args,int *states,Redirect *Input,Redirect *Output);//解译函数 
int Execute(char *buf);//执行函数 
int Shell(FILE *input,const int *states);//shell入口 
int Select(char **args,const Redirect Input,const Redirect Output,int *states);//判断函数 
void Fullpath(char *fullpath,char *shortpath);//全路径函数 
//内部指令函数 
int Cd(char **args, int *states);//cd
void Clear(void);//clear
int Dir(char **args,const Redirect Output,int *states);//dir
int Echo(char **args,const Redirect Input,const Redirect Output,int *states);//echo
int Environ(const Redirect Output,int *states); //environ
int Pwd(void);//pwd  
int Help(char **args,const Redirect Output,int *states);//help
int Bat(char **args,int *states);//myshell  
