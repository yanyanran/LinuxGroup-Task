package messages.toserver;

import messages.UserMessage;

/**
 * 群聊和单聊的消息对象类（common）
 *  客户端聊天handler new时记得传入from和to
 * */
public class ChatMsg extends UserMessage {
    // 实现serializable接口后必须指定序列化版本号，用于序列化与反序列化时验证对象
//    private static final long serialVersionUID = -7815896088464512553L;

    private String from;   // 发送方
    private String to;     // 接收方
    private int msgType;    // 消息类型:0--文本内容 1--文件
    private String msgBody;    // 消息内容:文件路径 普通文本内容
    private String time;       // 发送时间

    public ChatMsg(int msgType, String msgBody) {
        this.msgType = msgType;
        this.msgBody = msgBody;
    }

    public ChatMsg(String from, String to, int msgType, String msgBody,String time) {
        this.from = from;
        this.to = to;
        this.msgType = msgType;
        this.msgBody = msgBody;
        this.time = time;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
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
        return " 发送时间： " + time + " 发送方： " + from + " 接收方： " + to + " 消息类型（0:文本 1:文件）： = " + msgType + " 消息内容： " + msgBody;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}