package messages;

import messages.settoservermsg.LoginMsg;
import messages.settoservermsg.LogoutMsg;
import messages.settoservermsg.RegisterMsg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class UserMessage implements Serializable {
    // 实现serializable接口后必须指定序列化版本号，用于序列化与反序列化时验证对象
    private static final long serialVersionUID = -7815896088464512553L;

    protected static String username;
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