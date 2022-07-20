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



##### 帐号管理

登陆、注册、注销 （7.20完成）



> ##### *数据库表的构建：*
>
> *应该根据系统架构中的组件划分，针对每个组件所处理的业务进行组件单元的数据库设计；不同组件间所对应的数据库表之间的关联应尽可能减少，如果不同组件间的表需要外键关联也尽量不要创建外键关联，而只是记录关联表的一个主键，确保组件对应的表之间的独立性，为系统或表结构的重构提供可能性。*

**群聊列表和好友列表避免一个用户/一个群创建一个表** 

用户数据表 （注册后放到这里面）--> id、username、password 

好友列表 （互为好友关系时两人放到此表的同一行里）-->  user1（用户1），user2（用户2），num（属性：0普通好友，1为消息屏蔽好友）

聊天记录的存储用一个表来记录OnLineMessage（输入好友名发起聊天） --> time、me、user、infor

未读消息列表 (对方离线状态时，没接收的信息存到另一个表里OffLineMessage) --> time、me、user、infor

群聊列表 -->  groupname、username、type（属性：管理员为2，群众为1）

