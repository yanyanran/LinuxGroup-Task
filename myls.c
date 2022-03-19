/*
***-a 列出目录下的所有文件，包括以.开头的隐含文件
***-l 列出文件的详细信息（包括文件属性和权限等）
***-R 使用递归连同目录中的子目录中的文件显示出来，如果要显示隐藏文件就要添加-a参数
   （列出所有子目录下的文件）
***-t 按修改时间进行排序，先显示最后编辑的文件
***-r 对目录反向排序（以目录的每个首字符所对应的ASCII值进行大到小排序）
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
#define PARAM_NONE 0
#define PARAM_A    1              //参数a
#define PARAM_L    2              //参数l
#define PARAM_I    4              //参数i
#define PARAM_R    8              //参数r
#define PARAM_T    16             //参数t
#define PARAM_RR   32             //参数R
#define PARAM_S    64             //参数s
#define MAXROWLEN  155            //每行所用最大格数
 
int g_maxlen;                     //最长文件名长度
int g_leave_len = MAXROWLEN;
int total = 0;                    //文件的大小总和
int h = 0;                        //每行已输出文件名的个数，用来判断是否换行
int han = 4;                      //一行输出文件名的个数

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
int ls_l(const char *path)
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
    }else{
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
                if(errno)
                {
                    perror("readdir()");
                    closedir(dp);
                    return 1;
                }
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

/*
//显示文件大小和最后修改时间（-s -t）
static int get_file_size_time(const char *filename)
{
    struct stat statbuf;
    if(stat(filename,&statbuf)==-1)
    {
        printf("Get stat on %s Error：%s\n", filename, strerror(errno));
        return -1;
    }
    if(S_ISDIR(statbuf.st_mode)) return 1;
    if(S_ISREG(statbuf.st_mode))
    {
        printf("%s size：%ld bytes\tmodified at %s",filename, statbuf.st_size, ctime(&statbuf.st_mtime));
        return 0;
    }
}

int main(int argc, char **argv)
{
    DIR *dirp;
    struct dirent *direntp;
    int stats;
    if(argc != 2)
    {
      printf ("Usage：%s filename\n\a", argv[0]);
      exit (1);
    }
    if(((stats = get_file_size_time(argv[1])) == 0) || (stats == -1))
    {
        exit(1);
    }
    if((dirp = opendir(argv[1])) == NULL)
    {
        printf ("Open Directory %s Error：%s\n", argv[1], strerror(errno));
        exit (1);
    }
    while((direntp = readdir (dirp)) != NULL)
    if(get_file_size_time(direntp->d_name)==-1)
    {
        break;
    }
    closedir(dirp);
    exit(1);
}*/

/*//ls -R
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
*/

/*-R -r -t*/
void display_dir(int flag_param,char *path)  /* flag_param:输入参数的变形  path:要打开的目录名 */
{
	DIR *dir;
	long t;
	int count = 0;
	int i, j, len;
	struct dirent *ptr;
	int flag_param_temp;
	struct stat  buf;
    struct stat  name;
    char temp[PATH_MAX+10];
	flag_param_temp = flag_param;
	dir = opendir(path);


	if(dir == NULL)
	{
		printf("%s:无法打开\n",path);
		return ;
	}

	//解析文件个数，及文件名的最长值
	while((ptr = readdir(dir)) != NULL)
	{
		int a = 0;               //用来统计汉字的个数，个数=a/3
		int b = 0;               //用来统计非汉字的个数b
		for(i = 0; i < strlen(ptr->d_name); i++)
		{
			if(ptr->d_name[i]< 0)
			{
				a++;
			}
			else
			{
				b++;
			}	
		}
		len = a/3*2 + b;
		if(g_maxlen<len)
		{
			g_maxlen = len;
		}
		count++;              //文件个数
	}
    
	han = g_leave_len/(g_maxlen+15);
	if(g_maxlen >40)
	{
		han=1;
	}

	closedir(dir);
	char **filename=(char **)malloc(sizeof(char*)*count);
	long **filetime=(long **)malloc(sizeof(long*)*count);
	len=strlen(path);
	dir=opendir(path);

	//得到该目录下的所有文件的路径
	for(i = 0;i < count ;i++)
	{
		filename[i] = (char *)malloc(sizeof(char)*1000);
		ptr = readdir(dir);
		if(ptr == NULL)
		{
			my_err("oppendir",__LINE__);
		}
		strncpy(filename[i],path,len);
		filename[i][len]='\0';
		strcat(filename[i],ptr->d_name);
		filename[i][len+strlen(ptr->d_name)]='\0';
	}
	closedir(dir);
	
	//插入排序
	if(flag_param & PARAM_T)    //根据时间排序
	{
        flag_param-=PARAM_T;
		for(i=0;i<count;i++)
		{
			filetime[i]=(long*)malloc(sizeof(long));
			stat(filename[i],&buf);       //用buf获取文件filename[i]中的数据
			filetime[i][0]=buf.st_mtime;
		}

		for(i=0;i<count;i++)
		{
			for(j=i;j<count;j++)
			{
				if(filetime[i][0]<filetime[j][0])
				{
					/*交换时间filetime还要叫唤文件名*/
					t=filetime[i][0];
					filetime[i][0]=filetime[j][0];
					filetime[j][0]=t;
					strcpy(temp,filename[i]);
					strcpy(filename[i],filename[j]);
					strcpy(filename[j],temp);
				}
			}
		}
	}
	else if(flag_param & PARAM_R)//根据名字排序
	{
		for(i=0;i<count;i++)
		{
			for(j=i;j<count;j++)
			{
				if(strcmp(filename[i],filename[j])>0)
				{
					strcpy(temp,filename[i]);
					strcpy(filename[i],filename[j]);
					strcpy(filename[j],temp);
				}
			}
		}
	}

	//计算总用量total
    if(flag_param & PARAM_A)
    {
        for(i=0;i<count;i++)
        {
            stat(filename[i],&name);
            total=total+name.st_blocks/2;
        }
    }
    else
    {
        for(i=0;i<count;i++)
        {
            stat(filename[i],&name);
            if(filename[i][2]!='.')
            {
                total=total+name.st_blocks/2;
            }
        }
    }
    

	if(flag_param & PARAM_L)
	{
		printf("总用量： %d\n",total);
	}

	//输出文件
	if(flag_param & PARAM_R)
	{
		flag_param-=PARAM_R;
        if(flag_param & PARAM_S)
        {
            if(flag_param & PARAM_A)
            {
                for(i=0;i<count;i++)
                {
                    stat(filename[i],&name);
                    total=total+name.st_blocks/2;
                }
            }
            else
            {
                for(i=0;i<count;i++)
                {
                    if(filename[i][2]!='.')
                    {
                        stat(filename[i],&name);
                        total=total+name.st_blocks/2;
                    }
                }
            }
            printf("总用量: %d\n", total);
        }
		if(flag_param & PARAM_RR)//递归输出
		{
			flag_param-=PARAM_RR;
			for(i=count-1;i>=0;i--)
			{
				lstat(filename[i],&buf);
				if(S_ISDIR(buf.st_mode))
				{
					len=strlen(filename[i]);
					if(filename[i][len-1]=='.'&&filename[i][len-2]=='/' || filename[i][len-1]=='.' && filename[i][len-2]=='.' && filename[i][len-3]=='/')
					{
						continue;
					}
					if(!(flag_param & PARAM_A))
					{
						if(filename[i][2]=='.')
                        {
                            continue;
                        }
					}
					printf("\n\n%s :\n",filename[i]);
					len=strlen(filename[i]);
					filename[i][len]='/';
					filename[i][len+1]='\0';
					display_dir(flag_param,path); //递归
				}
				else
				{
					display(flag_param,filename[i]);
				}
			}
		}
		else
		{
			for(i=count-1;i>=0;i--)
			{
				display(flag_param,filename[i]);
			}
		}
	}
	else
	{
        if(flag_param & PARAM_S)
        {
            if(flag_param & PARAM_A)
            {
                for(i = 0;i<count;i++)
                {
                    stat(filename[i],&name);
                    total=total+name.st_blocks/2;
                }
            }
            else
            {
                for(i=0;i<count;i++)
                {
                    stat(filename[i],&name);
                    if(filename[i][2]!='.')
                    {
                        total=total+name.st_blocks/2;
                    }
                }
            }
            printf("总用量: %d\n",total);
        }
		if(flag_param & PARAM_RR)
		{
			flag_param-=PARAM_RR;
    		for (i=0;i<count;i++)
			{
				lstat(filename[i],&buf);
				if(S_ISDIR(buf.st_mode))
				{
					len=strlen(filename[i]);
					if(filename[i][len-1]=='.' && filename[i][len-2]=='/' || filename[i][len-1]=='.' && filename[i][len-2]=='.' && filename[i][len-3]=='/') 
					{
                        continue;                    }
					if(!(flag_param & PARAM_A))
					{
						if(filename[i][2]=='.')
						{
                            continue;
                        }
					}
					printf("\n\n%s :\n",filename[i]);
					len=strlen(filename[i]);
					filename[i][len]='/';
					filename[i][len+1]='\0';		
					display_dir(flag_param_temp,filename[i]); //递归
				}
				else display(flag_param,filename[i]);
			}
		}
		else
		{
			for(i=0;i<count;i++)
			{
				display(flag_param,filename[i]);
			}
		}
		if((flag_param & PARAM_L)==0)
		{
			printf("\n");
		}	
	}
}

int main(int argc, char *argv[])
{
    DIR *dir;
    char basePath[1000];
    int c;
    char *optstring ="-l::a::i::R::";

    if(argc < 2) return 1;
    while(1)
    {
        c =getopt(argc, argv, optstring);
        if(c==-1) break;
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
                memset(argv[2],'\0',sizeof(argv[2]));
                getcwd(argv[2], 999);
                printf("现在的目录是: %s\n",argv[2]);
                ls_R(argv[2]);
                break;
            case '?':
                printf("不认识此选项%s\n", argv[optind -1]);
                break;
            default:
                break;
        }
    }

    return 0;
}
