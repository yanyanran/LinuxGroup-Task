typedef struct User_basicinfo
{
	char id[15];
	char paw[20];
	char name[15];
	char sex[5];
}Users;
void Login();
void Regist();

#include <stdio.h>
#include<string.h>
int main()
{
	int input=-1; 
    do
	{
		printf("\t\t\t----------------------------------\n");
		printf("\t\t\t|           1.登录               |\n");
		printf("\t\t\t|           2.注册               |\n");
		printf("\t\t\t|           0.退出               |\n");
		printf("\t\t\t----------------------------------\n");
		printf("请选择功能->");
		scanf("%d",&input);
		switch(input)
		{
			case 1:Login();break;
			case 2:Regist();break;
			case 0:puts("退出成功"); return 0;
		}
	}while(input);
	
	return 0;
}
void Regist()
{
	Users a={0},b={0};
	char tmp[20]={-1};
	FILE *pf=NULL;
	pf=fopen("add.dat","r");//用pf去指向文件 
	if(pf==NULL)
	{
		printf("注册时打开文件失败\n");
		return ;
	}
	printf("\t\t\t欢迎来到注册界面\n\n");
	printf("\t\t\t输入账号->");
	scanf("%s",a.id);
	printf("输入成功!\n"); 

	fread(&b, sizeof(Users), 1, pf);
//【判断】有没有注册过-比较字符串是否相等 
//不相等->是否到文件尾 
while(1)
{
	if(strcmp(a.id, b.id)!=0 )
	{
		 if(feof(pf)==0 )//未到文件尾 
		 fread(&b, sizeof(Users), 1, pf);
	 	 else//到了文件尾仍然没有相同的字符串-说明输入的账号使新的 可以去注册界面 
		 {
		 printf("账号未注册过，将跳转到注册界面\n");
		 system("pause");
		 break;//利用break来跳出无限循环 
		 }
	}
	else
	{
		 printf("该账号已注册过\n");
		 fclose(pf);
         pf=NULL;
		 return;
    }
}
//【注册界面】 
	printf("\t\t\t请输入姓名->"); 
	scanf("%s",a.name);
	printf("\t\t\t请输入性别:男/女->"); 
	do{
		getchar();
		scanf("%s",a.sex);
		if(strcmp(a.sex, "男")!=0&&strcmp(a.sex, "女")!=0)
		printf("\t\t\t输入错误，请重新输入->");
		else break;
	     }while(1);
			
		printf("\t\t\t请输入密码->"); 
		scanf("%s",a.paw);
		printf("\t\t\t请再输入一次密码->"); 
		do{
		scanf("%s",tmp);
		if(strcmp(tmp,a.paw)!=0)
		printf("\t\t\t两次输入密码不一致，请再输入一次密码->");
		else break;
		}while(1);
		//两次密码一致
		fclose(pf);	pf=NULL;
		pf=fopen("add.dat","a");
		//fwrite会在当前文件指针的位置写入数据
		//"w" 打开，文件指针指到头，只写；"a" 打开，指向文件尾
		fwrite(&a, sizeof(Users) , 1, pf );
		printf("\t\t\t注册成功!\n"); 
		fclose(pf);	pf=NULL;
		return;	
}


void Login()
{
	Users a={0},b={0};
	FILE *pf=fopen("add.dat","r");
	if(pf==NULL)
	{
		printf("文件打开失败\n");
		return ;
	}
	printf("欢迎来到登录界面!\n");
	printf("请输入账号->");
	scanf("%s",a.id);
	fread(&b, sizeof(Users), 1, pf);
	while(1)
	{
		if(strcmp(a.id, b.id)!=0 )
		{
			if(feof(pf)==0)//未到文件尾 
			{
				fread(&b, sizeof(Users), 1, pf);
			}
			else
			{
				printf("该账号不存在,请先注册\n");
				fclose(pf); pf=NULL;
				return ;
			}
		}
		else//账号注册过->跳到输入密码 
		{
			break; //退出无限循环，跳到输入密码 
		}
		
	}
//【输入密码】 
	printf("请输入密码->"); 
	do{
	scanf("%s",a.paw);
	if(strcmp(a.paw, b.paw)!=0 )
	printf("密码错误，请重新输入->");
	else break;
	}while(1);
	printf("登录成功!\n");
	fclose(pf); pf=NULL;
}