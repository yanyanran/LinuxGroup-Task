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
    public abstract int getMessageType();

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