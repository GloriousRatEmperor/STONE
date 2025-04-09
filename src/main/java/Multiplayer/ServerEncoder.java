package Multiplayer;

import Multiplayer.DataPacket.ServerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.gamestuff.Deserializer.ServerDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ServerEncoder
        extends MessageToByteEncoder<ServerData> {
    private final Charset charset = StandardCharsets.UTF_8;
    private final Gson encoder=new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(ServerData.class, new ServerDataSerializer()).create();

    @Override
    protected void encode(ChannelHandlerContext ctx, ServerData msg, ByteBuf out) throws Exception {
            String msgString= encoder.toJson(msg, ServerData.class);
            out.writeInt(msgString.length());
            out.writeCharSequence(msgString, charset);
    }
}