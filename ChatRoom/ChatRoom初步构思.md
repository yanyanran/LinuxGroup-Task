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



关于帐号管理的登陆、注册、注销 7.19完成。

```
             // create.Statement() 创建语句对象，负责对表执行查询
            Statement stmt = con.createStatement();
             // 执行查询student is the table name & address is column
             // Step 4: 创建声明
             String query = "ALTER TABLE student Drop address";
             // Step 5: 执行查询
             // executeUpdate() 返回行数受语句执行影响
            int result = stmt.executeUpdate(query);
             // Step 6: 处理结果
             // 如果结果大于 0，则表示已添加值
            if (result > 0)
                System.out.println(
                    "表中的一列被删除.");
            else
                System.out.println(" 删除失败");
             // Step 7: 关闭连接
            con.close();
```