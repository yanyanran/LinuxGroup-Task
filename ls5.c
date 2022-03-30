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
}
*/

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
#define BULE 34
#define GREEN 32

struct file
{
    char filename[256];
    struct stat STA;
};
void LS_I(struct stat *STA);
void LS_S(struct stat *STA);
void LS_T(struct file *STA,int len);
void MODE(int mode, char str[]);
char* UID(uid_t uid);
char* GID(gid_t gid);
void show_file(struct stat* STA);

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
    int i=0,j=0;
    
    //buf->st_mtime 最后一次修改时间

    struct file* buff = malloc(sizeof(struct file)*100);
    struct file* temp = buff;  //还原
    while((ptr = readdir(dir))!= NULL)
    {
        if(name[A] == false)
        {
            if(ptr->d_name[0] == '.')
            {
                continue;
            }
        }
        stat_buf=stat(ptr->d_name, &(buff->STA));
        strcpy(buff->filename,ptr->d_name);
        //printf("%s ", buff->filename);
        buff++; //指针后移
        j++; //记录文件数
    }
    buff = temp;  //指向开始
    if(name[T] == true)
    {
        LS_T(buff, j);
    }
    for(i=0;i<j;i++)
    {
        if(name[S] == true)
        {
            LS_S(&(buff+i)->STA);
        }
        if(name[I] == true)
        {
            LS_I(&(buff+i)->STA);
        }
        if(name[L] == true)
        {
            printf("\n");
            show_file(&(buff+i)->STA);
        }

        //输出
        if(S_ISDIR(buff->STA.st_mode))
        {
            //目录
            COLOR(BULE);
            printf("%5s",(buff+i)->filename);
        }else if(buff->STA.st_mode & S_IXGRP)
        {
            //可执行文件（位运算？）
            printf("....");
            COLOR(GREEN);
            printf("%5s",(buff+i)->filename);
        }else if(S_ISREG(buff->STA.st_mode))
        {
            //普通文件
            printf("......................");
            printf("   %5s",(buff+i)->filename);
        }
        //printf("  %5s",(buff+i)->filename);  //输出文件名

        if(i % 5 ==0 && name[L] != true)
        {
            printf("\n");
        }
    }
    if(name[R] == true)  //-R递归
    {
        int i=0;
        printf("\n");
        for(;i<j;i++)
        {
            printf("%s:\n",(buff+i)->filename);
            list(&(name[I]),3);
        }
    }
    printf("\n");
    closedir(dir);
}

/*
字颜色:
普通文件白色
32:绿色（可执行文件）
34:蓝色（目录文件）：函数S_ISDIR()判断
*/
void COLOR(int color)
{
    printf("\033[%dm",color);
}

// 展示单个文件的详细信息 -l 
void show_file(struct stat* STA)  
{
    char modestr[11];  //存放权限
    //权限
    MODE((int)STA->st_mode, modestr);
    //连到该文件的硬连接数目，刚建立的文件值为1
    printf(" %5d", (int) STA->st_nlink);
    //用户
    printf(" %5s", UID(STA->st_uid));
    //用户组
    printf(" %5s", GID(STA->st_gid));
    //文件大小
    printf(" %5ld", (long) STA->st_size);
    //ctime:最后一次改变文件内容或目录内容的时间
    char buf_time[32];
    strcpy(buf_time, ctime(&(STA->st_mtime)));
    buf_time[strlen(buf_time) - 1] = '\0';
    printf(" %5s",buf_time);
    //文件名字
    //printf(" %20s\n", filename);
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

    printf("%s ",str);
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

//打印i节点 -i
void LS_I(struct stat *STA)
{
    printf("%ld ", STA->st_ino);
}

//文件大小 -s
void LS_S(struct stat *STA)
{
    printf("%ld ",( long )STA->st_size);
}

//-t 进行排序
void LS_T(struct file *FILE,int len)
{
    for(int i=1;i<=len-1;i++)
    {

        for(int j=1;j<len-i;j++)
        {

            if(FILE[i].STA.st_mtime<FILE[j].STA.st_mtime)//char* a; a-> = (*a)
            {
				struct file n = FILE[i];
				FILE[i] = FILE[j];
				FILE[j] = n;
            }
        }
    }
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
