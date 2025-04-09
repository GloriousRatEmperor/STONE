package Multiplayer;


import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.ServerData;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class TechnicalClient implements Runnable{
    String host;
    BlockingQueue<ClientData> requests;
    BlockingQueue<ServerData> responses;
    public TechnicalClient(String host, BlockingQueue<ClientData> requests, BlockingQueue<ServerData> responses){
        this.host=host;
        this.requests=requests;
        this.responses=responses;

    }

    @Override
    public void run() {

        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Client client= new Client(responses);

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
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