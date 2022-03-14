/*
-a 列出目录下的所有文件，包括以.开头的隐含文件
-l 列出文件的详细信息（包括文件属性和权限等）
-R 使用递归连同目录中的子目录中的文件显示出来，如果要显示隐藏文件就要添加-a参数
   （列出所有子目录下的文件）
-t 以时间排序
-r 对目录反向排序
-i 输出文件的i节点的索引信息
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

int ls_l(const char *path);
int ls_i(const char *path);
int ls_a(const char *path);
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
    if(*path == '.')
        return 1;
    else
        return 0;
}

int ls_l_1(const char *path,const char *name){
    struct stat mystat;
    struct passwd *pwd = NULL;
    struct tm *tmp = NULL;
    struct group *grp = NULL;
    char *buf = NULL;

    buf = buf_cat(path,name);

    if(lstat(buf, &mystat) == -1){
        perror("stat()");
        return 1;
    }

    if(hide(name) == 0){
        num +=mystat.st_blocks/2;
        switch(mystat.st_mode & S_IFMT){
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
        if(mystat.st_mode & S_IXUSR){
            if(mystat.st_mode &S_ISUID){
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
        if(mystat.st_mode & S_IXGRP){
            if(mystat.st_mode &S_ISGID){
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
        if(mystat.st_mode & S_IXOTH){
            if(mystat.st_mode &S_ISVTX){
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
        //if error
        if(tmp == NULL)
            return 1;
        strftime(buf, SIZE, "%m月  %d %H:%M",tmp);
        printf("%s ", buf);

        //文件名
        printf("%s ", name);

        putchar('\n');
    }
    return 0;
}

// ls -l
int ls_l(const char *path)
{
    DIR *dp = NULL;
    struct dirent *entry = NULL;
    char buf[SIZE] = {};
    struct stat sstat;
    if(lstat(path,&sstat) == -1){
        perror("stat()");
        return 1;
    }
    if(S_ISREG(sstat.st_mode)){
        ls_l_1(".", path);
    }else{
        dp = opendir(path);
        if(dp == NULL){
            perror("opendir()");
            return 1;
        }

        while(1){
            entry = readdir(dp);
            if(NULL == entry){
                if(errno){
                    perror("readdir()");
                    closedir(dp);
                    return 1;
                }
                break;
            }
            ls_l_1(path, entry->d_name);
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
    //  memset(buf,'\0',BUFFSIZE);
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

int main(int argc, char *argv[])
{
    int c;
    char *optstring ="-l::a::i::h::";

    if(argc < 2)
        return 1;

    while(1){
        c =getopt(argc, argv, optstring);
        if(c == -1)
            break;
        switch (c) {
            case 'l':
                ls_l(argv[2]);
                break;
            case 'a':
                ls_a(argv[2]);
                break;
            case 'i':
                ls_i(argv[2]);
                break;
            case '?':
                printf("不认识此选项%s\n", argv[optind -1]);
                break;
        //    case 1:printf("%s\n", argv[optind -1]);break;
            default:
                break;
        }
    }

    return 0;
}