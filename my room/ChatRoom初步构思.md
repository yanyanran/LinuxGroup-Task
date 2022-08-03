

# **ChatRoom初步构思**

![image-20220712115302120](/home/yanran/.config/Typora/typora-user-images/image-20220712115302120.png)

##### 客户端

ChatClient.java -> 客户端窗口

​								发起连接

​								关闭

​								聊天

##### 服务器端

ChatServer.java -> 启动服务器端口并监听

ServerFrame.java -> 服务器窗口

ServerProcess.java -> 服务器与客户端的处理 -> 登陆

​																				   注册

​																			       发送信息

​																				   退出

- ------

  用户注册、登陆、注销（7.19设计-----7.20完成）

- 框架以及页面选项设计（7.20设计-----7.25完成账户页面功能-----）

------

![image-20220731192157523](/home/yanran/.config/Typora/typora-user-images/image-20220731192157523.png)

> - ##### 数据库表的构建：（7.20）
>
> *应该根据系统架构中的组件划分，针对每个组件所处理的业务进行组件单元的数据库设计；不同组件间所对应的数据库表之间的关联应尽可能减少，如果不同组件间的表需要外键关联也尽量不要创建外键关联，而只是记录关联表的一个主键，确保组件对应的表之间的独立性，为系统或表结构的重构提供可能性。*

**群聊列表和好友列表避免一个用户/一个群创建一个表** 

- [x] 用户数据表 （注册后放到这里面）**client** --> id、username、password、state（初始0为离线，1为在线）

- [x] 好友列表 （*互为好友关系*时两人放到此表的同一行里）**friend_list** -->  user1（用户1），user2（用户2），type（属性：0普通好友，1为消息屏蔽(黑名单)好友），send（发送申请方），yes（接收申请方）

- [x] 聊天记录的存储用一个表来记录（输入好友名发起聊天）**history_msg** -->   id，fromc（发送方），toc（接收方），msg_type（消息类型：0--文本//设置群管理通知  1--文件//3 -- 好友申请  4 --*群申请*），msg（消息内容：字符串 本地路径），sendtime（发送时间）**【show单聊记录、单聊未读、群聊未读】**

  state --> 消息状态，0：对方在线，已发送；1对方离线，可用于1登陆主页面显示未读消息数量2选择查看页面，显示未读消息内容（查看完未读消息后把state设置为0）

- [x] 群聊列表 **group_list** -->  group_id （唯一群聊ID）、group_name、user、user_type（身份属性 0：群主、1：群众、2：管理员）

  一个对应一个群聊名对应一个群内身份，需要调用时直接使用共性查找 

- [x] 群聊记录 **group_msg** --> id（群id）、fromc （发送人me）、msg_type （消息类型）、msg （消息内容）、time （发送时间）。一个群一个id，所有群记录放在同个表里，按照id区分，调用查看id即可（索引）**【show群聊记录】**

  群聊有人发送消息时，消息*存俩表*：

  1、（查询全群聊天记录用 -- group_msg）

  2、（个人查询未读消息的时候用 -- history_msg）

  from -- 【group_msg获取，存上面那个表里的 id+from 合成的String】

  to -- memsg_type -- 【group_msg表获取】

  msg -- 【group_msg表获取】

  state -- 按照状态正常定sendtime -- 【group_msg表获取】您有一条管理员身份变动通知

  > 发送的时候ResultSet扫描群内用户，分两个ArrayList，一个个判断是否在线，在线的存一个ArrayList不在线的存一个，消息发送的时候遍历两个ArrayList发送（在线的通知+存表state=0，不在线的存表state=1）历史消息存表需要存两次

- [ ] ![image-20220801092318289](/home/yanran/.config/Typora/typora-user-images/image-20220801092318289.png)

- [ ] ![](/home/yanran/.config/Typora/typora-user-images/image-20220803151250503.png)

- [ ] ![image-20220727121020667](/home/yanran/.config/Typora/typora-user-images/image-20220727121020667.png)

![image-20220727110424181](/home/yanran/.config/Typora/typora-user-images/image-20220727110424181.png)

![image-20220727110507738](/home/yanran/.config/Typora/typora-user-images/image-20220727110507738.png)

------

### **问题：**

- [x] 先启动服务端再启动客户端，两边成功连接后在客户端启动Logn.run()调出用户登陆页面。登陆成功后给服务器端发送XXX上线，**退出登陆同样给服务端发XXX离线，但现在是需要完全退出系统关闭通道，服务端才会报“离线”**。（已解决：将shutdownGracefully放到closeFuture前面去就OK）

- [x] **不可以同时登陆两个帐号**（已解决：数据库中用户表的state，输入用户名之后遍历state值为1的name，如果有重复就显示在线不能重复登陆）

- [ ] 直接关闭程序进程后退出不会i导致state还原为0，会影响下次登陆（尝试在服务端连接channel中连接数据库将state设置为0,但获取不到当前用户名）

- [x] 登陆成功后拿到当前登陆帐号的username

- [x] 黑名单

- [x] 聊天过程中对方退出了，消息何去何从（发一次消息监测一次对方状态即可）

- [ ] 单聊中文件的发送（对方不在线能否发送？存哪儿？）

- [ ] 输出好友列表的时候顺带输出它的状态（需要连接两个表查询，后面再补充吧）

- [x] 添加黑名单好友的时候多一个“退出”操作（后面再补）

- [ ] FriendManageHandler中的setFriendList在展示完好友列表后用户选择退出，跳到上一层的页面不可操作 （半解决：不用new，直接原本基础上将所有页面用while(true)无限循环，跳出当前操作直接用rreturn跳出）

- [x] 展示好友列表的lsit在add有问题 

- [ ] 数据库连接池 

- [ ] 枚举 密码加密

- [ ] ctrl D和ctrl C的屏蔽

- [ ] 记得处理非法输入

- [x] 数据库连接应该在服务端

  客户端向服务器端发出申请

- [x] 用户登陆后页面显示（登陆成功后将状态改为在线，接收消息只存入“消息历史记录”表中）

- [x] 私聊、群聊、文件传输的实现都是**由客户端发到服务器，再由服务器发送到目标客户端**

- [x] **关于发消息：**登陆的时候一个帐号名绑定一个channel，发送消息时通过帐号名查找到对应的channel，然后通过服务器发送消息

- [ ] TCP半关闭 find（告诉对端不发了） 那读关闭如何实现？

- [ ] TCP快速重传 

- [ ] ​      /home/yanran/Downloads

- [x] 是否需要一个表来存放好友申请？ （不需要）0群主、1群众、2管理员、3全部显示

- [x] ```java
  // 查看加入的群列表 -- 客户端接收群列表
  try {
      synchronized (waitMessage) {
          waitMessage.wait();
      }
  } catch (InterruptedException e) {
      e.printStackTrace();
  }
  
  if (waitSuccess == 1) {
      System.out.println("以下是您已加入的群列表：");
      // 输出群ID和群名（map）
      Map<Integer, String> groupList = ServerToClientMsg.getMsgMap();
      msgMap.clear();  // 归0
      // map按照键排个序
      List<Map.Entry<Integer, String>> list = new ArrayList<Map.Entry<Integer, String>>(groupList.entrySet());
      Collections.sort(list, new Comparator<Map.Entry<Integer, String>>() {
          @Override
          public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
              // 升序排
              return Integer.parseInt(String.valueOf(o1.getKey())) - Integer.parseInt(String.valueOf(o2.getKey()));
          }
      });
      // 输出
      for (Map.Entry<Integer, String> entry : list) {
          System.out.println(entry.getValue());
      }
  ```
  
  ```java
  String sql = "select user2 from friend_list where user1=send and user2=yes and type=0 and user1='" + fromUser + "'";
  Statement stm = con.createStatement();
  ResultSet rs = stm.executeQuery(sql);
  while (rs.next())
  ```
  
  ```java
  // 启动客户端,等待连接服务端,同时将异步改为同步
  ChannelFuture channelFuture = bootstrap.connect(ip,port).sync();
  Channel channel = channelFuture.channel();
  System.out.println("-------" +nchannel.localAddress().toString().substring(1) + "--------");
  
  // 输入
  Scanner input = new Scanner(System.in);
  
  while (input.hasNextLine()) {
  	String msg = input.nextLine();
  	//向服务端发送消息
  	channel.writeAndFlush(msg);
  }
  ```

------

**-----------您有？条未读消息-----------**

**（A）好友管理**

​					**查看好友列表**

​					**查看黑名单**  添加黑名单好友、删除黑名单好友 

​					**添加好友**：【请输入您想要添加的好友名字】 --- 【是否确定添加为好友？】--- **（是）**发给服务端判断client中是否存在此人 ---> 再判断此人是否在自己的好友列表中 ---> 给对方*发送好友请求*【发送成功，等待对方验证中】、**（否）**【您已取消操作】

​					**删除好友**

**（B）聊天群管理**

​					**查看我的群列表** --- 列出群列表的时候格式为id+群名

​									***我加入的群***（群众1）

​												**查看群组成员**

​												**退出群聊**

​									***我创建的群***（群主0）

​												**查看群组成员**

​												**添加群管理员**

​												**删除群管理员**

删除管理员时：

判断1：正在查询操作人是否是此群的群主（您无权移除本群的群管理员！）

判断2：正在查询操作对象是否是此群的管理员（移除失败！用户不是此群的群管理员！）



​												**删除群成员**

​												**解散群聊**

​									***我管理的群***（管理员2）

​												**查看群组成员**

​												**删除群成员**

​												**退出群聊**

​					**创建新的群聊** -- 允许创建同名的群聊名，靠群ID区分

​					**申请加入群聊** 数据库查找是否存在此群 --- 存在

**（C）好友聊天**

​					**发起聊天**

发起聊天：先列出非黑名单的好友列表
再选择想要聊天的对象（输入对方名字）
显示（打印）与对方的历史记录：名字 发送时间 发送内容
打印完开始发送消息（发给服务端，服务端进行转发）

用户1...给用户2...发送消息中

判断用户2是否在线 --> 不在线（数据存入历史消息表和离线消息表中）在线（仅存入历史消息表中，然后给用户2发送【收到好友1的一条消息：msg】）

​					**查看聊天记录**

**（D）群聊天**

​					**进入群聊天**

​					**查看群聊天记录**

**（E）消息管理**

​					**未读消息**（就是离线后收到的消息，结束之后立刻将消息列表清空）

​					**查看好友请求**

​					**查看群通知**

​							**我的群验证 **--- 您已加入群聊.../ 您加入群聊...的申请被驳回

​						**我管理的群通知**

​									群申请 --- 选择同意/不同意

​									设置管理员通知 ---- 您已被群主设置为群...的管理员 / 您已被群主解除群...管理员身份

​									//群友退出 --- XXX退出了群聊

**（F）退出登陆**（将接收消息数据表改为未读消息表，状态改为离线）
