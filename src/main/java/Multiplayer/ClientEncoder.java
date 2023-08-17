package Multiplayer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientEncoder
        extends MessageToByteEncoder<ClientData> {

    private final Charset charset = StandardCharsets.UTF_8;
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ClientData msg, ByteBuf out) throws Exception {
        String msgString=mapper.writeValueAsString(msg);
        out.writeInt(msgString.length());
        out.writeCharSequence(msgString, charset);
    }
}