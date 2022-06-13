#include<windows.h>
#include<stdio.h>
#include<string.h>
#include<conio.h>
#include<stdlib.h>
#include<malloc.h>
#define ENTER 13
#define BACKSPACE 8
#define OK 1
#define ERROR 0

char filename[20];
void Register();
void Login();
void User_choice(int l,char *user,char *pwd);
struct activity_list * Read();

struct activity_list
{
    char number[100];
    char content[100];                  //活动内容
    int count;                          //活动人数
    int join;                           //参加活动
    char demand[100];                   //活动要求
    char time1[100];                    //活动时间
    char Cname[100];                    //活动创建者
    char pwd3[15];                      //活动创建密码
    struct activity_list * next;        /*链表的指针域*/
};

//保存活动的相关信息
void save(struct activity_list * head)
{
    struct activity_list *p;
    FILE *fp;

    p=head;
    fp=fopen("全部活动.txt","w+");
    fprintf(fp,"%30s%30s%30d%30d%30s%30s%30s",p->number,p->content,p->count,p->join,p->demand,p->time1,p->Cname);
    while(p->next!= NULL)
    {
        p=p->next;
        fprintf(fp,"%20s%20s%20d%20d%20s%20s%20s",p->number,p->content,p->count,p->join,p->demand,p->time1,p->Cname);
    }
    fclose(fp);
}
//插入活动
struct activity_list * InsertDoc(struct activity_list * head,struct activity_list * book)
{
    struct activity_list * ptr,* p;

    p=head;
    ptr=book;
    if(head==NULL)
    {
        head=ptr;
        head->next=NULL;
    }
    else
    {
        while(p->next!=NULL)
            p=p->next;
        p->next=ptr;
        ptr->next=NULL;
    }
    save(head);
    return head;
}
//把密码字符转为"*"
char *Get_password(const char *prompt)
{
    char ch;
    static char p[15];
    int i=0;
    puts(prompt);
    while((ch=getch())!=ENTER && ch != -1)
    {
        if(i == 14 && ch != BACKSPACE)
        {
            continue;
        }
        if(ch==BACKSPACE)
        {
            if(i==0)
            {
                continue;
            }
            else		             //遇后退键后退一位，吃掉“*",再后退一位
            {
                i--;
                putchar(BACKSPACE);
                putchar(' ');
                putchar(BACKSPACE);
            }
        }
        else		//将字符存到数组
        {
            p[i]=ch;
            putchar('*');
            i++;
        }
    }
    printf("\n");
    p[i]='\0';
    return p;
}

//判断用户名是否合法
int Judge_user(char *user)
{
    int flag=0;
    FILE *fp;
    char user1[21],pwd[15];
    if((fp=fopen("登陆信息.txt","r"))==NULL) flag=1;       //判断文件是否存在
    if(flag==0)
    {

        while(!(feof(fp)))
        {
            fscanf(fp,"%s %s",user1,pwd);     //读取已有用户的信息
            if(strcmp(user1,user)==0)   break;
        }

        if(!(feof(fp)))
        {
            printf("该用户名已存在，请换一个用户名:\n");
            return ERROR;
        }
        else
        {
            return OK;            //返回判断结果
        }

    }
    else return OK;


    if(fclose(fp))
    {
        printf("Can not close the file!创建失败！\n");
        exit(0);
    }
}

//主页面
void Ledge_ment()
{
    printf("\t\t ============活动管理============\n");
    printf("\t\t *·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【1】创建活动            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【2】浏览活动            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【3】删除活动            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【4】参加活动            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【5】查看参加活动并评论  *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【6】浏览活动评论        *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【7】查看已结束的活动    *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【8】注销用户            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *    【0】退出程序            *\n");
    printf("\t\t·                             ·\n");
    printf("\t\t *·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t ================================\n");
}

//判断在"全部活动.txt"中活动编号是否已存在
int Judge_number2(char *number)
{
    FILE *fp;
    int flag=0,flag1=0;
    struct activity_list *p1;
    if((fp=fopen("全部活动.txt","r"))==NULL) flag=1;
    if(flag==0)
    {
        p1=(struct activity_list *)malloc(sizeof(struct activity_list));                               //从文件中读取已有活动信息
        while(!(feof(fp)))
        {

            fscanf(fp,"%20s%20s%20d%20d%20s%20s%20s",p1->number,p1->content,&p1->count,&p1->join,p1->demand,p1->time1,p1->Cname);
            if((strcmp(p1->number,number))==0)
            {
                flag1=1;
                //判断活动编号是否已存在 ，存在则跳出
            }

        }

        if(flag1==1)
        {
            system("cls");
            printf("\n");
            printf("活动编号已存在! \n");
            printf("\n");
            return ERROR;

        }
        else return OK;
    }

    else  return OK;
    //返回判断结果
    if(fclose(fp))
    {
        printf("Can not close the file!\n");
        exit(0);
    }
}
//创建活动，并且对活动设置密码，以防误删
void Create_activity(char *user,char *pwd)
{
    FILE *fp;
    char *pwd3,pwd4[15],*num,s2[50];
    int i,n;
    struct activity_list * head, * p;
    head=Read();
    int size=sizeof(struct activity_list);
    p=(struct activity_list * )malloc(size);

    do
    {
        printf("请输入活动的编号：");
        scanf("%s",p->number);
    }
    while(!(Judge_number2(p->number)));
    printf("请输入活动内容:");
    scanf("%s",p->content);
    printf("请输入活动人数：");
    scanf("%d",&p->count);
    do
    {
        printf("请输入0：");
        scanf("%d",&p->join);
    }
    while((p->join)!=0);
    printf("请输入活动要求：");
    scanf("%s",p->demand);
    printf("请输入活动时间(eg:2015/04/23)：");
    scanf("%s",p->time1);
    printf("请输入创建者名：");
    scanf("%s",p->Cname);
    num=p->number;
    pwd3=Get_password("请另输入密码，以防误删(<=14)：");
    for(i=0; *pwd3!='\0'; i++)
    {
        pwd4[i]=*pwd3;
        pwd3++;
    }
    pwd4[i]='\0';
    pwd3=Get_password("请确认密码(<=14):");
    while(strcmp(pwd4,pwd3)!=0)
    {
        pwd3=Get_password("密码不一致,请重新输入(<=14):");
        for(i=0; *pwd3!='\0'; i++)
        {
            pwd4[i]=*pwd3;
            pwd3++;
        }
        pwd4[i]='\0';
        pwd3=Get_password("请确认密码(<=14):");
    }
    //将活动编号和密码存入 活动密码文件 中,以便删除或修改活动
    if((fp=fopen("活动密码.txt","a+"))==NULL) exit(0);
    fprintf(fp,"%s %s\n",num,pwd3);
    if(fclose(fp)) exit(0);
    //将活动保存
    head=InsertDoc(head,p);
    strcpy(s2,p->number);
    strcat(s2,"编号活动.txt");
    strcpy(filename,s2);
    if((fp=fopen(filename,"a+"))==NULL)
    {
        printf("File open error!\n");
        exit(0);
    }
    if(fclose(fp))
    {
        printf("Can not close the file!\n");
        exit(0);
    }
    system("cls");
    printf("\n");
    Ledge_ment();
    printf("\t\t\t活动创建成功!\n");
    printf("请输入你的选择(0~8):");
    scanf("%d",&n);
    User_choice(n,user,pwd);
}
/*读文件*/
struct activity_list * Read()
{
    struct activity_list *p1,*head=NULL,*tail;
    FILE * fp;

    if((fp=fopen("全部活动.txt","r"))==NULL)
    {
        goto here1;
    }

    while(!feof(fp))
    {
        p1=(struct activity_list *)malloc(sizeof(struct activity_list));
        fscanf(fp,"%20s%20s%20d%20d%20s%20s%20s",p1->number,p1->content,&p1->count,&p1->join,p1->demand,p1->time1,p1->Cname);
        if(head==NULL)
            head=p1;
        else
            tail->next=p1;
        tail=p1;
    }
    tail->next=NULL;
    fclose(fp);

here1:
    return head;
}
//浏览全部活动
void Search_activity(char *user,char *pwd,struct activity_list *head)
{
    struct activity_list * p1;
    head=Read();
    int n;
    if(head==NULL)
    {
        system("cls");
        Ledge_ment();
        printf("\t\t暂无活动存在，您可选择创建活动！\n");
        printf("请重新输入你的选择(0~8):");
        scanf("%d",&n);
        User_choice(n,user,pwd);
    }
    for(p1=head; p1; p1=p1->next)
    {
        printf("\n");
        printf("活动编号：%s\n活动内容：%s\n活动人数：%d\n已参加该活动的人数：%d\n活动要求：%s\n活动日期：%s\n活动创建者：%s\n",p1->number,p1->content,p1->count,p1->join,p1->demand,p1->time1,p1->Cname);
        printf("\n\n");
    }
    Ledge_ment();
    printf("\t\t温馨提示，鼠标上滑即可见活动!\n");
    printf("请输入你的选择(0~8):");
    scanf("%d",&n);
    User_choice(n,user,pwd);
}

//在删除中查询删除的编号
struct activity_list * Delete(char *user,char *pwd,char *temp)
{
    struct activity_list * ptr1,* ptr2,*head;
    int a=0,l;
    char b;
    head=Read();
    if(head==NULL)
    {
        printf("\n活动为空，无法删除！\n\n");
        return NULL;
    }
    while(head!=NULL&&strcmp(head->number,temp)==0)
    {
        ptr2=head;
        head=head->next;
        free(ptr2);
        a=1;
    }
    if(head==NULL)
    {
        printf("\n活动为空，无法删除！\n\n");
        return NULL;
    }
    ptr1=head;
    ptr2=head->next;
    while(ptr2!=NULL)
    {
        if(strcmp(ptr2->number,temp)==0)
        {
            ptr1->next=ptr2->next;
            free(ptr2);
            a=1;
        }
        else
            ptr1=ptr2;
        ptr2=ptr1->next;
    }
    if(ptr2==NULL&&a==0)
    {
        system("cls");
        Ledge_ment();
        printf("\t\t\t您所要删除的活动不存在！\n\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
        return head;
    }
    printf("\n确认删除该活动？（Y/N）：");
    getchar();
    b=getchar();
    if(b=='Y'||b=='y')
    {
        save(head);
    }
    else
    {
        system("cls");
        Ledge_ment();
        printf("\t\t\t未删除活动！\n\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    return head;
}
//删除活动（创建者才有资格删除）
void Delete_activity(char *user,char *pwd1)
{
    char number[21],s2[26],num[21];
    FILE *fp;
    char *pwd,pwd3[15];
    int j=1,l;
    if((fp=fopen("活动密码.txt","r"))==NULL)
    {

        Ledge_ment();
        printf("\t\t\t暂无已创建活动！\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd1);
    }
    printf("请输入想要删除的活动编号:");
    scanf("%s",number);
    pwd=Get_password("请输入活动密码(<=14):");         //读取用户名及密码

    while(!(feof(fp)))
    {
        fscanf(fp,"%s %s",num,pwd3);
        if(strcmp(num,number)==0)  break;
    }
    if(!feof(fp))
    {
        while(strcmp(pwd,pwd3)!=0)
        {

            printf("密码错误!\n");
            pwd=Get_password("请重新输入该活动密码:");
            j++;
            //判断用户名与密码是否匹配
            if(j>3) break;
        }
        if(j>=3)
        {
            system("cls");
            Ledge_ment();
            printf("\t\t多次输入错误密码,请稍后再删除!\n");
            printf("请输入你的选择(0~8):");
            scanf("%d",&l);
            User_choice(l,user,pwd1);
        }
        else
        {

            Delete(user,pwd1,number);
            strcpy(s2,number);
            strcat(s2,"编号活动.txt");
            strcpy(number,s2);
            remove(number);
            system("cls");
            Ledge_ment();
            printf("\t\t\t成功删除该活动！\n\n");
            printf("请输入你的选择(0~8):");
            scanf("%d",&l);
            User_choice(l,user,pwd);
        }
    }
    else
    {
        system("cls");
        Ledge_ment();
        printf("\t\t此活动不存在!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }

    if(fclose(fp)) exit(0);

}


//参加活动
void Join_activity(char *user,char *pwd,char *filename)
{
    char num[20],num1[20],s2[20],a;
    int l,m,n;
    FILE *fp,*fp1;
    struct activity_list * p,*head;
    head=Read();
    printf("请输入您想参加的活动的编号: ");
    scanf("%s",num);
    if(head==NULL)
    {
        Ledge_ment();
        printf("\t\t\t暂无活动存在!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    for(p=head; p; p=p->next)
    {
        if(strcmp(p->number,num)==0)
        {
            printf("\n");
            printf("活动编号：%s\n活动内容：%s\n活动人数：%d\n已参加该活动的人数：%d\n活动要求：%s\n活动日期：%s\n活动创建者：%s\n",p->number,p->content,p->count,p->join,p->demand,p->time1,p->Cname);
            printf("\n");
            m=p->join;
            n=p->count;
            if((fp=fopen(filename,"a+"))==NULL)
            {
                printf("File open error!\n");
                exit(0);
            }
            while(!feof(fp))
            {
                fscanf(fp,"%s",num1);
                if((strcmp(num1,num)==0))
                {
                    if(strcmp(num1,num)==0)
                    {
                        Ledge_ment();
                        printf("\t\t    您不能重复参加同一活动！\n");
                        printf("请输入你的选择(0~8):");
                        scanf("%d",&l);
                        User_choice(l,user,pwd);
                        break;
                    }
                }
            }
            if(feof(fp))
            {
                if(strcmp(num1,num)!=0)
                {

                    if(m<n)
                    {
                        fclose(fp);
                        printf("确认是否参加该活动？(Y or N):");
                        getchar();
                        a=getchar();
                        if(a=='Y'||a=='y')
                        {
                            if((fp=fopen(filename,"a+"))==NULL) exit(0);
                            printf("1\n");
                            fprintf(fp,"%s\n",num);
                            printf("2\n");
                            fclose(fp);
                            strcpy(s2,num);
                            strcat(s2,"编号活动.txt");
                            strcpy(num,s2);
                            if((fp1=fopen(num,"a+"))==NULL)    exit(0);
                            fprintf(fp1,"%s\n",user);
                            fclose(fp1);
                            p->join++;
                            save(head);
                            Ledge_ment();
                            printf("\t\t\t成功参加该活动!\n");
                            printf("请输入你的选择(0~8):");
                            scanf("%d",&l);
                            User_choice(l,user,pwd);
                        }
                        else
                        {
                            Ledge_ment();
                            printf("\t\t   您已放弃参加该活动!\n");
                            printf("请输入你的选择(0~8):");
                            scanf("%d",&l);
                            User_choice(l,user,pwd);
                        }
                    }
                    else
                    {
                        Ledge_ment();
                        printf("\t\t该活动人数已满，请另选活动参加!\n");
                        printf("请输入你的选择(0~8):");
                        scanf("%d",&l);
                        User_choice(l,user,pwd);
                    }
                }
            }
            break;
        }
        if(p->next==NULL)
        {
            Ledge_ment();
            printf("\t\t    未找到您所要参加的活动!\n");
            printf("请输入你的选择(0~8):");
            scanf("%d",&l);
            User_choice(l,user,pwd);
        }
    }
}

//评论中查找活动
int Search_activity1(char *user,char *pwd,char *temp)
{
    int a=0,l;
    struct activity_list * p1,*head;
    head=Read();
    p1=head;
    if(head==NULL)
    {
        Ledge_ment();
        printf("\t\t您暂未参加活动，您可选择参加活动!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    else
    {
        while(p1!= NULL)
        {
            if(strstr(p1->number,temp)!=NULL)
            {
                printf("\n");
                printf("活动编号：%s\n活动内容：%s\n活动人数：%d\n已参加该活动的人数：%d\n活动要求：%s\n活动日期：%s\n活动创建者：%s\n",p1->number,p1->content,p1->count,p1->join,p1->demand,p1->time1,p1->Cname);
                printf("\n\n");
                return OK;
            }
            p1=p1->next;

        }
    }
    save(head);
}
//查看参加的活动并评论
void Look_and_Discuss_activity(char *user,char *pwd,char *filename)
{
    int l,a=0;
    FILE *fp,*fp1;
    char num[21],num1[21],discuss1[21];
    struct activity_list * p,*head;
    p=head;
    head=Read();
    if((fp=fopen(filename,"r"))==NULL)
    {
        Ledge_ment();
        printf("\t\t您暂未参加活动，您可选择参加活动!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    while(!(feof(fp)))
    {
        fscanf(fp,"%s\n",num);
        Search_activity1(user,pwd,num);
    }
    if(fclose(fp)) exit(0);
    if((fp=fopen(filename,"r"))==NULL) exit(0);
    printf("\n");
    printf("请输入您想评论的活动编号：");
    scanf("%s",num1);
    while(!(feof(fp)))
    {
        fscanf(fp,"%s\n",num);
        if(strcmp(num,num1)==0) a++;

    }
    if(fclose(fp)) exit(0);
    if(a>0)
    {
        printf("请输入您想评论的内容：");
        scanf("%s",discuss1);
        if((fp1=fopen("活动评论.txt","a+"))==NULL) exit(0);
        fprintf(fp,"%s %s %s\n",num1,discuss1,user);
        if(fclose(fp1)) exit(0);
        Ledge_ment();
        printf("\t\t\t成功评论该活动！\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);

    }
    else
    {
        Ledge_ment();
        printf("\t\t您尚未参加该活动，您可选择参加该活动！\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }

}
//查看被评论的活动的评论内容
void Look_Discuss(char *user,char *pwd)
{
    int l;
    FILE *fp;
    char num[21],num1[21],discuss1[21];
    if((fp=fopen("活动评论.txt","r"))==NULL)
    {
        Ledge_ment();
        printf("\t\t\t暂无活动评论!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    while(!(feof(fp)))
    {
        fscanf(fp,"%s %s %s\n",num1,discuss1,user);
        {
            printf("\n");
            printf("活动编号：%s\n评论内容：%s\n活动评论者：%s\n",num1,discuss1,user);
            printf("\n");
        }

    }
    Ledge_ment();
    printf("\t\t 温馨提示，上滑鼠标可浏览活动!\n");
    printf("请输入你的选择(0~8):");
    scanf("%d",&l);
    User_choice(l,user,pwd);
    if(fclose(fp)) exit(0);
}
//查看已结束征集的活动
void Look_Ended(char *user,char *pwd)
{
    FILE *fp,*fp1;
    struct activity_list * p,*head;
    int m,n,a=0,l;
    char *num1,joinname[100],s2[21];
    head=Read();
    p=head;
    if(head=NULL)
    {
        Ledge_ment();
        printf("\t\t\t暂无活动存在！\n");
        printf("请重新输入你的选择(0~8):");
        scanf("%d",&n);
        User_choice(n,user,pwd);
    }
    if((fp=fopen("全部活动.txt","r"))==NULL)
    {
        Ledge_ment();
        printf("\t\t\t暂无活动存在！\n");
        printf("请重新输入你的选择(0~8):");
        scanf("%d",&n);
        User_choice(n,user,pwd);
    }
    while(!(feof(fp)))
    {
        fscanf(fp,"%30s%30s%30d%30d%30s%30s%30s",p->number,p->content,&p->count,&p->join,p->demand,p->time1,p->Cname);
        m=p->join;
        n=p->count;
        if(m==n)
        {
            printf("\n");
            printf("活动编号：%s\n活动内容：%s\n活动人数：%d\n已参加该活动的人数：%d\n活动要求：%s\n活动日期：%s\n活动创建者：%s\n",p->number,p->content,p->count,p->join,p->demand,p->time1,p->Cname);
            num1=p->number;
            strcpy(s2,num1);
            strcat(s2,"编号活动.txt");
            strcpy(num1,s2);
            printf("活动参加者：");
            if((fp1=fopen(num1,"r"))==NULL) exit(0);
            while(!(feof(fp1)))
            {
                fscanf(fp1,"%s ",joinname);
                printf("%s ",joinname);
            }
            if(fclose(fp1)) exit(0);
            printf("\n");
            a++;
        }

    }
    if(a==0)
    {
        printf("\n");
        Ledge_ment();
        printf("\t\t全部活动正在征集，暂无征集结束的活动!\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    else
    {
        Ledge_ment();
        printf("\t\t温馨提示，上滑鼠标可见征集结束的活动！\n");
        printf("请输入你的选择(0~8):");
        scanf("%d",&l);
        User_choice(l,user,pwd);
    }
    if(fclose(fp)) exit(0);
}


//用户选择
void User_choice(int l,char *user,char *pwd)
{
    struct activity_list *head;
    head=Read();
    int n,k;
    k=l;
    while(k<0||k>8)
    {
        printf("输入有误!请重新输入!");
        scanf("%d",&k);
    }
    switch(k)
    {
    case 1:
        Create_activity(user,pwd);
        break;
    case 2:
        Search_activity(user,pwd,head);
        break;
    case 3:
        Delete_activity(user,pwd);
        break;
    case 4:
        Join_activity(user,pwd,filename);
        break;
    case 5:
        Look_and_Discuss_activity(user,pwd,filename);
        break;
    case 6:
        Look_Discuss(user,pwd);
        break;
    case 7:
        Look_Ended(user,pwd);
        break;

    case 8:
        system("cls");
        printf("\t\t================活动管理================\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*\t   1.注册 2.登录 0.退出        *\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t========================================\n");
        printf("请输入你的选择:");
        scanf("%d",&n);
        while(n>2||n<0)
        {
            printf("\n");
            printf("输入有误!请重新输入!\n");
            printf("\n");
            scanf("%d",&n);
        }
        switch(n)
        {
        case 1:
            Register();
            break;
        case 2:
            Login();
            break;
        case 0:
            exit(0);

        }
        break;
    case 0:
        exit(0);
        break;

    }
}

//用户登录
void Login()
{
    char user[21],s2[26],user1[21],ch;
    FILE *fp;
    char *pwd,pwd1[15];
    int j=1,n,l;
    printf("请输入用户名:\n");
    scanf("%s",&user);
    pwd=Get_password("请输入密码(<=14):");         //读取用户名及密码

    if((fp=fopen("登陆信息.txt","r"))==NULL)
    {
        system("cls");
        printf("\t\t 暂无用户名注册，您可选择先注册再登录！\n");
        printf("\t\t================活动管理================\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*\t   1.注册 2.登录 0.退出        *\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t========================================\n");
        printf("请输入你的选择:");
        scanf("%d",&n);
        switch(n)
        {
        case 1:
            Register();
            break;
        case 2:
            Login();
            break;
        case 0:
            exit(0);
        }
    }

    while(!(feof(fp)))
    {
        fscanf(fp,"%s%s",user1,pwd1);
        if(strcmp(user1,user)==0)  break;   //判断用户名是否存在
    }
    if(!feof(fp))
    {
        while(strcmp(pwd,pwd1)!=0)
        {

            printf("密码错误!\n");
            pwd=Get_password("请重新输入:");
            j++;
            //判断用户名与密码是否匹配
            if(j>3) break;
        }
        if(j>=3)
        {
            printf("多次输入错误密码,稍后登录!");
            exit(0);
        }
        else
        {
            strcpy(s2,user);       //登录成功，把用户所在文件调出
            strcat(s2,".txt");
            strcpy(filename,s2);
            if((fp=fopen(filename,"a"))==NULL)
            {
                printf("File open error!\n");
                exit(0);
            }
            if(fclose(fp))
            {
                printf("Can not close the file!\n");
                exit(0);
            }
            system("cls");
            Ledge_ment();
            printf("请输入你的选择(0~8):");
            scanf("%d",&l);
            User_choice(l,user,pwd);                            //登录成功，进入活动管理程序
        }
    }
    else
    {
        system("cls");
        printf("\t\t\t     此用户名不存在!\n");
        printf("\t\t================活动管理================\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*\t   1.注册 2.登录 0.退出        *\n");
        printf("\t\t.                                      .\n");
        printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
        printf("\t\t========================================\n");
        printf("请输入你的选择:");
        scanf("%d",&n);
        switch(n)
        {
        case 1:
            Register();
            break;
        case 2:
            Login();
            break;
        case 0:
            exit(0);
        }
    }

    if(fclose(fp)) exit(0);

}

//注册用户
void Register()
{
    char user[21],s1[26];
    FILE *fp;
    int i,n;
    char *pwd1,pwd2[15];
    do
    {
        printf("请输入用户名(<=20):\n");                //判断输入的用户名是否超过规定字数，不合法重新输入
        scanf("%s",user);
        while(strlen(user)>20)
        {
            printf("用户名超过规定字数，请重新输入:");
            scanf("%s",&user);
        }
    }
    while(!(Judge_user(user)));                       //调用 Judge_user()函数，判断用户名是否存在，存在重新输入

    pwd1=Get_password("请输入密码:");                  //判断二次输入的密码是否相同 ，不同重新输入
    for(i=0; *pwd1!='\0'; i++)
    {
        pwd2[i]=*pwd1;
        pwd1++;
    }
    pwd2[i]='\0';
    pwd1=Get_password("请确认密码:");
    while(strcmp(pwd2,pwd1)!=0)
    {
        pwd1=Get_password("密码不一致,请重新输入(<=14):");
        for(i=0; *pwd1!='\0'; i++)
        {
            pwd2[i]=*pwd1;
            pwd1++;
        }
        pwd2[i]='\0';
        pwd1=Get_password("请确认密码(<=14):");
    }
    if((fp=fopen("登陆信息.txt","a"))==NULL)  	exit(0);

    fprintf(fp,"%s %s\n",user,pwd1);
    if(fclose(fp)) 	exit(0);

    system("cls");                                 //创建成功，清屏，返回创建及登录界面
    printf("\t\t\t\t创建成功！\n");
    printf("\t\t================活动管理================\n");
    printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t.                                      .\n");
    printf("\t\t*\t   1.注册 2.登录 0.退出        *\n");
    printf("\t\t.                                      .\n");
    printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t========================================\n");
    printf("请输入你的选择:");
    scanf("%d",&n);
    switch(n)
    {
    case 1:
        Register();
        break;
    case 2:
        Login();
        break;
    case 0:
        exit(0);
    }
}


//主函数
int main()
{
    int n;
    system("Color 3f");
    printf("\t\t================活动管理================\n");
    printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t.                                      .\n");
    printf("\t\t*\t   1.注册 2.登录 0.退出        *\n");
    printf("\t\t.                                      .\n");
    printf("\t\t*·*·*·*·*·*·*·*·*·*·*·*·*·*\n");
    printf("\t\t========================================\n");
    printf("请输入你的选择:");
    scanf("%d",&n);
    while(n>2||n<0)
    {
        printf("\n");
        printf("输入有误!请重新输入!\n");
        printf("\n");
        scanf("%d",&n);
    }
    switch(n)
    {
    case 1:
        Register();
        break;
    case 2:
        Login();
        break;
    case 0:
        exit(0);

    }
}
