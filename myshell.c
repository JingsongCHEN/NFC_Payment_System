#include "myshell.h"

int main(void)
{
	char pwd[MAX_ARGS];//当前的工作地址 
	char shell_path[MAX_ARGS]="shell=";//myshell的绝对地址 
	char readme_path[MAX_ARGS]="readme_path=";//readme的绝对地址 
	char newpath[MAX_ARGS*1000];
	strcpy(newpath,getenv("PATH"));
	strcat(newpath,":");
	strcpy(pwd,getenv("PWD"));
	strcat(newpath,pwd);
	setenv("PATH",newpath,1);//在PATH中添加当前地址 
	strcat(shell_path,pwd);
	strcat(shell_path,"/myshell");
	putenv(shell_path);//添加myshell的地址为环境变量 
	strcat(readme_path,pwd);
	strcat(readme_path,"/readme");
	putenv(readme_path);//添加readme的地址为环境变量 
	Clear();//清屏 
	fprintf(stderr,"Welcome to use myshell!\n");
	Shell(stdin,NULL);//进入shell 
	return 0; 
}
