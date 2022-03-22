/*
***-a 列出目录下的所有文件，包括以.开头的隐含文件
***-l 列出文件的详细信息（包括文件属性和权限等）
***-R 使用递归连同目录中的子目录中的文件显示出来，如果要显示隐藏文件就要添加-a参数
   （列出所有子目录下的文件）
-t 按修改时间进行排序，先显示最后编辑的文件
-r 对目录反向排序（以目录的每个首字符所对应的ASCII值进行大到小排序）
***-i 输出文件的i节点的索引信息
-s 在每个文件名后输出该文件的大小
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

#define SIZE 1024

static int num;

static char *buf_cat(const char *path, const char *name)
{
    char *bufcat = malloc(SIZE);
    memset(bufcat,'\0',SIZE);
    strcpy(bufcat,path);
    strcat(bufcat,"/");
    strcat(bufcat,name);
    return bufcat;
}

// 判断是否为隐藏文件
static int hide(const char *path)
{
    if(*path == '.') return 1;
    else return 0;
}

int ls_l_1(const char *path,const char *name) //列信息（ls-l）
{
    struct stat mystat;
    struct passwd *pwd = NULL;
    struct tm *tmp = NULL;
    struct group *grp = NULL;
    char *buf = NULL;

    buf = buf_cat(path,name);

    if(lstat(buf, &mystat) == -1)
    {
        //perror()输出错误原因
        perror("stat()");//stat()获取文件信息，成功获取返回0,失败返回-1
        return 1;
    }

    if(hide(name) == 0)
    {
        num +=mystat.st_blocks/2;
        switch(mystat.st_mode & S_IFMT)
        {
            case S_IFREG:
                printf("-");
                break;
            case S_IFBLK:
                printf("b");
                break;
            case S_IFDIR:
                printf("d");
                break;
            case S_IFCHR:
                printf("c");
                break;
            case S_IFSOCK:
                printf("s");
                break;
            case S_IFLNK:
                printf("l");
                break;
            case S_IFIFO:
                printf("p");
                break;
            default:
                break;
        }

        //所有者权限
        if(mystat.st_mode & S_IRUSR)
            putchar('r');
        else
            putchar('-');
        if(mystat.st_mode & S_IWUSR)
            putchar('w');
        else
            putchar('-');
        if(mystat.st_mode & S_IXUSR)
        {
            if(mystat.st_mode &S_ISUID)
            {
                putchar('s');
            }else
                putchar('x');
        }else
            putchar('-');

        //所属组权限
        if(mystat.st_mode & S_IRGRP)
            putchar('r');
        else
            putchar('-');
        if(mystat.st_mode & S_IWGRP)
            putchar('w');
        else
            putchar('-');
        if(mystat.st_mode & S_IXGRP)
        {
            if(mystat.st_mode &S_ISGID)
            {
                putchar('s');
            }else
                putchar('x');
        }else
            putchar('-');

        //其他人权限
        if(mystat.st_mode & S_IROTH)
            putchar('r');
        else
            putchar('-');
        if(mystat.st_mode & S_IWOTH)
            putchar('w');
        else
            putchar('-');
        if(mystat.st_mode & S_IXOTH)
        {
            if(mystat.st_mode &S_ISVTX)
            {
                putchar('t');
            }else
                putchar('x');
        }else
            putchar('-');

        //硬链接
        printf(" %ld ",mystat.st_nlink);

        //文件拥有者名
        pwd = getpwuid(mystat.st_uid);
        printf("%s ", pwd->pw_name);

        //文件所属组
        grp = getgrgid(mystat.st_gid);
        printf("%s ",grp->gr_name);

        //总字节个数
        printf("%ld ", mystat.st_size);

        //获取文件时间
        tmp = localtime(&mystat.st_mtim.tv_sec);

        //错误
        if(tmp == NULL) return 1;
        strftime(buf, SIZE, "%m月  %d %H:%M",tmp);
        printf("%s ", buf);

        //文件名
        printf("%s ", name);
        putchar('\n');
    }
    return 0;
}

// ls -l
int ls_l(char *path)
{
    DIR *dp = NULL;
    struct dirent *entry = NULL;
    char buf[SIZE] = {};
    struct stat sstat;
    if(lstat(path,&sstat) == -1)//如果错误
    {
        perror("stat()");
        return 1;
    }
    if(S_ISREG(sstat.st_mode))
    {
        ls_l_1(".", path);
    }else
    {
        getcwd(path,sizeof(path));  //获取当前目录路径
        dp = opendir(path);
        if(dp == NULL)
        {
            perror("opendir()");
            return 1;
        }
        while(1)
        {
            entry = readdir(dp);
            if(NULL == entry)
            {
                perror("readdir()");
                closedir(dp);
                return 1;
                break;
            }
            ls_l_1(path, entry->d_name); //成功打印信息
        }
        printf("总用量：%d\n", num);
        closedir(dp);
    }   
    return 0;
}

//ls -i
int ls_i(const char *path)
{
    struct stat mystat;
    glob_t myglob;
    char buf[SIZE]={};
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

//ls -a
int ls_a(const char *path)
{
    struct stat mystat;
    struct dirent *entry = NULL;
    DIR *dp = NULL;

    if(lstat(path,&mystat)<0)
    {
        perror("lstat()");
        return -1;
    }

    dp=opendir(path);
    if(dp == NULL)
    {
        perror("opendir()");
        return -1;
    }
    while(1)
    {
        entry = readdir(dp);
        if(entry == NULL)
        {
            if(errno)
            {
                perror("readdir()");
                closedir(dp);
                return -1;
            }
            break;
        }
        printf("%s  ",entry->d_name);
        printf("\n");
    }
    closedir(dp);
    return 0;
}

//ls -R
int ls_R(char *basePath)
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

int main(int argc, char *argv[])
{
    int c;
    char s[1024];
    char *optstring = getcwd(s, sizeof(s));
    DIR *dir = opendir( optstring );

    if(argc < 2) return 1;
    c =getopt(argc, argv, optstring); //解析命令行选项参数
    printf("%d",c);
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
        default:
            printf("不认识此选项\n");
            break;
    }
    return 0;
}
