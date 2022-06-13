#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

#define N 10
#define HOURROOM 10
#define NORMALROOM 288
#define BIGROOM 388
#define WORTHROOM 588
#define GREATROOM 998

struct room_message
{
	int number;	 //房间号
	int people;	 //有无入住
	int floor;	 //楼层数（不同楼层房间类型不同）
	time_t time; //入住时间，无人住则置空
} ROOM[100];

struct administrator
{
	char name[16];	   //用户名
	char password[16]; //密码
	int number;		   //人数
} group[25];

int TheUsingAdministrator;

void BuildGroup()
{ //建立密码文件
	FILE *fp = NULL;
	int a;
	char b[16] = {"0"};
	if ((fp = fopen("administrator.dat", "r")) == NULL) //创建文件 防止出错
	{
		fp = fopen("administrator.dat", "w");
		for (a = 0; a < 25; a++)
		{
			strcpy(group[a].name, b);
			strcpy(group[a].password, b);
			group[a].number = a + 1;
			fwrite(&group[a], sizeof(struct administrator), 1, fp);
		}
	}
	else
	{
		for (a = 0; a < 25; a++)
		{
			fread(&group[a], sizeof(struct administrator), 1, fp);
		}
	}
	fclose(fp);
}

void BuildRoom()
{ //创建（引用）房间的函数
	FILE *fp = NULL;
	int i, j; //控制循环

	if ((fp = fopen("roommessage.dat", "r")) == NULL)
	{
		fp = fopen("roommessage.dat", "w");
		for (i = 0; i < N; i++)
		{
			for (j = 0; j < N; j++)
			{
				ROOM[i * 10 + j].number = j;
				ROOM[i * 10 + j].floor = i;
				ROOM[i * 10 + j].people = 0;
				ROOM[i * 10 + j].time = 0;
				fwrite(&ROOM[i * 10 + j], sizeof(struct room_message), 1, fp);
			}
		}
	}
	else
	{
		for (i = 0; i < N; i++)
		{
			for (j = 0; j < N; j++)
			{
				fread(&ROOM[i * 10 + j], sizeof(struct room_message), 1, fp);
			}
		}
	}
	fclose(fp);
}

void PrintFee(int floor, int room, int fee, int people)
{ //打印所需费用
	time_t time1, time2;
	int a;
	time1 = ROOM[floor * 10 + room].time;
	time2 = time(NULL);
	if (people == 0)
	{
		printf("该房间没有人\n");
	}
	else
	{
		if (floor < 2)
		{
			a = ((time2 - time1) / 3600) + 1;
			printf("该房间的费用为%d元\n", a * fee);
		}
		else
		{
			a = (((time2 - time1) / 3600) / 24) + 1;
			printf("该房间的费用为%d元\n", a * fee);
		}
	}
}

void CheckOut()
{ //退房函数，引用计算费用函数，并更改入住情况
	int roomnuber, choose, a, b, floor, room, fee;
	FILE *fp = NULL;
	do
	{
		printf("\n请输入您想退房的房间的房间号:");
		scanf("%d", &roomnuber);
		floor = (roomnuber / 100) - 1;
		room = roomnuber % 100;
		floor = (roomnuber / 100) - 1;
		room = (roomnuber % 100) - 1;
		if (floor > 9 || floor < 0 || room < 0 || room > 9)
		{
			printf("您的输入有误，请重新输入\n");
		}
	} while (floor > 9 || floor < 0 || room < 0 || room > 9);
	printf("您要退的房为%d,它是", roomnuber);
	if (floor == 0 || floor == 1)
	{
		printf("钟点房\n");
		fee = HOURROOM;
	}
	if (floor == 2 || floor == 3)
	{
		printf("标准间\n");
		fee = NORMALROOM;
	}
	if (floor == 4 || floor == 5)
	{
		printf("大床房\n");
		fee = BIGROOM;
	}
	if (floor == 6 || floor == 7)
	{
		printf("行政套房\n");
		fee = WORTHROOM;
	}
	if (floor == 8 || floor == 9)
	{
		printf("豪华套房\n");
		fee = GREATROOM;
	}
	do
	{
		printf("\n您是否确定要退房。\n");
		printf("1.是\n");
		printf("2.不是\n");
		scanf("%d", &choose);
		if (choose != 1 && choose != 2)
		{
			printf("\n您的输入有误，请重新输入\n");
		}
	} while (choose != 1 && choose != 2);
	if (choose == 1)
	{
		PrintFee(floor, room, fee, ROOM[floor * 10 + room].people);
		ROOM[floor * 10 + room].people = 0;
		ROOM[floor * 10 + room].time = 0;
		fp = fopen("roommessage.dat", "w");
		for (a = 0; a < N; a++)
		{
			for (b = 0; b < N; b++)
			{
				fwrite(&ROOM[a * 10 + b], sizeof(struct room_message), 1, fp);
			}
		}
		printf("\n退房成功\n");
	}
	fclose(fp);
	system("pause");
}

void CalFee()
{ //计费函数，通过时间相减，计算费用
	int roomnuber, floor, room, fee;
	do
	{
		printf("\n请输入您想查询费用的房间的房间号(例如1001):");
		scanf("%d", &roomnuber);
		floor = (roomnuber / 100) - 1;
		room = (roomnuber % 100) - 1;
		if (floor > 9 || floor < 0 || room < 0 || room > 9)
		{
			printf("您的输入有误，请重新输入\n");
		}
	} while (floor < 0 || floor > 9 || room < 0 || room > 9);
	printf("您要找的%d号房为", roomnuber);
	if (floor == 0 || floor == 1)
	{
		printf("钟点房\n");
		fee = HOURROOM;
	}
	if (floor == 2 || floor == 3)
	{
		printf("标准间\n");
		fee = NORMALROOM;
	}
	if (floor == 4 || floor == 5)
	{
		printf("大床房\n");
		fee = BIGROOM;
	}
	if (floor == 6 || floor == 7)
	{
		printf("行政套房\n");
		fee = WORTHROOM;
	}
	if (floor == 8 || floor == 9)
	{
		printf("豪华套房\n");
		fee = GREATROOM;
	}
	PrintFee(floor, room, fee, ROOM[floor * 10 + room].people);
}

void ScanRoom()
{ //查询函数，读取入住情况
	int a, i, j;
	FILE *fp = NULL;
	void PrintRoomFee(int a);
	printf("\n请选择您要查询的房间种类\n");
	printf("1.钟点房\n");
	printf("2.标准间\n");
	printf("3.为大床房\n");
	printf("4.为行政套房\n");
	printf("5.为豪华套房\n");
	scanf("%d", &a);
	if ((fp = fopen("roommessage.dat", "r")) != NULL)
	{
		for (i = 0; i < N; i++)
		{
			for (j = 0; j < N; j++)
			{
				fread(&ROOM[i * 10 + j], sizeof(struct room_message), 1, fp);
			}
		}
		if (a == 1)
		{
			PrintRoomFee(a);
			for (i = 0; i <= 1; i++)
			{
				for (j = 0; j < 10; j++)
				{
					printf("\n第%d层", ROOM[i * 10 + j].floor + 1);
					printf("第%d号房", ROOM[i * 10 + j].number + 1);
					if (ROOM[i * 10 + j].people)
					{
						printf("有人，不能入住");
					}
					else
					{
						printf("没有人，可以入住");
					}
				}
			}
		}
		if (a == 2)
		{
			PrintRoomFee(a);
			for (i = 2; i <= 3; i++)
			{
				for (j = 0; j < 10; j++)
				{
					printf("\n第%d层", ROOM[i * 10 + j].floor + 1);
					printf("第%d号房", ROOM[i * 10 + j].number + 1);
					if (ROOM[i * 10 + j].people)
					{
						printf("有人，不能入住");
					}
					else
					{
						printf("没有人，可以入住");
					}
				}
			}
		}
		if (a == 3)
		{
			PrintRoomFee(a);
			for (i = 4; i <= 5; i++)
			{
				for (j = 0; j < 10; j++)
				{
					printf("\n第%d层", ROOM[i * 10 + j].floor + 1);
					printf("第%d号房", ROOM[i * 10 + j].number + 1);
					if (ROOM[i * 10 + j].people)
					{
						printf("有人，不能入住");
					}
					else
					{
						printf("没有人，可以入住");
					}
				}
			}
		}
		if (a == 4)
		{
			PrintRoomFee(a);
			for (i = 6; i <= 7; i++)
			{
				for (j = 0; j < 10; j++)
				{
					printf("\n第%d层", ROOM[i * 10 + j].floor + 1);
					printf("第%d号房", ROOM[i * 10 + j].number + 1);
					if (ROOM[i * 10 + j].people)
					{
						printf("有人，不能入住");
					}
					else
					{
						printf("没有人，可以入住");
					}
				}
			}
		}
		if (a == 5)
		{
			PrintRoomFee(a);
			for (i = 8; i <= 9; i++)
			{
				for (j = 0; j < 10; j++)
				{
					printf("\n第%d层", ROOM[i * 10 + j].floor + 1);
					printf("第%d号房", ROOM[i * 10 + j].number + 1);
					if (ROOM[i * 10 + j].people)
					{
						printf("有人，不能入住");
					}
					else
					{
						printf("没有人，可以入住");
					}
				}
			}
		}
	}
}

void PrintRoomFee(int a)
{ //打印房间价格
	if (a == 1 || a == 2)
	{
		printf("\n钟点房的费用为每小时%d元\n", HOURROOM);
	}
	if (a == 3 || a == 4)
	{
		printf("\n标准间的费用为每天%d元\n", NORMALROOM);
	}
	if (a == 5 || a == 6)
	{
		printf("\n大床房的费用为每天%d元\n", BIGROOM);
	}
	if (a == 7 || a == 8)
	{
		printf("\n行政套房的费用为每天%d元\n", WORTHROOM);
	}
	if (a == 9 || a == 10)
	{
		printf("\n豪华套房的费用为每天%d元\n", GREATROOM);
	}
}

void CheckIn()
{ //入住函数，记录时间，更改入住情况
	int a, b, c, i, j;
	FILE *fp = NULL;
	do
	{
		printf("\n请输入您想入住的房间层数(1~10)");
		printf("\n1~2为钟点房");
		printf("\n3~4为标准间");
		printf("\n5~6为大床房");
		printf("\n7~8为行政套房");
		printf("\n9~10为豪华套房\n");
		scanf("%d", &a);
		if (a < 1 || a > 10)
		{
			printf("您的输入有误，请重新输入\n");
		}
	} while (a < 1 || a > 10);
	PrintRoomFee(a);
	do
	{
		printf("\n请输入您想入住的房间号(1~10)：");
		scanf("%d", &b);
		if (b < 1 || b > 10)
		{
			printf("您的输入有误，请重新输入\n");
		}
	} while (b < 1 || b > 10);
	if ((fp = fopen("roommessage.dat", "rb")) != NULL)
	{ //打开文件
		if (ROOM[(a - 1) * 10 + (b - 1)].people == 0)
		{ //判断房间是否可住
			for (i = 0; i < N; i++)
			{
				for (j = 0; j < N; j++)
				{
					fread(&ROOM[i * 10 + j], sizeof(struct room_message), 1, fp);
				}
			}
			fclose(fp);
			printf("\n请输入入住的人数:");
			scanf("%d", &c);
			ROOM[(a - 1) * 10 + b - 1].people = c;
			ROOM[(a - 1) * 10 + b - 1].time = time(NULL);
			fp = fopen("roommessage.dat", "w");
			for (a = 0; a < N; a++)
			{
				for (b = 0; b < N; b++)
				{
					fwrite(&ROOM[a * 10 + b], sizeof(struct room_message), 1, fp);
				}
			}
		}
		else
		{
			printf("\n该房间已经有人了\n");
			printf("请选择其他的房间入住\n");
		}
	}
	fclose(fp);
}

void ChangePassword()
{ //修改密码
	FILE *fp = NULL;
	char password1[16] = {'0'}, password2[16] = {'0'}, password[16] = {'0'}, c;
	int a = 0, b = 0; //a为计数器
	printf("\n请输入新密码:");
	while ((c = getchar()) != '\r')
	{
		if (c != '\b')
		{
			password1[a] = c;
			password[a] = c;
			putchar('*');
			a++;
		}
		else
		{
			a--;
			if (a < 0)
			{
				a++;
				putchar(' ');
			}
			else
			{
				password1[a + 1] = '\0';
				password[a + 1] = '\0';
			}
			putchar('\b');
			putchar(' ');
			putchar('\b');
		}
		if (a > 15)
		{
			break;
		}
	}
	printf("\n请再次输入新密码:");
	a = 0;
	while ((c = getchar()) != '\r')
	{
		if (c != '\b')
		{
			password2[a] = c;
			putchar('*');
			a++;
		}
		else
		{
			a--;
			if (a < 0)
			{
				a++;
				putchar(' ');
			}
			else
			{
				password2[a + 1] = '\0';
			}
			putchar('\b');
			putchar(' ');
			putchar('\b');
		}
		if (a > 15)
		{
			break;
		}
	}
	if (strcmp(password1, password2))
	{ //比对密码是否一样
		printf("\n两次输入不一样，修改密码失败\n");
	}
	else
	{
		strcpy(group[TheUsingAdministrator - 1].password, password);
		printf("\n修改密码成功!\n");
		fp = fopen("administrator.dat", "wb");
		for (b = 0; b < 25; b++)
		{
			fwrite(&group[b], sizeof(struct administrator), 1, fp);
		}
		fclose(fp);
	}
	system("pause");
}

void PrintAdministrator()
{ //打印所有的帐号信息
	char b[16] = {'0'};
	int a;
	printf("序号\t用户名\t\t密码");
	for (a = 0; a < 25; a++)
	{
		if (strcmp(b, group[a].name))
		{
			printf("%d\t", a + 1);
			printf("%s\t", group[a].name);
			if (strlen(group[a].name) < 8)
			{
				printf("\t");
			}
			printf("%s\t", group[a].password);
			if (strlen(group[a].password) < 8)
			{
				printf("\t");
			}
		}
	}
	system("pause");
}

//管理酒店信息
int ChangeRoom()
{ 
	int step;
	BuildRoom();
	do
	{ //主程序循环
		do
		{ //选择界面循环
			printf("\n请输入您想执行的步骤序号\n");
			printf("1.登记入住\n");
			printf("2.查询入住情况\n");
			printf("3.查询当前费用\n");
			printf("4.退房\n");
			printf("5.回到主界面\n");
			scanf("%d", &step);
			if (step < 1 || step > 5)
			{
				printf("\n您的输入有误，请重新输入\n");
			}
		} while (step < 1 || step > 5);
		switch (step)
		{
		case 1:
			CheckIn();
			break;
		case 2:
			ScanRoom();
			break;
		case 3:
			CalFee();
			break;
		case 4:
			CheckOut();
			break;
		case 5:
			return 0;
		}
		printf("\n请问你还需执行其他管理操作吗？\n");
		printf("1.是\n");
		printf("2.否\n");
		do
		{
			scanf("%d", &step);
			if (step < 1 || step > 2)
			{
				printf("\n您的输入有误，请重新输入\n");
			}
		} while (step < 1 || step > 2);
	} while (step == 1);
	return 0;
}

void ChangeGroup()
{ //写入帐号信息
	FILE *fp = NULL;
	int a;
	fp = fopen("administrator.dat", "w");
	for (a = 0; a < 25; a++)
	{
		fwrite(&group[a], sizeof(struct administrator), 1, fp);
	}
	fclose(fp);
}

void DeleteAdministrator()
{ //删除帐号
	int a;
	char b[16] = {'0'};
	PrintAdministrator();
	do
	{
		printf("\n请输入您要删除的账户的序号:");
		scanf("%d", &a);
		if (a == TheUsingAdministrator)
		{
			printf("不能删除您正在使用的帐号\n");
		}
	} while (a == TheUsingAdministrator);
	strcpy(group[a - 1].name, b);
	strcpy(group[a - 1].password, b);
	ChangeGroup(); //写入信息
	printf("删除成功\n");
	system("pause");
}

void ControlAdministrator()
{ //帐号管理菜单
	int step;

	printf("\n请选择您想执行的操作\n");
	printf("1.查看所有账户信息\n");
	printf("2.删除一个账户\n");
	printf("3.修改当前账户密码\n");
	printf("4.修改当前账户年龄\n");
	printf("5.修改当前账户性别\n");
	scanf("%d", &step);
	switch (step)
	{
	case 1:
		PrintAdministrator();
		break;
	case 2:
		DeleteAdministrator();
		break;
	case 3:
		ChangePassword();
		break;
	}
}

//登陆帐号
int Logadministrator()
{
	char name[16] = {'0'}, password[16] = {'0'}, wrong[16] = {'\r'};
	char c;
	int a, d, right, empty; //a为计数器,right为判断是否正确,empty判断用户名是否存在,b用于跳出登录
	printf("\n请输入您的用户名:");
	d = 0;
	while ((c = getchar()) != '\r')
	{
		if (c != '\b')
		{
			name[d] = c;
			putchar(c);
			d++;
		}
		else
		{
			d--;
			if (d < 0)
			{
				d++;
				putchar(' ');
			}
			else
			{
				name[d + 1] = '\0';
			}
			putchar('\b');
			putchar(' ');
			putchar('\b');
		}
		if (d > 15)
		{
			break;
		}
	}
	if (strcmp(name, wrong) == 0)
	{
		printf("用户名不能为空，请重新输入\n");
		return 0;
	}
	printf("\n请输入您的密码:");
	d = 0;
	while ((c = getchar()) != '\r')
	{
		if (c != '\b')
		{
			password[d] = c;
			d++;
		}
		else
		{
			d--;
			if (d < 0)
			{
				d++;
				putchar(' ');
			}
			else
			{
				password[d + 1] = '\0';
			}
			putchar('\b');
			putchar(' ');
			putchar('\b');
		}
		if (d > 15 || d < 0)
		{
			break;
		}
	}
	for (a = 0, right = 0; a < 25; a++)
	{
		if (strcmp(name, group[a].name) == 0)
		{
			if (strcmp(password, group[a].password) == 0)
			{
				right = 1;
			}
			break;
		}
		if (a == 24)
		{
			empty = 1;
		}
	}
	if (empty != 1)
	{ //输入错误，防止想不起密码而设置
		if (right == 0)
		{
			printf("\n您的密码输入有误，请重新输入\n");
			return 0;
		}
	}
	else
	{
		printf("\n您输入的用户名不存在，请重新输入\n");
		return 0;
	}
	printf("\n欢迎您，%s!\n", group[a].name);
	TheUsingAdministrator = group[a].number;
	return 1;
}

//注册帐号
int Newadministrator()
{
	char d[16] = {'0'}, name[16] = {'0'};
	FILE *fp = NULL;
	char c;
	int a = 0, b;

	do
	{
		printf("\n请输入您想使用的用户名（10个字母以内）:");
		b = 0;
		strcpy(name, d);
		while ((c = getchar()) != '\r')
		{
			if (c != '\b')
			{
				name[b] = c;
				b++;
			}else{
				b--;
				if (b < 0)
				{
					b++;
					putchar(' ');
				}
				else
				{
					name[b + 1] = '\0';
				}

				//退格键的实现三步走
				putchar('\b');		//将光标左移
				putchar(' ');
				putchar('\b');
			}
			if(b > 10)
				break;
		}
		printf("\n");
		for (a = 0; a < 11; a++)
		{
			if (strcmp(name, group[a].name) == 0)
			{
				printf("\n这个用户名已经被使用了，请换一个用户名\n");
				break;
			}
			if (strcmp(d, group[a].name) == 0)
				break;
		}
	} while (strcmp(name, group[a].name) == 0);

	if (a < 12)
	{
		strcpy(group[a].name, name);
		printf("请输入六位密码:");
		b = 0;
		while ((c = getchar()) != '\r')
		{
			if (c != '\b')
			{
				group[a].password[b] = c;
				b++;
			}else{
				b--;
				if (b < 0)
				{
					b++;
					putchar(' ');
				}
				else
				{
					group[a].password[b + 1] = '\0';
				}
				putchar('\b'); //用于删除打印出的字符
				putchar(' ');
				putchar('\b');
			}
			if (b > 6)
				break;
		}
		fp = fopen("administrator.dat", "wb");
		for (b = 0; b < 7; b++)
		{
			fwrite(&group[b], sizeof(struct administrator), 1, fp);
		}
		fclose(fp);
		printf("\n欢迎您，%s!\n", group[a].name);
		TheUsingAdministrator = group[a].number;
		return 1;
	}
	else
	{
		printf("已到达注册上限，不能注册\n");
		return 0;
	}
}

//菜单
int Admin()
{
	int a = 0, b, choose; //a为用户载体
	do
	{
		do
		{
			b = 0;
			printf("\t\t\t|--------------------------------------\n");
			printf("\t\t\t|         请问您需要做什么?              \n");
			printf("\t\t\t|--------------------------------------\n");
			printf("\t\t\t|         1.登录已有帐号                \n");
			printf("\t\t\t|--------------------------------------\n");
			printf("\t\t\t|         2.注册新的帐号                \n");
			printf("\t\t\t|--------------------------------------\n");
			scanf("%d", &choose);
			if (choose != 1 && choose != 2)
			{
				printf("您的输入有误，请重新输入\n");
			}
		} while (choose != 1 && choose != 2);
		switch (choose)
		{
		case 1:
			a = Logadministrator();
			break;
		case 2:
			a = Newadministrator();
			break;
		}
		if (a == 0)
		{
			printf("\n是否重新输入:\n");
			printf("1.是(重新选择登录或者注册)\n");
			printf("2.否(将退出程序)\n");
			scanf("%d", &b);
		}
	} while (b == 1);
	return a;
}

//欢迎页面
int LogIn()
{
	printf("\n\n");
	printf("\t\t\t\t**********************\n");
	printf("\t\t\t\t|欢迎使用酒店管理系统|\n");
	printf("\t\t\t\t**********************\n\n");
	BuildGroup();
	return Admin();
}

//main
void main()
{
	int step;
	if (LogIn())
	{
		do
		{
			printf("\n请选择您想做的事情:\n");
			printf("\t\t\t----------------------------------------\n");
			printf("\t\t\t|           1.管理酒店信息               |\n");
			printf("\t\t\t|           2.管理账户信息               |\n");
			printf("\t\t\t|           3.注销账户                  |\n");
			printf("\t\t\t|           4.退出系统                  |\n");
			printf("\t\t\t|           5.开发者信息                |\n");
			printf("\t\t\t---------------------------------------\n");
			scanf("%d", &step);
			switch (step)
			{
			case 1:
				ChangeRoom();
				break;
			case 2:
				ControlAdministrator();
				break;
			case 3:
				if (LogIn() == 0)
				{
					step = 4;
				};
				break;
			case 5:
				printf("\t\t\t-------------------------------------\n");
				printf("\t\t\t|           西安邮电大学              |\n");
				printf("\t\t\t|           计科2106班               |\n");
				printf("\t\t\t|              颜然                  |\n");
				printf("\t\t\t|  ***********感谢使用************   |\n");
				printf("\t\t\t------------------------------------\n");
			}
		} while (step != 4); //输入4时退出
		printf("\n感谢您的使用，正在退出...\n");
		printf("退出成功\n");
	}
	else
	{
		printf("\n登录失败，程序即将退出\n");
		printf("程序已退出\n");
	}
}
