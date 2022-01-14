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
  int cup1,cup2,i=0;
  char ch;

  cup1=a->len;         /*交换len*/
  a->len=b->len;
  b->len=cup1;

  cup2=a->alloc;       /*交换alloc*/
  a->alloc=b->alloc;
  b->alloc=cup2;

  while(*(a->buf+i)!='\0')  /*交换字符串*/
  {
    ch=*(a->buf+i);
    *(a->buf+i)=*(b->buf+i);
    *(b->buf+i)=ch;
    i++;
  }
}

//将 sb 中的原始内存取出，并获得其长度
/*理解：
返回buf的内存地址
长度返给sz
重置结构体（最简单的free掉重新init，但应该还有其他方法）
*/
char *strbuf_detach(struct strbuf *sb, size_t *sz)
{
  return sb->buf; /*输出还是return不太确定*/
  sz=(size_t*)sb->len;
  free(sb->buf);
  void strbuf_init(struct strbuf *sb, size_t alloc);
}

//比较两个 strbuf 的内存是否相同
int strbuf_cmp(const struct strbuf *first, const struct strbuf *second)
{
  if(sizeof((struct strbuf) *first)==sizeof((struct strbuf )*second))
  {
    printf("相同\n");
  }else printf("不同\n");
}

//清空 sb
void strbuf_reset(struct strbuf *sb)
{
  memset(&sb,0,sizeof(sb));  /*函数memset(&struct_name,0,sizeof(struct_name))*/
}


/*2B*/
//将 sb 的长度扩大 extra
void strbuf_grow(struct strbuf *sb, size_t extra)
{
  sb->len=sb->len+extra;
}

//向 sb 追加长度为 len 的数据 data
/*和attach有点像*/
void strbuf_add(struct strbuf *sb, const void *data, size_t len)
{
  int n;
  sb->len=sb->len+len;
  if(sb->len>=sb->alloc)
  {
    printf("想要扩充空间为原来的几倍？：\n");
    scanf("%d",&n);
    sb->alloc=sb->alloc*n;
    sb->buf=(char*)malloc(sizeof(char)*sb->alloc);
  }
  strcat(sb->buf,(char*)data);
}

//向 sb 追加一个字符 c
void strbuf_addch(struct strbuf *sb, int c)
{
  int n;
  sb->len+=1;
  if(sb->len>=sb->alloc)
  {
    printf("想要扩充空间为原来的几倍？：\n");
    scanf("%d",&n);
    sb->alloc=sb->alloc*n;
    sb->buf=(char*)malloc(sizeof(char)*sb->alloc);
  }
  strcat(sb->buf,(char*)c);
}

//向 sb 追加一个字符串 s
/*和strbuf_add*/
void strbuf_addstr(struct strbuf *sb, const char *s)
{
  int n;
  sb->len=sb->len+strlen(s);
  if(sb->len>=sb->alloc)
  {
    sb->alloc=sb->alloc*2;
    sb->buf=(char*)malloc(sizeof(char)*sb->alloc);
  }
  strcat(sb->buf,s);
}

//向一个 sb 追加另一个 strbuf的数据
void strbuf_addbuf(struct strbuf *sb, const struct strbuf *sb2)
{
  int n;
  sb->len=sb->len+sb2->len;
  if(sb->len>=sb->alloc)
  {
    printf("想要扩充空间为原来的几倍？：\n");
    scanf("%d",&n);
    sb->alloc=sb->alloc*n;
    sb->buf=(char*)malloc(sizeof(char)*sb->alloc);
  }
  strcat(sb->buf,sb2->buf);
}

//设置 sb 的长度 len
void strbuf_setlen(struct strbuf *sb, size_t len)
{
  sb->len=len;
}

//计算 sb 目前仍可以向后追加的字符串长度
size_t strbuf_avail(const struct strbuf *sb)
{
  int remain;
  remain=sb->alloc-sb->len;
  printf("%d",remain);
  return remain;
}

//向 sb 内存坐标为 pos 位置插入长度为 len 的数据 data
/*0000000000000000000000000000000000000000000000000000000000000000000000000
pos超过len/pos没超过len
*/
void strbuf_insert(struct strbuf *sb, size_t pos, const void *data, size_t len)
{
  int i,j;
  /*先对pos判断*/
    if(pos>sb->len)  //pos超过len
      sb->len=pos+len;
    else sb->len+=len; //pos没超过len

    char *temp=(char*)malloc(sizeof(char)*(sb->len));

    if(pos<sb->len-len)
    {
      for(i=0;i<sb->len;i++)
        *(temp+i)=*((sb->buf)+pos +i);
    }
    //.....
}


/*2C*/
//去除 sb 缓冲区左端的所有 空格，tab, '\t'
void strbuf_rtrim(struct strbuf *sb)
{
  int num,i;
  char *temp =(char*)malloc(sizeof(char)*sb->alloc);
  num=0;
  for(i=0;i<sb->len;i++)  //遍历
  {
    if(*(sb->buf+i)!=' '&&*(sb->buf+i)!='  '&&*(sb->buf+i)!='\t')
    {
      *(temp+num)=*(sb->buf+i); //拷贝粘贴进去
      num++;
    }
  }
  sb->len=num;
  sb->buf=temp;
}

//去除 sb 缓冲区右端的所有 空格，tab, '\t'
void strbuf_ltrim(struct strbuf *sb)  //遇到就变成\0
{
  int i;
  for(i=sb->len;i<sb->alloc;i++)
  {
    if(*(sb->buf+i)==' '||*(sb->buf+i)=='  '||*(sb->buf+i)=='\t')
    {
      *(sb->buf+i)='\0';
    }
  }
}

//删除 sb 缓冲区从 pos 坐标长度为 len 的内容
/*和strbuf_rtrim相似*/
void strbuf_remove(struct strbuf *sb, size_t pos, size_t len)
{
  int num,i;
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
  sb->buf=temp;
}


/*2D*/
//为 sb 直接扩容 hint ? hint : 8192 大小， 然后将文件描述符为 fd 的所有文件内容读取到 sb 中
ssize_t strbuf_read(struct strbuf *sb, int fd, size_t hint)
{

}

//为 sb 直接扩容 hint ? hint : 8192 大小， 然后将路径为 path 的所有文件内容读取到 sb 中
ssize_t strbuf_read_file(struct strbuf *sb, const char *path, size_t hint)
{

}

//将 将文件句柄为 fp 的一行内容读取到 sb
int strbuf_getline(struct strbuf *sb, FILE *fp)
{

}


/*CHALLENGE*/
//将长度为 len 的字符串 str 根据切割字符 terminator 切成多个 strbuf,并从结果返回，max 可以用来限定最大切割数量
struct strbuf **strbuf_split_buf(const char *str, size_t len, int terminator, int max)
{

}

//target_str : 目标字符串，str : 前缀字符串，strlen : target_str 长度 ，前缀相同返回 true 失败返回 false
bool strbuf_begin_judge(char* target_str, const char* str, int strlen)
{

}

//target_str : 目标字符串，begin : 开始下标，end 结束下标。len : target_buf的长度，参数不合法返回 NULL. 下标从0开始，[begin, end]区间
char* strbuf_get_mid_buf(char* target_buf, int begin, int end, int len)
{

}