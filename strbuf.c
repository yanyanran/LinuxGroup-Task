#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "strbuf.h"
 
/*struct strbuf
{
    int len; //长度
    int alloc; //容量
    char *buf;  //字符串
};*/
 
 
/*2A*/
 
//初始化 sb 结构体，容量为 alloc
void strbuf_init(struct strbuf *sb, size_t alloc)
{
  sb->buf=(char*)malloc(sizeof(char)*alloc);
  sb->alloc=alloc;
  sb->len=0;
}
 
//将字符串填充到 sb 中，长度为 len, 容量为 alloc
void strbuf_attach(struct strbuf *sb, void *str, size_t len, size_t alloc)
{
  sb->alloc = alloc;
	sb->len = len;
	sb->buf=(char*)str;
}
 
//释放 sb 结构体的内存
void strbuf_release(struct strbuf *sb)
{
  free(sb->buf);
}
 
//交换两个 strbuf
void strbuf_swap(struct strbuf *a, struct strbuf *b)
{
  int cup1;
  char*cup2;
 
  cup1=a->len;   /*交换len*/
  a->len=b->len;
  b->len=cup1;
 
  cup1=a->alloc;    /*交换alloc*/
  a->alloc=b->alloc;
  b->alloc=cup1;
 
  cup2=a->buf;  /*交换字符串*/
  a->buf=b->buf;
  b->buf=cup2;
}
 
//将 sb 中的原始内存取出，并获得其长度
char *strbuf_detach(struct strbuf *sb, size_t *sz)
{
  char *ss;
  ss= sb->buf;
  *sz = sb->alloc;
  return ss; 
}
 
//比较两个 strbuf 的内存是否相同
int strbuf_cmp(const struct strbuf *first, const struct strbuf *second)
{
    if(first->len>second->len)
        return 1;
    else if(first->len<second->len)
        return -1;
    else
        return 0;
}
 
//清空 sb
void strbuf_reset(struct strbuf *sb)
{
    /*strbuf_init(sb, sb->alloc);
	free(sb->buf);*/
    for(int i=0;i<sb->len;i++)
    {
    sb->buf[i]='\0';
    }
    sb->len=0;
}
 
 
/*2B*/
 
 
//将 sb 的长度扩大 extra
void strbuf_grow(struct strbuf *sb, size_t extra)
{
  sb->buf=(char*)realloc(sb->buf,extra);
  sb->alloc=sb->len+extra;
}
 
//向 sb 追加长度为 len 的数据 data
void strbuf_add(struct strbuf *sb, const void *data, size_t len)
{ 
    if(data==NULL)
    {
	return;
    }
    if(sb->len+len>=sb->alloc)
    {
        sb->buf=(char *)realloc(sb->buf,sb->len+len+1);
        sb->alloc=sb->len+len+1;
    }
    memcpy(sb->buf+sb->len,data,len);
    sb->len+=len;
    sb->buf[sb->len]='\0';
}
 
//向 sb 追加一个字符 c
void strbuf_addch(struct strbuf *sb, int c)
{
  if(sb->len+2>=sb->alloc)
  {
    sb->alloc=sb->alloc*2;
    sb->buf=(char*)realloc(sb->buf,sizeof(char)*(sb->alloc)*2);
  }
  memcpy(sb->buf+sb->len,&c,2);
  sb->len++;
}
 
//向 sb 追加一个字符串 s
/*和strbuf_add*/
void strbuf_addstr(struct strbuf *sb, const char *s)
{
  strbuf_add(sb,s,strlen(s));
  sb->buf[sb->len]='\0';
  /*sb->len=sb->len+strlen(s);
  if(sb->len+1>=sb->alloc)
  {
    sb->alloc=sb->len;
    sb->buf=(char*)realloc(sb->buf,sb->alloc+1);
  }
  strcat(sb->buf,s);*/
}
 
//向一个 sb 追加另一个 strbuf的数据
void strbuf_addbuf(struct strbuf *sb, const struct strbuf *sb2)
{
  strbuf_addstr(sb,sb2->buf);
}
 
//设置 sb 的长度 len
void strbuf_setlen(struct strbuf *sb, size_t len)
{
  sb->len=len;
  sb->buf[sb->len]='\0';
}
 
//计算 sb 目前仍可以向后追加的字符串长度
size_t strbuf_avail(const struct strbuf *sb)
{
  if(sb->alloc==0)
  {
    return 0;
  }else
  {
  int remain=(sb->alloc)-(sb->len)-1;
  return remain;
  }
}
 
//向 sb 内存坐标为 pos 位置插入长度为 len 的数据 data
void strbuf_insert(struct strbuf *sb, size_t pos, const void *data, size_t len)
{
  /*int i,j;
    if(pos>sb->len)  //pos超过len
      sb->len=pos+len;
    else sb->len+=len; //pos没超过len
    char *temp=(char*)malloc(sizeof(char)*(sb->len));
    if(pos<sb->len-len)
    {
      for(i=0;i<sb->len;i++)
        *(temp+i)=*((sb->buf)+pos +i);
    }*/
  int n_len=len+sb->len;
  int n_alloc=sb->alloc;
//判断内存是否足够，内存不够扩大长度
  if(n_alloc<sizeof(char)*n_len)
  {
	strbuf_grow(sb, sizeof(char*)*len);
  }
//分段重置
  for(int i=sb->len;i>=pos;i--)  
  {
	sb->buf[i+len]=sb->buf[i];
  }
  for(int j=0,k=pos;j<len;j++,k++)
  {
	sb->buf[k]=((char*)data)[j];
  }
  sb->len=n_len;
}
 
 
/*2C*/
 
 
//去除 sb 缓冲区右端的所有 空格，tab, '\t'
void strbuf_rtrim(struct strbuf *sb)
{
  /*int num=0,i;
  char *temp=(char*)malloc(sizeof(char)*(sb->len));
  for(i=0;i<sb->len;i++)  //遍历
  {
    if(*(sb->buf+i)!=' '&&*(sb->buf+i)!='  '&&*(sb->buf+i)!='\t')
    {
      *(temp+num)=*(sb->buf+i); //拷贝粘贴进去
      num++;
    }
  }
  sb->len=num;
  sb->buf=temp;*/
  //遍历识别到存在删除项时让长度减1，完了直接在长度末尾结束字符串
  while(sb->buf[sb->len-1]==' '||sb->buf[sb->len-1]=='\t')
  {
    sb->len--; 
  }
  sb->buf[sb->len]='\0';
}
 
//去除 sb 缓冲区左端的所有 空格，tab, '\t'
void strbuf_ltrim(struct strbuf *sb)  //遇到就变成\0
{
  /*int i;
  for(i=sb->len;i<sb->alloc;i++)
  {
    if(*(sb->buf+i)==' '||*(sb->buf+i)=='  '||*(sb->buf+i)=='\t')
    {
      *(sb->buf+i)='\0';
    }
  }*/
  char *s=sb->buf;
  while(*s=='\t'||*s==' ')
  {
    sb->len--;
    s++;
  }
  memmove(sb->buf,s,sb->len);
}
 
//删除 sb 缓冲区从 pos 坐标长度为 len 的内容
/*和strbuf_rtrim相似*/
void strbuf_remove(struct strbuf *sb, size_t pos, size_t len)
{
  /*int num,i;
  char *temp =(char*)malloc(sizeof(char)*sb->alloc);
  num=0;
  for(i=0;i<sb->len;i++)
  {
    if(*(sb -> buf + i)!='\0')  //判断条件变一下
    {
      *(temp+num)=*(sb->buf+i);
      num++;
    }
  }
  sb->len=num;
  sb->buf=temp;*/
  memmove(sb->buf+pos,sb->buf+pos+len,sb->len-pos-len);
  sb->len=sb->len-len;
}
 
 
/*2D*/
 
 
//为 sb 直接扩容 hint ? hint : 8192 大小， 然后将文件描述符为 fd 的所有文件内容读取到 sb 中
ssize_t strbuf_read(struct strbuf *sb, int fd, size_t hint)
{
  /*int o_len,o_alloc,f,r;
  o_len=sb->len;
  o_alloc=sb->alloc;
  strbuf_grow(sb,hint);
  char*num=sb->buf+sb->len;
  while(r=read(fd,num,hint))
  {
    if(r==0)
      break;
    f=r;
    sb->len=sb->len+f;
  }
  if(sb->len==o_len)
  {
    sb->alloc=o_alloc;
  }else{
    sb->buf[sb->len]='\0';
  }
  return 1;*/
 
    FILE *fp;
    char e;
    if(((fp=fdopen(fd,"r"))==NULL)||(e=fgetc(fp))==EOF) 
        return sb->len;
    else {
        sb->alloc+=(hint?hint:8192);
        sb->buf=(char*)realloc(sb->buf,sizeof(char)*(sb->alloc));
        sb->buf[sb->len++]=e;
        while((e=fgetc(fp))!=EOF)
	{ 
            sb->buf[sb->len]=e;
            sb->len++;
        }
        sb->buf[sb->len]='\0'; 
        return sb->len;
    }
}
 
//将文件句柄为 fp 的一行内容读取到 sb
int strbuf_getline(struct strbuf *sb, FILE *fp)//行
{
  int cnt=0,i;
  while(1)
  {
    if(((i=fgetc(fp))=='\n'||i==EOF))
      break;  //读取到行结束或文件结尾跳出
 
    if(strbuf_avail(sb)>=1)  //可追加字符数量达标
    {
      strbuf_addch(sb,i); 
      cnt++;
    }else if(strbuf_avail(sb)<1)
    {
      sb->buf=(char*)realloc(sb->buf,sizeof(char)*(sb->alloc+1));
      strbuf_addch(sb,i); 
      cnt++;
    }
  }
  sb->len=cnt;
  return 1;
}
 
 
/*CHALLENGE*/
 
 
//将长度为 len 的字符串 str 根据切割字符 terminator 切成多个 strbuf,并从结果返回，max 可以用来限定最大切割数量
struct strbuf **strbuf_split_buf(const char *str, size_t len, int terminator, int max)
{
   /* 
    int count=0,i;
    char q[2]; 
    q[0]=(char)terminator;
    q[1]='\0';
    char s[len+1];
    char*r=strtok(s,q);//分割字符串
    int r_len=strlen(r);
    struct strbuf **t=NULL;
    t=(struct strbuf**)realloc(t,sizeof(struct strbuf*)*(count+1));
    struct strbuf *j;
    memcpy(s,str,len+1);
    
    for(i=0;i<len;i++)
    {
      if(s[i]=='\0')
      s[i]='{';
    }
    
    while(r!=NULL&&count<max)
    {   
      for(i=0;i<r_len;i++)
      {
        if(r[i]=='{')
        r[i]='\0';
      }
      j=(struct strbuf*)malloc(sizeof(struct strbuf));
      strbuf_init(j,r_len+1);
      strbuf_add(j,r,r_len);
      t[count++]=j;
      t=(struct strbuf**)realloc(t,sizeof(struct strbuf*)*(count+1));
      r=strtok(NULL,q);
    }
    t[count]= NULL;
    return t;*/
 
    char q[2]; 
    q[0]=(char)terminator;
    q[1]='\0';
    int count=0,i;
      
    struct strbuf **t=NULL;
      t=(struct strbuf**)realloc(t,sizeof(struct strbuf*)*(count+1));
    struct strbuf *j;
      
    char s[len+1];
    memcpy(s,str,len+1);
    
    for(i=0;i<len;i++)
    {
      if(s[i]=='\0')
      s[i]='{';
    }
    char*r=strtok(s,q);
    while(r!=NULL&&count<max)
    {   
      int r_len=strlen(r);
      for(i=0;i<r_len;i++)
      {
        if(r[i]=='{')
        r[i]='\0';
      }
      j=(struct strbuf*)malloc(sizeof(struct strbuf));
      {
        strbuf_init(j,r_len+1);
        strbuf_add(j,r,r_len);
      }
      t[count++]=j;
      t=(struct strbuf**)realloc(t,sizeof(struct strbuf*)*(count+1));
      r=strtok(NULL,q);
    }
    t[count]= NULL;
    return t;
}
 
/*
target_str : 目标字符串
str : 前缀字符串
len : target_str长度
前缀相同返回 true，失败返回 false
*/
bool strbuf_begin_judge(char* target_str, const char* str, int len)
{
  if(str==NULL||strncmp(target_str,str,strlen(str))==0)
    return true;
  else
    return false;
}
 
/*
target_str : 目标字符串
begin : 开始下标
end：结束下标
len : target_buf的长度
参数不合法返回 NULL
下标从0开始，[begin, end]区间
*/
char* strbuf_get_mid_buf(char* target_buf, int begin, int end, int len)
{
  int i,j=0;
  if (target_buf==NULL||begin>end)
    return NULL;
  char*flag=(char*)malloc(sizeof(char)*(end-begin+2));
  for(i=begin;i<=end;i++)
  {
    flag[j++]=target_buf[i];
  }
  flag[end-begin]='\0';
  return flag;
}
