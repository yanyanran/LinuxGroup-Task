package messages.toclient;

import messages.UserMessage;

import java.util.ArrayList;
import java.util.Map;

// server --> client
public class ServerToClientMsg extends UserMessage {
        private boolean success;
        private String result;
        private int ServerToClientMsg;
        private int num;
        int MessageType = ServerToClientMsg;
        ArrayList<String> list;

        public ServerToClientMsg(boolean success,String reason) {
            this.success = success;
            this.result = reason;
        }

        public ServerToClientMsg(boolean success, int num) {
            this.num = num;
            this.success = success;
        }

        public ServerToClientMsg(boolean success,String reason,String username) {
            this.success = success;
            this.result = reason;
            this.username = username;
        }

        public ServerToClientMsg(boolean success, Map<Integer, String> msgMap) {
            this.success = success;
            this.msgMap = msgMap;
         }

//    public ServerToClientMsg(boolean success, Map<Integer, String> msgMap, String from) {
//        this.success = success;
//        this.msgMap = msgMap;
//        this.from = from;
//    }

        public ServerToClientMsg(boolean success, ArrayList<String> list) {
            this.success = success;
            this.list = list;
        }

        public void setMessageType(int msgType) {
                this.MessageType = msgType;
        }

        public boolean getSuccess() {
            return this.success;
        }

        public String getResult() {
            return this.result;
        }

        public int getNum() {
            return this.num;
        }

        public static Map<Integer,String> getMsgMap() {
            return msgMap;
        }

        public ArrayList<String> getList() {
            return this.list;
        }

        public static String getMe() {
            return username;
        }

//        public static String getFrom() {
//            return from;
//        }

        public int getMessageType() {
            return this.MessageType;
        }

        public String toString() {
            return "success = " + success + ", reason = " + result + "me = " + username;
        }
}