package messages;

import messages.settoservermsg.LoginMsg;
import messages.settoservermsg.LogoutMsg;
import messages.settoservermsg.RegisterMsg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class UserMessage implements Serializable {
    private int messageType;
    private String username;
    private int sequenceId;
    public abstract  int getMessageType();

    // Login main page
    // register
    private static final int registermsgCtoS = 0;  // client --> server
    private static final int registermsg1StoC = 1; // server --> client

    // login
    private static final int loginmsgCtoS = 2;
    private static final int loginmsg1StoC = 3;

    // logout
    public static final int logoutmsgCtoS = 4;
    private static final int logoutmsg1StoC = 5;

    // quit
    private static final int quitmsgCtoS = 6;
    private static final int quitmsg1StoC = 7;

    private static final Map<Integer,Class<?>> messageClass = new HashMap<>();

    public String getUsername() {
        return username;
    }
    public int getSequenceId(){
        return sequenceId;
    }
    // 展示未读消息数量
    public static int getUnreadMessage(){
        int unread = UserMessage.getUnreadMessage();
        System.out.printf("-------您有%d条未读消息-------\n",unread);
        return 1;
    }
}