#include "myshell.h"

int back_bat=0;//后台批处理判断 
char batchfile[MAX_PATH];//批处理文件名 
int isbat=0;//批处理标志 
char *open;//重定向类型 

int Trans(char *buf,char **args,int *states,Redirect *Input,Redirect *Output)//解译函数 
{
	int i,flag,argc,error;
	char c;
	states[0]=states[1]=states[2]=states[3]=0;//状态归零，states[0]是后台标志，states[1]是输入重定向标志，states[2]是输出重定向标志，states[3]是参数个数 
	error=argc=0;
	args[0]=NULL;
	open=NULL;
	flag=1;
	i=-1;
	while(buf[++i]&&buf[i]!='#')//循环读取一行指令 
	{
		c=buf[i];
		switch(c)
		{
			case '<'://输入重定向 
				if(flag==0)
				{
					flag=1;
					buf[i]='\0';
				}
				open="<";
				while(buf[++i]==' '|| buf[i]=='\t');//清除多余的空格和tab 
				if(buf[i]<32|| buf[i]=='#')//重定向后的字符不对 
				{
				 	error=1;
					printf("Error!\n");  
				 	break;
				}
				else if(buf[i]=='&'|| buf[i]=='<'|| buf[i]=='>'|| buf[i]=='|'|| buf[i]==';') //重定向后的字符不对 
                {      
                    error=1;
		    		printf("Error!\n");     
                    break;   
                }                     
				states[1]=1;//输入重定向标记 
				i--;
				break;
			case '>'://输出重定向 
				if(flag==0)
				{
					flag=1;
					buf[i]='\0';
				} 
				if(buf[i+1]=='>')//判断是否是‘>>’ 
				{
					buf[++i]='\0';
					open=">>";
				}
				else
					open=">";
				while(buf[++i]==' '|| buf[i]=='\t');//清除多余的空格和tab 
				if(buf[i]<32|| buf[i]=='#')//判断重定向符号后的字符是否正确 
				{
				 	error=1;
					printf("Error!\n");  
				 	break;
				}
				else if(buf[i]=='&'|| buf[i]=='<'|| buf[i]=='>'|| buf[i]=='|'|| buf[i]==';') 
                {       
                    error=1; 
	            printf("Error!\n");    
                    break;   
                }
				i--;
				states[2]=1;//输出重定向标记
				break;
			case'&'://后台模式 
				if(flag==0)
				{
					flag=1;
					buf[i]='\0';
			    }
				if(states[0])
				{
				error=1;
            			printf("Error!\n");                             
                break;    	
				}
				states[0]=1;//后台模式标记 
				break;
			case' '://清除空格和tab 
			case'\t':
				if(flag==0)
				{
					flag=1;
					buf[i]='\0';
			    }
				while(buf[++i]==' '|| buf[i]=='\t');
				i--;
				break;
			case'\n':
			case'\r':
				buf[i]='\0';
				i--;
				break;//换行即跳出循环 
			default:
				if(flag)
				{
					flag=0;
					if(open)//给重定向结构体赋值 
					{
						if(!strcmp(open,"<" ))
							Input->filename=buf+i;
						else  if( !strcmp(open,">>")) 
						{  
                        	strcpy(Output->opentype,"a");   
                            strcpy(Output->open,">>");   
                        	Output->filename=buf+i;  
                        }  
                        else  if( !strcmp(open,">" ) )  
                        {  
                        	strcpy(Output->opentype,"w");   
                            strcpy(Output->open,">");    
                            Output->filename=buf+i;  
                    	}   
                        open=NULL;  	
					}
					else
						args[argc++]=buf+i;
				}
		}
	}
	args[argc]=NULL;
	states[3]=argc;//读取参数个数			
	if(error||argc==0)//如果参数个数为零则报错 
		printf("Error!\n");  ;		  
	return error;	
}
int Execute(char *buf)//执行函数 
{
	pid_t pid;//进程号 
	char *args[MAX_ARGS];//参数 
	int error;
	int states[4];//状态标记 
	Redirect Input;//重定向      
	Redirect Output;
	error=Trans(buf,args,states,&Input,&Output);
	if(error||args[0]==NULL) //如果无参数就返回 
		return -1;
	if(!strcmp(args[0],"quit"))//如果是quit指令就结束myshell 
	{
		if(args[1])
			printf("Error!\n"); 
		fprintf(stderr,"Thanks for using!\n");
		exit(0);
	}
	else if(states[0])//如果是后台模式 
	{
		switch (pid=fork())//fork出父子进程 
		{
			case -1: printf("Error!\n");  
			case 0: sleep(1);//延时1秒 
					fprintf(stderr,"\n");
					Select(args,Input,Output,states);//进入判断函数 
					exit(1);
			default: if(isbat==0)
					 	fprintf(stderr,"pid=%d\n",pid); //显示进程号				
		}
	}
	else
		Select(args,Input,Output,states);//进入判断函数 
	return 0;
}
int Shell(FILE *input,const int *states)//入口函数 
{
	char buf[MAX_BUFFER];
	do
	{
		if(input==stdin)
			fprintf(stderr,"\n[%s@%s]:$",getenv("USERNAME"),getenv("PWD"));//命令行输入前的显示 
		if(fgets(buf,MAX_BUFFER,input))//逐行读取 
		{
			Execute(buf);//执行 
		}
	}while (!feof(input));
	return 0;
}
int Select(char **args,const Redirect Input,const Redirect Output,int *states)//判断函数 
{
	char filepath[MAX_PATH],parent[MAX_ARGS];
	FILE *output=NULL,*input;
	pid_t newpid;
	int flag;
	if(!strcmp(args[0],"myshell"))//如果是myshell 
	{
		flag=0;
		if(isbat)//如果是批处理 
		{
			switch(newpid=fork())//fork出父子进程 
			{
				case -1:printf("Error!\n");  
				case 0:     
                    if(states[0]&&(args[1]))   
                    {  
						back_bat++;    
                        flag=1;   
                    }                                                                                
                    Bat(args,states);   //在后台执行        
                    if(flag)   back_bat--;      
                    exit (0);   
                default:  waitpid(newpid, NULL, WUNTRACED); //等到子进程结束再执行   
			}
		}
		else
		{
		if(states[0]&&(args[1])) //如果是后台进程  
        {            
			back_bat++;   
            flag=1;   
        }     
        Bat(args,states);       //执行   
        if(flag)   back_bat--;   	
		}
		if(states[0]) 
			exit(1);      
        else   
			return 0;     
    }   
	if(states[2])  //如果有输出重新定向  
    {   
    	Fullpath(filepath, Output.filename);   //取出完整地址 
    	output=freopen(filepath, Output.opentype,stdout);   //打开文件 
        if(output==NULL)    
    	{       
			printf("Error!\n");                   
            if(states[0]) 
				exit(1);      
            else    
				return -4;     
    	}                        
    }       
	if(!strcmp(args[0],"cd"))    //cd                
    	Cd(args,states);    
   
    else if(!strcmp(args[0],"clr"))  //clr 
    {             
            Clear();                                              
        if(args[1]||states[1]||states[2]) //如果有参数或重定向就报错   
        	printf("Error!\n");       
    }   
    
    else if (!strcmp(args[0],"dir"))   //dir
        Dir(args,Output,states);   
                   
    else if (!strcmp(args[0],"echo"))    //echo 
        Echo(args,Input,Output,states);    
    else if (!strcmp(args[0],"environ"))  //environ
    {     
        Environ(Output,states);   
        if(states[1])   //如果有输入重定向就报错  
            printf("Error!\n");    
        if(args[1])    //如果有参数就报错 
            printf("Error!\n");     
    }                   
    else if (!strcmp(args[0],"help"))   //help 
    {        
	if(args[1])	//如果有参数就执行，否则报错 
		Help(args,Output,states);
	else
		 printf("Error!\n");
        if(states[1])      
            printf("Error!\n");    
    }    
    else  if (!strcmp(args[0],"pwd"))  //pwd     
    {          
        Pwd();  
        if(states[1])//如果有输入重定向或参数就报错 
            printf("Error!\n");  
        if(args[1])    
            printf("Error!\n");                   
    }   
    else 
	{   
        strcpy(parent,"parent=") ;  //父进程路径         
        strcat(parent, getenv("shell"));        
        switch (newpid=fork( ))  //fork出两个进程 
        {      
            case -1:   
				 printf("Error!\n");     
            case 0:                                                  
                if(states[1])   //如果有输入重定向 
                {            
					Fullpath(filepath,Input.filename);   //取出全路径                                                                       
					input=freopen(filepath,"r",stdin);  //打开文件 
                    if(input==NULL)    
                    {    
				printf("Error!\n");       
                        exit(1);   
					}   
                }        
                putenv(parent);   //添加父进程路径                                     
                execvp(args[0],args);                                                                                          
                printf("Error!\n");     
                sleep(1);        
                if(input)  
					fclose(input);                                 
                exit(0);   
            default:     
                if(states[0]==0)  //如果不是后台 
                    waitpid(newpid, NULL, WUNTRACED);                                                                      
        }     
    }          
           
    if(output)   //如果有输出重定向则关闭文件 
    {    
		fclose(output);   
        freopen("/dev/tty","w",stdout);   
    }            
    if(states[0]) 
		exit(0);      
    else 
		return 0;    
}
void Fullpath(char *fullpath,char *shortpath) //全路径函数 
{
	int i,j;    
    i=j=0;         
    fullpath[0]=0;    
    char *old_dir, *current_dir; //老路径和当前路径  
    if(shortpath[0]=='~')   //若果是‘~’ 
    {   
    	strcpy(fullpath, getenv("HOME"));//则添加HOME地址   
        j=strlen(fullpath);        
        i=1;    
    }      
    else  if(shortpath[0]=='.'&&shortpath[1]=='.') //若果是‘..’ 
    {                    
        old_dir=getenv("PWD");  
        chdir("..");                   
        current_dir=(char *)malloc(MAX_BUFFER);
		getcwd(current_dir,MAX_BUFFER);   
        strcpy(fullpath, current_dir); //则添加上一级地址   
        j=strlen(fullpath);        
        i=2;     
        chdir(old_dir);  
    }  
    else   if(shortpath[0]=='.')//如果是'.' 
    {      
        strcpy(fullpath, getenv("PWD")); //就添加当前目录  
    	j=strlen(fullpath);        
        i=1;   
    }  
    else if(shortpath[0]!='/')    //如果不是特殊符号 
    {         
        strcpy(fullpath, getenv("PWD")); //就添加当前地址 
        strcat(fullpath, "/");   
        j=strlen(fullpath);                       
        i=0;   
    }  
    strcat(fullpath+j,shortpath+i);    
    return;    
}
int Cd(char **args,int *states)//cd 
{      
    char dirpath[MAX_PATH],filepath[MAX_PATH],dirname[MAX_PATH];  
    char  *current_dir;    
    int i,flag;   
    FILE *inputfile;               
    if(args[1])   //如果有参数 
    {                        
    	if(args[2]) //有两个参数就报错                       
            printf("Error!\n");            
        Fullpath(dirpath,args[1]);    //获得参数1的全地址                                                                                         
    }   
    else   //否则显示当前目录 
	{    
		fprintf(stdout,"%s\n",getenv("PWD"));   
        return 0;   
    }   
    flag=chdir(dirpath);  
    if(flag)  //如果目录不存在                         
    {      
		 printf("Error!\n");              
        return -2;    
    }     
    current_dir=(char *)malloc(MAX_BUFFER);   
    if(!current_dir)           
		printf("Error!\n");                 
    getcwd(current_dir,MAX_BUFFER);   
    setenv("PWD",current_dir,1);  //修改PWD为当前目录 
    free(current_dir);   
    return 0;   
}
void Clear(void)//clear 
{         
    pid_t newpid;   //进程号 
    switch (newpid=fork( ))   //fork出父子进程 
    {   
        case -1:   
            printf("Error!\n");     
        case 0:       
            execlp("clear","1",NULL);//子进程执行    
            printf("Error!\n");                                            
        default:      
			waitpid(newpid, NULL, WUNTRACED);   
            fprintf(stderr,"Clear!\n");    
    }     
	return;   
}
int Dir(char **args,const Redirect Output,int *states)//dir 
{   
	   
    pid_t newpid;  //进程号 
    DIR *pdir;  
    int i;   
    char filepath[MAX_PATH],dirpath[MAX_PATH],dirname[MAX_PATH];  
    if(args[1])     //如果有参数 
    {                        
    	if(args[2])   // 超过一个参数就报错                        
               printf("Error!\n");              
        Fullpath(dirpath,args[1]);  //取全路径                                                                                           
    }  
    else 
	strcpy(dirpath, ".");   
    pdir=opendir(dirpath);                
    if(pdir==NULL)//如果目录不存在也报错                      
    {         
		printf("Error!\n");           
        return -2 ;   
    }   
    switch (newpid=fork( ))   //fork出父子进程 
    {   
        case -1:   
            printf("Error!\n");    
        case 0:                                                                                       
            execlp( "ls","ls" ,"-al", dirpath, NULL);  //在子进程中执行                                   
            printf("Error!\n");                                                        
        default:   
			waitpid(newpid, NULL, WUNTRACED);   
    }           
    return 0;   
} 
int Echo(char **args,const Redirect Input,const Redirect Output,int *states)//echo 
{   
	FILE *input,*output;    
    char filepath[MAX_PATH];   
    char buf[MAX_BUFFER];   
    int k;
	if(states[2])//如果输出重定向 
	{
		Fullpath(filepath,Output.filename); //取全路径  
    	output=freopen(filepath,Output.opentype,stdout);  //打开文件      	
		fprintf(stderr,"\nThe results will be writen into file \"%s\".\n",Output.filename);                
	
	}  
    if(states[1]) //如果输入重定向   
    {   
		if(args[1])    // 如果还有参数就报错      
             printf("Error!\n");                         
        Fullpath(filepath,Input.filename);  //取全路径 
        input=fopen(filepath,"r");   
        if(input==NULL)    
        {     
		printf("Error!\n");                     
            return -2;    
        }   
        if(states[2]==0)//如果只有输入重定向     
        	fprintf(stderr,"The contents of file \"%s\" is as follows:\n",Input.filename);   
        while (!feof(input))    // 读文件   
        {   
            if(fgets(buf, MAX_BUFFER, input))   
            	fprintf(stdout,"%s",buf); //打印   
        }                             
        fclose(input);//关闭读文件   
        fprintf(stdout,"\n");                                
    }   
           
    else 
	{       
        if(args[1])   //如果有参数 
        {    
			for(k=1;k<states[3]-1;k++)     //循环显示 
                fprintf(stdout,"%s ",args[k]);     
            fprintf(stdout,"%s",args[k]);     
        }     
        fprintf(stdout,"\n");                                                                                                    
    }       
    return 0;   
}   
int Environ(const Redirect Output,int *states)//environ 
{
	FILE *output;         
    char filepath[MAX_PATH];    
    char ** env = environ;//取全环境变量 
    if(states[2])
	{
		Fullpath(filepath,Output.filename);   //取全路径 
    	output=freopen(filepath,Output.opentype,stdout); //打开文件       	
		fprintf(stderr,"\nThe results will be writen into file \"%s\".\n",Output.filename);                
	}
	while(*env) 
		fprintf(stdout,"%s\n",*env++); //输出                                     
    return 0;           
}   
int Pwd(void)//pwd
{                             
    fprintf(stdout,"%s\n",getenv("PWD"));//读PWD                          
    return 0;   
}
int Help(char **args,const Redirect Output,int *states)//help 
{           
	FILE *readme;   
    char buffer[MAX_BUFFER];     
    char keyword[MAX_BUFFER]="<help ";      
    int i,len;       
    strcat(keyword,args[1]);   
    strcat(keyword," ");      
    len=strlen(keyword);   
    keyword[len-1]='>';   
    keyword[len]='\0';  //取出标记keyword           
    if(!strcmp(keyword,"<help more>"))   //如果是help more 
    {  
        strcpy(buffer,"more ");  
        strcat(buffer,getenv("readme_path"));  //调用more指令 
        for(i=0;i<states[2];i++)  
        {  
			strcat(buffer,Output.open);  
            strcat(buffer,Output.filename);  
        }    
        Execute(buffer);                         
        return 0;    
    }  
    readme=fopen(getenv("readme_path"),"r");   //打开readme   
    while(!feof(readme)&&fgets(buffer,MAX_BUFFER,readme))   
    {   
		if(strstr(buffer,keyword))   //如果碰到标记为就停下 
    		break;  
    }      
    while(!feof(readme)&&fgets(buffer,MAX_BUFFER,readme))  //读文件直到'#' 
    {   
        if(buffer[0]=='#')   
            break;   
        fputs(buffer,stdout);      
    }      
    if(feof(readme))  //如果读到底就报错 
    {    
		keyword[len-1]='\0';     
          printf("Error!\n");            
    }   
    if(readme) 
		fclose(readme);   
    return 0;   
}   
int Bat(char **args,int *states)//myshell 
{      
	FILE *input;  
    char filepath[MAX_PATH];    
    int i=0;  
    char fullpath_batchfile[MAX_PATH];   
    pid_t  newpid;    
    if(isbat)   //如果是批处理 
        fprintf(stderr,"***Line of inputfile \"%s\": ",batchfile);         
    if(args[1]) //如果有参数 
    {         
        if(args[2])  //超过一个就报错                        
             printf("Error!\n");                                 
		Fullpath(filepath,args[1]);  //取全路径      
        Fullpath(fullpath_batchfile,batchfile);  
        if(isbat && !strcmp(fullpath_batchfile,filepath)) 
        {   
            fprintf(stderr,"Warning: commands not execute, it will cause infinite loop!!!\n");                
            return -5;    
        }   
        input=fopen(filepath,"r");//读文件 
        if(input==NULL) 
        {         
		printf("Error!\n");         
            return -2;    
        }   
		isbat=1;     
        strcpy(batchfile,args[1]);  //拷贝文件地址   
        fprintf(stderr,"Turn to execute the commands in batch file \"%s\" :\n",batchfile);                                         
    	Shell(input,states);     //开始循环读取 
        fclose(input);   
        fprintf(stderr,"\nExecution of batch file \"%s\" is finished!\n",batchfile);   
	isbat=0;          
    }     
    else       //否则提示输入文件地址 
        fprintf(stdout,"Please input the filename!\n");         
    return 0;   
}   
