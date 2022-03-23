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
      
#define LS_NONE 0  
#define LS_L 101  
#define LS_R 102  
#define LS_I 103 
#define LS_A 200  
#define LS_AL (LS_A+LS_L)

#define WHITE 37
#define BLUE 34
#define GREEN 32
      
// 展示单个文件的详细信息  
void show_file(char* filename, struct stat* info_p)  
{  
    char* uid_to_name(), *ctime(), *gid_to_name(), *filemode();  
    void mode_to_letters();  
    char modestr[11];  
    mode_to_letters(info_p->st_mode, modestr);  
      
    printf("%s", modestr);  
    printf(" %d", (int) info_p->st_nlink);  
    printf(" %s", uid_to_name(info_p->st_uid));  
    printf(" %s", gid_to_name(info_p->st_gid));  
    printf(" %ld", (long) info_p->st_size);  //大小
    printf(" %s", 4 + ctime(&info_p->st_mtime));  //时间
    printf(" %s\n", filename);  //文件名字
}  

//文件权限
void mode_to_letters(int mode, char str[])  
{  
    strcpy(str, "----------");  //初始化全为---------- 
      
    //开改
    if(S_ISDIR(mode))  
    {  
        str[0] = 'd';  
    }  
      
    if(S_ISCHR(mode))  
    {  
        str[0] = 'c';  
    }  
      
    if(S_ISBLK(mode))  
    {  
        str[0] = 'b';  
    }  
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
      
char* uid_to_name(uid_t uid)  
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
      
char* gid_to_name(gid_t gid)  
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
      
void ls(char dirname[],int mode)  
{  
    DIR* dir_ptr;  
    struct dirent* direntp;  
      
    if((dir_ptr = opendir(dirname)) == NULL)  
    {  
        fprintf(stderr, "ls2: cannot open %s \n", dirname);  
    }  
    else  
    {  
        char dirs[20][100];  
        int dir_count = 0;  
                  
        while ((direntp = readdir(dir_ptr)) != NULL)  
        {  
      
            if(mode < 200 && direntp->d_name[0]=='.')  
            {  
                continue;  
            }  
            char complete_d_name[200];  // 存文件的完整路径  
            strcpy(complete_d_name,dirname);  
            strcat(complete_d_name,"/");  
            strcat(complete_d_name,direntp->d_name);  
                      
            struct stat info;  
            if(stat(complete_d_name, &info) == -1)  
            {  
                perror(complete_d_name);  
            }  
            else  
            {  
                if(mode == LS_L||mode == LS_AL)  
                {  
                    show_file(direntp->d_name, &info);  
                }  
                else if(mode == LS_A||mode == LS_NONE||mode == LS_I)  
                {  
                    if(mode == LS_I)  
                    {  
                        printf("%lu ", direntp->d_ino);  //索引信息
                    }  
      
                    printf("%s\n", direntp->d_name);  //加颜色（？）
                }  
                else if(mode == LS_R)  
                {  
                    if(S_ISDIR(info.st_mode))  
                    {  
                        printf("%s\n", direntp->d_name);  
                        strcpy(dirs[dir_count],complete_d_name);  
                        dir_count++;  
                    }  
                    else  
                    {  
                        printf("%s\n", direntp->d_name);  
                    }  
                }  
      
            }  
        }  
        if(mode == LS_R)  
        {  
            int i=0;  
            printf("\n");  
            for(;i<dir_count;i++)
            {  
                printf("%s:\n", dirs[i]);  
                ls(dirs[i],LS_R);  
                printf("\n");  
            }
        }
        closedir(dir_ptr);  
    }  
}  
      
// 解析命令行参数进行匹配 (使用长度来区分l和la al
int A(char* input)
{  
    if(strlen(input)==2)  
    {  
        if(input[1]=='l') return LS_L;  
        if(input[1]=='a') return LS_A;  
        if(input[1]=='R') return LS_R;  
        if(input[1]=='i') return LS_I;  
    }  
    else if(strlen(input)==3)  
    {  
        if(input[1]=='a'&& input[2]=='l'||input[1]=='l'&&input[2]=='a') // -al或-la
        {
            return LS_AL;
        }   
    }  
    return -1;  //什么都不是返-1
}  

//main
int main(int argc,char* argv[])  
{
    if(argc == 1)  
    {  
        ls(".",LS_NONE);  
    }  
    else  
    {  
        int mode = LS_NONE;      // 默认为无参数ls  
        int have = 0;            // 判断是否有输入文件参数  
      
        while(argc>1)  
        {  
            argc--;  
            argv++;  
      
            int MMode = A(*argv);  //解析参数
            if(MMode!=-1)  
            {  
                mode+=MMode;  
            }  
            else  
            {  
                have = 1;  
                do  
                {  
                    printf("%s:\n", *argv);  
                    ls(*argv,mode);  
                    printf("\n");  
      
                    argc--;  
                    argv++;  
                }while(argc>=1);  
            }  
        }  
        if(!have)
        {  
            ls(".",mode);  
        }     
    }       
}  
