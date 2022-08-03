package messages.toserver;

import messages.UserMessage;

/**
 * 群主添加//删除群管理员消息类型
 * 0添加 1删除
 * */
public class GroupManagerMsg extends UserMessage {
    private int num;
    private int groupID;
    private String me;
    private String name;
    private String time;

    public GroupManagerMsg(int groupID,String me, String name, int num, String time) {
        this.groupID = groupID;
        this.me = me;
        this.name = name;
        this.num = num;
        this.time = time;
    }

    public String getMe() {
        return this.me;
    }

    public String getName() {
        return this.name;
    }

    public int getGroupID() {
        return this.groupID;
    }

    public String getTime() {
        return this.time;
    }

    public int getNum() {
        return this.num;
    }

    @Override
    public String toString() {
        return "------ >*您有一条管理员身份变动通知*< ------";
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}