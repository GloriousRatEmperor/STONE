package Multiplayer;

import Multiplayer.DataPacket.ClientData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.gamestuff.Deserializer.ClientDataSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientEncoder
        extends MessageToByteEncoder<ClientData> {
    private final Gson encoder=new GsonBuilder()
            .registerTypeAdapter(ClientData.class, new ClientDataSerializer())
            .enableComplexMapKeySerialization().create();

    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    protected void encode(ChannelHandlerContext ctx,
                          ClientData msg, ByteBuf out) throws Exception {
        String msgString= encoder.toJson(msg,ClientData.class);
        out.writeInt(msgString.length());
        out.writeCharSequence(msgString, charset);
    }
}