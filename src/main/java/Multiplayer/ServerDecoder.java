package Multiplayer;

import Multiplayer.DataPacket.ClientData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.gamestuff.Deserializer.ClientDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServerDecoder extends ReplayingDecoder<ClientData> {

        private final Charset charset = StandardCharsets.UTF_8;
        private final Gson deserializer=new GsonBuilder()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(ClientData.class, new ClientDataSerializer()).create();

        @Override
        protected void decode(ChannelHandlerContext ctx,
                              ByteBuf in, List<Object> out) throws Exception {

                ClientData data;
                int size=in.readInt();
                String thing=in.readCharSequence(size,charset).toString();
                data= deserializer.fromJson(thing,ClientData.class);
                out.add(data);

        }
    }