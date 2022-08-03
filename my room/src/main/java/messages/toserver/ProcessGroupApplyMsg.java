package messages.toserver;

import messages.UserMessage;

/**
 * 管理员/群主处理入群申请消息类型
 * （0通过申请、1拒绝申请）
 * */
public class ProcessGroupApplyMsg extends UserMessage {
    private int msgId;
    private String GroupID;
    private int num;    // 通过num区分对方是同意了还是拒绝了
    String time;

    public ProcessGroupApplyMsg(int msgId, String GroupID, int num, String time) {
        this.msgId = msgId;
        this.GroupID = GroupID;
        this.num = num;
        this.time = time;
    }

    public int getMsgId() {
        return this.msgId;
    }

    public String getGroupID() {
        // msg
        return this.GroupID;
    }

    public int getNum() {
        return this.num;
    }

    public String getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        // 申请通过和申请拒绝区分（处理完通知给申请方
        return "----- *>您对群【" + GroupID + "】发出的入群申请已被处理<* -----";
    }
    @Override
    public int getMessageType() {
        return 0;
    }
}