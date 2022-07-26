package messages.settoservermsg;

import messages.UserMessage;

import java.io.Serializable;

// 群聊和单聊的消息对象类（common）
public class ChatMsg extends UserMessage implements Serializable {
    // 实现serializable接口后必须指定序列化版本号，用于序列化与反序列化时验证对象
    private static final long serialVersionUID = -7815896088464512553L;

    private String from;   // 发送方
    private String to;     // 接收方
    private String msgType;    // 消息类型:File---文件 String---普通字符串文本
    private String msgBody;    // 消息内容:文件路径 普通文本内容
    protected static String username;
    private int sequenceId;

    public ChatMsg(String msgType, String msgBody) {
        this.msgType = msgType;
        this.msgBody = msgBody;
    }

    public ChatMsg(String from, String to, String msgType, String msgBody) {
        this.from = from;
        this.to = to;
        this.msgType = msgType;
        this.msgBody = msgBody;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "from = " + from + " to = " + to + " msgType = " + msgType + " msgBody = " + msgBody;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}