package Multiplayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServerDecoder extends ReplayingDecoder<ClientData> {

        private final Charset charset = StandardCharsets.UTF_8;
        ObjectMapper mapper = new ObjectMapper();

        @Override
        protected void decode(ChannelHandlerContext ctx,
                              ByteBuf in, List<Object> out) throws Exception {

                ClientData data;
                int size=in.readInt();
                String thing=in.readCharSequence(size,charset).toString();
                data=mapper.readValue(thing, ClientData.class);
                out.add(data);

        }
    }