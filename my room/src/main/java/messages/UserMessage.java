package messages;

import messages.settoservermsg.LogoutMsg;
import messages.settoservermsg.RegisterMsg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class UserMessage implements Serializable {
    private int messageType;
    private String username;
    public abstract  int getMessageType();

    // Login main page
    // register
    private static final int registermsg = 0;
    private static final int registermsg1 = 1;

    // login
    private static final int loginmsg = 2;
    private static final int loginmsg1 = 3;

    // logout
    public static final int logoutmsg = 4;
    private static final int logoutmsg1 = 5;

    // quit
    private static final int quitmsg = 6;
    private static final int quitmsg1 = 7;

    private static final Map<Integer,Class<?>> messageClass = new HashMap<>();

    public static Class<?> getMessageClass(int messageType) {
        return messageClass.get(messageType);
    }

    public String getUsername() {
        return username;
    }

    static{
        messageClass.put(0, RegisterMsg.class);
        messageClass.put(1, LogoutMsg.class);
//        messageClasses.put(2, message.Loginmsg.class);
//        messageClasses.put(3, message.Loginmsg1.class);
//        messageClasses.put(4, message.Logoutmsg.class);
//        messageClasses.put(5, message.Logoumsg1.class);
//        messageClasses.put(6, message.Quit.class);
//        messageClasses.put(7, message.Quit1.class);
//        messageClasses.put(8, message.Enrollmsg.class);
    }

    // 展示未读消息数量
    public static int getUnreadMessage(){
        int unread = UserMessage.getUnreadMessage();
        System.out.printf("-------您有%d条未读消息-------\n",unread);
        return 1;
    }
}