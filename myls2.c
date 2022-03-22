/*
***-a 列出目录下的所有文件，包括以.开头的隐含文件
***-l 列出文件的详细信息（包括文件属性和权限等）
***-R 使用递归连同目录中的子目录中的文件显示出来，如果要显示隐藏文件就要添加-a参数
   （列出所有子目录下的文件）
***-t 按修改时间进行排序，先显示最后编辑的文件
***-r 对目录反向排序（以目录的每个首字符所对应的ASCII值进行大到小排序）
***-i 输出文件的i节点的索引信息
-s 在每个文件名后输出该文件的大小*/
#include "command_execute.h"
#include "command_input.h"
#include<stdio.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<unistd.h>
#include<string.h>
#include<errno.h>
#include<pwd.h>
#include<grp.h>
#include<time.h>
#include<dirent.h>
#include<glob.h>
#include<stdlib.h>

/*命令判断函数
ready_command传入待比较的命令 ，n_comm传入需要比较的命令，ready_size传入前缀已经匹配的字符串*/
bool command_explain(const char *ready_command, const char *n_comm)
{
    int r_size = strlen(ready_command);
    int n_size = strlen(n_comm);
    if (r_size != n_size)
    {
        return false;
    }
    int i = 0;
    for (i = 0; i < r_size; i++)
    {
        if (ready_command[i] != n_comm[i])
        {
            return false;
        }
    }
    return true;
}
void listdir_choice(const char *command)
{
    if (command_explain("ls",command)||command_explain("ls -a",command)||command_explain("ls -c", command))
    {
        ls();
    }
    else if(command_explain("ls -al",command))
    {
        ls__al();
    }
    else if((!command_explain("ls -al ",command)))
    {
        ls_al_filename(command);
    }
}
void ls()
{
    DIR *dir;
    struct dirent *ptr;
    char ch = '0';
    char buf[32] = {0};
    char pathname_buf[256];
    getcwd(pathname_buf,sizeof(pathname_buf));
    //获取当前路径
    dir=opendir(pathname_buf);
    struct stat s_buf;
    int stat_buf;
    int i=0;
    while ((ptr = readdir(dir)) != NULL)
    {
        stat_buf=stat(ptr->d_name, &s_buf);
        if (S_ISDIR(s_buf.st_mode))
        {
            //目录文件打印
            color_set(BLUE);
            printf("%-20s", ptr->d_name);
            i++;
        }
        else if (s_buf.st_mode & S_IXGRP)
        {
            //可执行文件打印
            color_set(GREEN);
            printf("%-20s", ptr->d_name);
            i++;
        }
        else if (S_ISREG(s_buf.st_mode))
        {
            //普通文件打印
            color_set(WHITE);
            printf("%-20s", ptr->d_name);
            i++;
        }
        if (i==5) //对齐换行
        {
            printf("\n");
            i=0;
        }
    }
    printf("\n");
    closedir(dir);
}

void ls__al()
{
    struct stat s_buf;
    char name[128];
    DIR *dir;

    char pathname_buf[256];
    getcwd(pathname_buf, sizeof(pathname_buf));

    //获取当前路径
    dir=opendir(pathname_buf);
    struct dirent *ptr;
    while ((ptr=readdir(dir)) != NULL)
    {
        lstat(ptr->d_name,&s_buf);
        ls_al_ana(&s_buf,ptr->d_name);
        printf("\n");
    }
    closedir(dir);
}

//-al命令对文件的仔细分析
void ls_al_ana(struct stat *buf, const char *file_name)
{
    //文件属性
    if (S_ISLNK(buf->st_mode))
    {
        printf("l"); //链接文件
    }
    else if (S_ISREG(buf->st_mode))
    {
        printf("-"); //文件
    }
    else if (S_ISCHR(buf->st_mode))
    {
        printf("d"); //目录文件
    }
    else if (S_ISDIR(buf->st_mode))
    {
        printf("c"); //串行端口设备
    }
    else if (S_ISFIFO(buf->st_mode))
    {
        printf("f"); //FIFO文件
    }
    else if (S_ISSOCK(buf->st_mode))
    {
        printf("s"); //套接字文件
    }

    //文件权限

    // 1.拥有者权限打印
    if (buf->st_mode & S_IRGRP)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }

    if (buf->st_mode & S_IWGRP)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }

    if (buf->st_mode & S_IXGRP)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    // 2.用户组权限打印
    if (buf->st_mode & S_IRGRP)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }
    if (buf->st_mode & S_IWGRP)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }
    if (buf->st_mode & S_IXGRP)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    // 3.其他用户权限打印
    if (buf->st_mode & S_IROTH)
    {
        printf("r");
    }
    else
    {
        printf("-");
    }
    if (buf->st_mode & S_IWOTH)
    {
        printf("w");
    }
    else
    {
        printf("-");
    }
    if (buf->st_mode & S_IXOTH)
    {
        printf("x");
    }
    else
    {
        printf("-");
    }

    // 空格打印
    printf("  ");

    struct passwd *psd = getpwuid(buf->st_uid);
    struct group *grp = getgrgid(buf->st_gid);

    printf("%-3ld", buf->st_nlink);
    printf("%-8s", psd->pw_name);
    printf("%-8s", grp->gr_name);

    printf("%6ld", buf->st_size);
    char buf_time[32];
    strcpy(buf_time, ctime(&(buf->st_mtime)));
    buf_time[strlen(buf_time) - 1] = '\0';

    printf("   %s", buf_time);
    if (S_ISDIR(buf->st_mode))
    {
        //目录文件打印
        color_set(BLUE);
        printf(" %s", file_name);
        color_set(WHITE);
    }
    else if (buf->st_mode & S_IXGRP)
    {
        //可执行文件打印
        color_set(GREEN);
        printf(" %s", file_name);
        color_set(WHITE);
    }
    else if (S_ISREG(buf->st_mode))
    {
        //一般文件打印
        color_set(WHITE);
        printf(" %s", file_name);
        color_set(WHITE);
    }
}

// ls -al filename
void ls_al_filename(const char *command)
{
    struct stat s_buf;
    DIR *dir;
    char pathname_buf[256];
    getcwd(pathname_buf, sizeof(pathname_buf)); //获取当前路径
    dir = opendir(pathname_buf);
    struct dirent *ptr;
    char filename[1024];
    int i = 0;
    for (; i < 64 - 1; i++)
    {
        filename[i] = command[i + 7];
        if (filename[i] == 10)
        {
            break;
        }
    }
    filename[i] = '\n';
    fflush(stdin);
    while ((ptr = readdir(dir)) != NULL)
    {
        lstat(ptr->d_name, &s_buf);
        if (command_explain(filename, ptr->d_name))
        {
            ls_al_ana(&s_buf, ptr->d_name);
            printf("\n");
            closedir(dir);
            return;
        }
    }
    if (ptr==NULL)
    {
        printf("抱歉，没有找到该文件。\n");
    }
    closedir(dir);
    return;
}

//  -r
const char* getpathname_for_ls_r(const char* filename,char* pathname)
{
    getcwd(pathname,sizeof(filename));
    int cho = strlen(filename);
    int i=0;
    for( ;i<sizeof(filename);cho++,i++)
    {
        pathname[cho] = filename[i];
    }
    return pathname;
}

//  -R
int ls_R(const char *path)
{
    memset(path,'\0',sizeof(path));
    getcwd(path, 999);
    printf("现在的目录是: %s\n",path);
}

int ls_R_1(char *basePath)
{
    DIR *dir;
    struct dirent *ptr;
    char base[1000];
    if ((dir=opendir(basePath))==NULL)//错误
    {
        perror("error");
        exit(1); //退出
    }
    while ((ptr=readdir(dir)) != NULL)
    {
        if(strcmp(ptr->d_name,".")==0 || strcmp(ptr->d_name,"..")==0)    //当前目录或父目录
            continue;
        else if(ptr->d_type == 8)    ///文件
            printf("d_name:%s/%s\n",basePath,ptr->d_name);
        else if(ptr->d_type == 10)    ///链接文件
            printf("d_name:%s/%s\n",basePath,ptr->d_name);
        else if(ptr->d_type == 4)    ///目录
        {
            memset(base,'\0',sizeof(base));
            strcpy(base,basePath);
            strcat(base,"/");
            strcat(base,ptr->d_name);
            ls_R(base);
        }
    }
    closedir(dir);
    return 1;
}

//  -i
int ls_i(const char *path)
{
    struct stat mystat;
    glob_t myglob;
    char buf[1024]={};
    if(lstat(path,&mystat)<0)
    {
        perror("lstat()");
        return -1;
    }
    if(!S_ISDIR(mystat.st_mode))
    {
        printf("%ld  %s\n",mystat.st_ino,path);
    }
    strcpy(buf,path);
    strcat(buf,"/*");
    glob(buf,0,NULL,&myglob);

    for(int i=0;i<myglob.gl_pathc;i++)
    {
        if(lstat(((myglob.gl_pathv)[i]),&mystat)<0)
        {
            perror("lstat()");
            return -1;
        }
        char *o=strrchr((myglob.gl_pathv)[i],'/');
        printf("%ld  %s\n",mystat.st_ino,++o);

    }
    globfree(&myglob);

}

int main(int argc, char *argv[])
{
    int c;
    char*name[1024] = {}; //保存所有文件名称
    char s[1024];
    char *optstring = getcwd( s, sizeof(s));
    DIR *dir = opendir( optstring );

    if(argc < 2) return 1;
    c =getopt(argc, argv, optstring); //解析命令行选项参数
    if(c == -1) return ;
    switch(c)
    {
        case 'l':
            ls_l(argv[2]);
            break;
        case 'a':
            ls_a(argv[2]);
            break;
        case 'i':
            ls_i(argv[2]);
            break;
        case 'R':
            ls_R(argv[2]);
            break;
        case '?':
            printf("不认识此选项%s\n", argv[optind -1]);
            break;
        default:
            break;
    }
    return 0;
}
int main()
{ 
	char* name[1024]={}; //保存所有文件名称
	char s[1024]={};
	char* file=getcwd(s,sizeof(s));
	DIR* dp=opendir(file);
	
	if(dp==NULL)
	{
		perror("opendir"); //显示文件打开错误信息
		exit(EXIT_FAILURE);
	}
	int cnt=0;
	struct dirent* dr=readdir(dp);
	for(;dr;dr=readdir(dp))
	{
		if(dr->d_name[0]=='.') 
			continue;//-a
		name[cnt++]=dr->d_name;
	}

	long *fileTime[1024]={};
	for(int i=0;i<cnt;i++)
	{
		struct stat buf={};
		stat((char*)name[i],&buf);
		fileTime[i]=(long*)buf.st_mtime;
		for(int j=i+1;j<cnt;j++)
		{
			stat((char*)name[j],&buf);
			fileTime[j]=(long*)buf.st_mtime;
			if(fileTime[i]<fileTime[j])
			{
				
				long *t=fileTime[i];
				fileTime[i]=fileTime[j];
				fileTime[j]=t;
				
				char *temp=name[i];
				name[i]=name[j];
				name[j]=temp;
			}
		}
	}
	closedir(dp);
	return 0;
}
