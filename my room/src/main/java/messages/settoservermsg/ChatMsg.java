package messages.settoservermsg;

import messages.UserMessage;

// 群聊和单聊的聊天信息类型（common）
public class ChatMsg extends UserMessage {
    private int msgType;   // 0---文本  1---文件
    private String from;   // 发送方
    private String to;     // 接收方
    private String msg;    // 消息体

    public ChatMsg(String from, String to, int msgType, String msg) {
        this.from = from;
        this.to = to;
        this.msgType = msgType;
        this.msg = msg;
    }

    public int getMsgType() {
        return this.msgType;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public String Msg() {
        return this.msg;
    }

    public String toString() {
        return "from = " + from + " to = " + to + " msgType = " + msgType + " msg = " + msg;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}