package sc;

import java.io.Serializable;

/**
 * @program:
 * @description: 消息对象类
 * @author:
 **/
public class UserMessage implements Serializable {
    // 实现serializable接口后必须指定序列化版本号，用于序列化与反序列化时验证对象
    private static final long serialVersionUID = -7815896088464512553L;

    private String msgType;  //消息类型：  File|文件；String|普通字符串文本
    private String msgBody;  //消息内容：  文件路径/普通文本内容

    public UserMessage(String msgType, String msgBody) {
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

    @Override
    public String toString() {
        return "UserMessage{" +
                "msgType='" + msgType + '\'' +
                ", msgBody='" + msgBody + '\'' +
                '}';
    }
}

