package Multiplayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.*;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class Client extends ChannelInboundHandlerAdapter {

    private final InetSocketAddress addr2;
    private final InetSocketAddress addr1;
    private final SimpleChannelPool pool;
    ChannelHandlerContext ctx;
    BlockingQueue<ServerData> responses;
    private final Charset charset = StandardCharsets.UTF_8;
    ObjectMapper mapper = new ObjectMapper();

    public Client(BlockingQueue<ServerData> responses, ChannelPoolMap<InetSocketAddress,SimpleChannelPool> poolMap, InetSocketAddress addr1,InetSocketAddress addr2) {
        this.responses=responses;
        this.addr2=addr2;
        this.addr1=addr1;

        this.pool = poolMap.get(addr1);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {
        this.ctx=ctx;


    }
    public void toServer(ClientData msg) throws JsonProcessingException {
        Future<Channel> f = pool.acquire();
        f.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> f) {
                if (f.isSuccess()) {
                    System.out.println("Connected to server");
                    Channel ch = f.getNow();
                    ch.writeAndFlush(msg);
                    pool.release(ch);
                }
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        responses.add((ServerData) msg);

    }
}