package Multiplayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.*;
import jade.Time;

import java.util.ArrayList;

@ChannelHandler.Sharable
public class Server extends ChannelInboundHandlerAdapter {
    private float playerCount=0;
    public Time time=new Time();
    private ArrayList<ChannelHandlerContext> ctxlist=new ArrayList<>();
    private float delay=2;
    private int maxPlayerCount=2;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server connected to sumone");
        ctxlist.add(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {



        ClientData ClientData = (ClientData) msg;
        switch (ClientData.getName()) {
            case "start" -> {
                if (playerCount < maxPlayerCount) {
                    playerCount += 1;
                    if (playerCount == maxPlayerCount) {

                        ServerData ServerData = new ServerData();
                        long curTime=System.currentTimeMillis();
                        ServerData.setStart(curTime);
                        time.setBeginTime(curTime);
                        ServerData.setTime(time.getTime());

                        ServerData.setName(ClientData.getName());

                        toClients(ServerData);
                    }
                }
            }
            case "Move" -> {

                ServerData ServerData = new ServerData();
                ServerData.setTime(time.getTime() + delay);
                ServerData.setIntValue(ClientData.getIntValue());
                ServerData.setGameObjects(ClientData.getGameObjects());
                ServerData.setName(ClientData.getName());
                ServerData.setPos(ClientData.getPos());
                toClients(ServerData);
            }
        }

    }
    public void toClients(ServerData msg) throws JsonProcessingException {
        for (ChannelHandlerContext ctx : ctxlist) {
            ctx.writeAndFlush(msg);
        }
    }
    public void toclient(ChannelHandlerContext ctx,ServerData msg) throws JsonProcessingException {
        ctx.writeAndFlush(msg);
    }
}