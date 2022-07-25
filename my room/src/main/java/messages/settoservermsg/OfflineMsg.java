package messages.settoservermsg;

import messages.UserMessage;

public class OfflineMsg extends UserMessage {
    private String username;

    public OfflineMsg(String username) {
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public String toString() {
        return "username = " + username;
    }

    @Override
    public int getMessageType() {
        return 0;
    }
}