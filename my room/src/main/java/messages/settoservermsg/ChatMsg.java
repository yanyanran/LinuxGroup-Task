package messages.settoservermsg;

import messages.UserMessage;

import java.io.Serializable;

/**
 * 群聊和单聊的消息对象类（common）
 *  客户端聊天handler new时记得传入from和to
 * */
public class ChatMsg extends UserMessage implements Serializable {
    // 实现serializable接口后必须指定序列化版本号，用于序列化与反序列化时验证对象
    private static final long serialVersionUID = -7815896088464512553L;

    private String from;   // 发送方
    private String to;     // 接收方
    private String msgType;    // 消息类型:File---文件 String---普通字符串文本
    private String msgBody;    // 消息内容:文件路径 普通文本内容
    private String time;       // 发送时间

    public ChatMsg(String msgType, String msgBody) {
        this.msgType = msgType;
        this.msgBody = msgBody;
    }

    public ChatMsg(String from, String to, String msgType, String msgBody,String time) {
        this.from = from;
        this.to = to;
        this.msgType = msgType;
        this.msgBody = msgBody;
        this.time = time;
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

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "from = " + from + " to = " + to + " msgType = " + msgType + " msgBody = " + msgBody + "send time = " + time;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}