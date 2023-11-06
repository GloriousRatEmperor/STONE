package Multiplayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class Client extends ChannelInboundHandlerAdapter {

    ChannelHandlerContext ctx;
    BlockingQueue<ServerData> responses;
    private final Charset charset = StandardCharsets.UTF_8;
    ObjectMapper mapper = new ObjectMapper();

    public Client(BlockingQueue<ServerData> responses) {
        this.responses=responses;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        this.ctx=ctx;

    }
    public void toServer(ClientData msg) throws JsonProcessingException {
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        responses.add((ServerData) msg);

    }
}