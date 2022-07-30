package messages.toserver;

import messages.UserMessage;

/**
 * 好友申请消息类型
 * */
public class FriendApplyMsg extends UserMessage {
    private String fromUser;
    private String toUser;
    private String time;

    public FriendApplyMsg(String fromUser, String toUser, String time) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.time = time;
    }

    public String getFromUser() {
        return this.fromUser;
    }

    public String getToUser() {
        return this.toUser;
    }

    public String getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return time + " " + fromUser + "申请添加您为好友";
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}