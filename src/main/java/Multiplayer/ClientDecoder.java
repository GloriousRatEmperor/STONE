package Multiplayer;

import Multiplayer.DataPacket.ServerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.gamestuff.Deserializer.ServerDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ClientDecoder
        extends ReplayingDecoder<ServerData> {
    private final Charset charset = StandardCharsets.UTF_8;
    private final Gson deserializer=new GsonBuilder()
            .enableComplexMapKeySerialization()
            .registerTypeAdapter(ServerData.class, new ServerDataSerializer()).create();

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf in, List<Object> out) throws Exception {

            ServerData data;
            int size = in.readInt();
            String thing = in.readCharSequence(size, charset).toString();
            data= deserializer.fromJson(thing,ServerData.class);
            out.add(data);
    }

}