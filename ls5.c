/*
struct stat name
{
    dev_t     st_dev;    //文件的设备编号
    int_t     st_ino;     //节点
    mode_t    st_mode;   //文件的类型和存取的权限
    nlink_t   st_nlink;     //连到该文件的硬连接数目，刚建立的文件值为1
    uid_t     st_uid;        //用户ID
    gid_t     st_gid;        //组ID
    dev_t     st_rdev;     //(设备类型)若此文件为设备文件，则为其设备编号
    offf_t    st_size;      //文件字节数(文件大小)
    unsigned  long st_bilsize;   //块大小（文件系统的I/O缓存区大小）
    unsigend  long st_blocks;   //块数
    time_t    st_atime;  //最后一次访问时间
    time_t    st_mtime; //最后一次修改时间
    time_t    st_ctime;   //最后一次改变时间(指属性)
};*/
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
#include<stdbool.h>

#define A 0
#define L 1
#define R 2
#define I 3
#define S 4
#define T 5

void LS_I(struct stat *STA);
void MODE(int mode, char str[]);
char* UID(uid_t uid);
char* GID(gid_t gid);

//获取当前路径打印文件名
void list(bool* name,int size)
{
    DIR *dir =NULL;
    struct dirent *ptr;
    char ch = '0';
    char buf[32] = {0};
    char pathname_buf[256];

    getcwd(pathname_buf,sizeof(pathname_buf));  //获取当前路径
    dir=opendir(pathname_buf);  //返回目录流

    if(dir == NULL) //如果路径为空，返回错误
    {
        printf("error!cannot open the file\n");
        exit(-1);
    }
    struct stat s_buf;
    int stat_buf;
    int i=0;
    
    while((ptr = readdir(dir)) != NULL)
    {
        stat_buf=stat( ptr->d_name, &s_buf);
        if(name[I] == true)
        {
            LS_I(&s_buf);
        }
        printf("%-20s", ptr->d_name);
        i++;
        if(name[L] == false)
        {
            if(i % 5 == 0)
            {
                printf("\n");
            }
        }else if(name[L] == true)
        {
            show_file(ptr->d_name, &s_buf);
        }
        if(name[T] == true)
        {
            LS_T(&s_buf);
        }
        if(name[S] == true)
        {
            LS_S(&s_buf);
        }
        
    }
    printf("\n");
    closedir(dir);
}

// 展示单个文件的详细信息 -l 
void show_file(char* filename, struct stat* STA)  
{
    char modestr[11];  

    //权限
    MODE(STA->st_mode, modestr);  
    //连到该文件的硬连接数目，刚建立的文件值为1
    printf(" %d", (int) STA->st_nlink);
    //用户
    printf(" %s", UID(STA->st_uid));
    //用户组
    printf(" %s", GID(STA->st_gid));
    //文件大小
    printf(" %ld", (long) STA->st_size);
    //ctime:最后一次改变文件内容或目录内容的时间
    printf(" %s", 4 + ctime(&STA->st_mtime));
    //文件名字（颜色未完成）
    printf(" %s\n", filename);
}

//文件权限
void MODE(int mode, char str[])
{  
    strcpy(str, "----------");  //初始化全为---------- 
      
    if(S_ISDIR(mode))   //是否为目录
    {  
        str[0] = 'd';  
    }  
      
    if(S_ISCHR(mode))  //是否为字符设置
    {  
        str[0] = 'c';  
    }  
      
    if(S_ISBLK(mode))  //是否为块设备
    {  
        str[0] = 'b';  
    }

    //逻辑与
    /*
        S_IRUSR：用户读权限
        S_IWUSR：用户写权限
        S_IRGRP：用户组读权限
        S_IWGRP：用户组写权限
        S_IROTH：其他组都权限
        S_IWOTH：其他组写权限
    */
    if((mode & S_IRUSR))  
    {  
        str[1] = 'r';  
    }  
    if((mode & S_IWUSR))  
    {  
        str[2] = 'w';  
    }
    if((mode & S_IXUSR))  
    {  
        str[3] = 'x';  
    }  
    if((mode & S_IRGRP))  
    {  
        str[4] = 'r';  
    }  
    if((mode & S_IWGRP))  
    {  
        str[5] = 'w';  
    }
    if((mode & S_IXGRP))  
    {  
        str[6] = 'x';  
    }  
    if((mode & S_IROTH))  
    {  
        str[7] = 'r';  
    }  
    if((mode & S_IWOTH))  
    {  
        str[8] = 'w';  
    }  
    if((mode & S_IXOTH))  
    {  
        str[9] = 'x';  
    }  
} 

//通过uid和gid找到用户名字和用户组名字    
char* UID(uid_t uid)
{  
    struct passwd* getpwuid(),* pw_ptr;  
    static char numstr[10];  
      
    if((pw_ptr = getpwuid(uid)) == NULL)  
    {  
        sprintf(numstr,"%d",uid);
        return numstr;
    }  
    else  
    {  
        return pw_ptr->pw_name;
    }  
}       
char* GID(gid_t gid)
{  
    struct group* getgrgid(),* grp_ptr;  
    static char numstr[10];  
      
    if(( grp_ptr = getgrgid(gid)) == NULL)  
    {  
        sprintf(numstr,"%d",gid);  
        return numstr;  
    }  
    else  
    {  
        return grp_ptr->gr_name;  
    }  
}  

//打印i节点
void LS_I(struct stat *STA)
{
    printf("%ld ", STA->st_ino);
}

//文件大小 
void LS_S(struct stat *STA)
{
    printf("%ld ",( long )STA->st_size);
}

//-t 按时间排序
void LS_T(struct stat *STA)
{
    char* name[1000]={};  //文件名字
	long *filetime[1000]={}; //文件修改时间

	for(int i=0;i<6;i++) //文件个数（？）
	{
		struct stat buf={};
		stat((char*)name[i],&buf);
		filetime[i]=(long*)buf.st_mtime;

		for(int j=i+1;j<6;j++)
		{
			stat((char*)name[j],&buf);
			filetime[j]=(long*)buf.st_mtime;

			if(filetime[i]<filetime[j]) //时间从大到小排序
			{
				long *t=filetime[i];
				filetime[i]=filetime[j];
				filetime[j]=t;
				
				char *temp=name[i];
				name[i]=name[j];
				name[j]=temp;
			}
		}
	}
    //printf("%s ",name);
    //返回(?)
}

int main(int argc,char** argv)
{
    // ./ls5 -xxxxx
    int i;
    int command[10]={0};
    int num=strlen(argv[1]);
    printf("%s %s",argv[0],argv[1]);
    for(i=0;i<num;i++)
    {
        if(argv[1][i]=='a')
        {
            command[A]++;
        }else if(argv[1][i]=='l')
        {
            command[L]++;
        }else if(argv[1][i]=='R')
        {
            command[R]++;
        }else if(argv[1][i]=='i')
        {
            command[I]++;
        }else if(argv[1][i]=='s')
        {
            command[S]++;
        }else if(argv[1][i]=='t')
        {
            command[T]++;
        }else{
            printf("错误！\n");
            return -1;
        }
    }

    /*for(i=0;i<6;i++)
    {
        printf("\n%d ",command[i]);
    }*/

    bool sum[6]={false};  //布尔数组判断其真值
    for(i=0;i<6;i++)
    {
        if(command[i]!=0)
        {
            sum[i]=true;
        }
        //printf("\n%d ",sum[i]);
    }
    
    list(sum,6);
    return 0;
}
