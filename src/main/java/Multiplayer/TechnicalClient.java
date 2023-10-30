package Multiplayer;


import com.fasterxml.jackson.core.JsonProcessingException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.junit.runner.Request;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class TechnicalClient implements Runnable{
    String host;
    BlockingQueue<ClientData> requests;
    BlockingQueue<ServerData> responses;
    InetSocketAddress addr1 = new InetSocketAddress("10.0.0.10", 8888);
    InetSocketAddress addr2 = new InetSocketAddress("10.0.0.11", 8888);
    public TechnicalClient(String host, BlockingQueue<ClientData> requests, BlockingQueue<ServerData> responses){
        this.host=host;
        this.requests=requests;
        this.responses=responses;

    }


    @Override
    public void run() {
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();


        try {
            Bootstrap b = new Bootstrap();

            ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
                @Override
                protected SimpleChannelPool newPool(InetSocketAddress key) {
                    return new SimpleChannelPool(b.remoteAddress(key), new ChannelPoolHandler());
                }
            };





            b.group(workerGroup);
            b.channel(NioSocketChannel.class);

            b.option(ChannelOption.SO_KEEPALIVE, true);
            Client client= new Client(responses,poolMap,addr1,addr2);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    ch.pipeline().addLast(new ClientEncoder(),

                            new ClientDecoder(), client);

                }
            });

            ChannelFuture f = b.connect(host, port).sync();


            while (true) {
                ClientData request;
                    while (!Objects.equals((request = requests.take()).getName(), "exit")) {
                        client.toServer(request);
                    }

            }

            //f.channel().closeFuture().sync();
        } catch (InterruptedException | JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }


    }
}