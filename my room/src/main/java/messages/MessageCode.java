package messages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import messages.UserMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageCode extends ByteToMessageCodec<UserMessage> {
    private int messageType;
    private int sequenceId;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UserMessage message, ByteBuf byteBuf) throws Exception {
        // 魔数
        byteBuf.writeBytes(new byte[]{9,2,6,4});
        // 序列化算法方式 0-->jdk ，1-->json
        byteBuf.writeByte(0);
        // 指令类型
        // 请求序号
        byteBuf.writeInt(message.getSequenceId());

        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes=bos.toByteArray();
        // 消息长度
        byteBuf.writeInt(bytes.length);
        //System.out.println(bytes.length);
        // 对齐
        byteBuf.writeShort(0xffff);
        // 获取内容的字节数组
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum=byteBuf.readInt();
        byte serializerType=byteBuf.readByte();
        int sequenceId=byteBuf.readInt();
        int length=byteBuf.readInt();
        // System.out.println(length);
        byteBuf.readByte();
        byteBuf.readByte();
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes,0,length);

        if(serializerType==0){
            // jdk序列化
            ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
            UserMessage message=(UserMessage) ois.readObject();
            list.add(message);
        }

    }
    private static final Map<Integer,Class<?>> messageClasses = new HashMap<>();

    public static Class<?> getMessageClass(int messageType){
        return messageClasses.get(messageType);
    }
    public int getSequenceId(){
        return sequenceId;
    }
}