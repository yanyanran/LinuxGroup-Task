package messages.settoclientmsg;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * map --> 绑定channel和对应帐号（聊天用）
 * */
public final class ChatHandlerMap {
    private static Map<String, Channel> channelMap = new HashMap<>();
    private static Map<Channel,String> userMap = new HashMap<>();

    public static void add(String user, Channel chatHandler){
        channelMap.put(user,chatHandler);
        userMap.put(chatHandler,user);
    }

    public static String getUser(Channel channel){
        if(userMap.containsKey(channel)){
            return userMap.get(channel);
        }else {
            return null;
        }
    }

    public static Channel getChannel(String user){
        if(channelMap.containsKey(user)){
            return channelMap.get(user);
        }else {
            return null;
        }
    }
}