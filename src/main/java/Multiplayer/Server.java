package Multiplayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.*;
import jade.Time;

import java.util.ArrayList;

import static Multiplayer.State.*;

@ChannelHandler.Sharable
public class Server extends ChannelInboundHandlerAdapter {
    private float playerCount=0;
    public Time time=new Time();
    private ArrayList<Player> players=new ArrayList<>();
    private float delay=1;
    private int maxPlayerCount;
    private int playerCountAdd;

    public Server(int maxPlayers) {
        this.maxPlayerCount=maxPlayers;
        this.playerCountAdd=maxPlayers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server connected to sumone");
        players.add(new Player (ctx));
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {



        ClientData ClientData = (ClientData) msg;
        Player player = getPlayer(ctx.channel().id());
        switch (ClientData.getName()) {
            case "stop" -> {
                if (player.state!=inactive){
                    if(player.state==waiting){
                        playerCount-=1;
                    }else {
                        maxPlayerCount-=1;
                        playerCount-=1;
                    }
                    player.state=inactive;

                }else{
                    System.out.println("but he wasn't playing nor ready wtf");
                }
            }
            case "start" -> {
                if (player.state==inactive){
                        player.state=waiting;
                        playerCount += 1;
                    System.out.println(playerCount);
                    System.out.println(maxPlayerCount);
                        if (playerCount == maxPlayerCount) {

                            maxPlayerCount+=playerCountAdd;
                            ServerData ServerData = new ServerData();
                            long curTime=System.currentTimeMillis();
                            ServerData.setStart(curTime);
                            time.setBeginTime(curTime);
                            ServerData.setTime(time.getTime());
                            ServerData.setName(ClientData.getName());

                            for (Player p:players) {
                                int c=0;
                                if(p.state==waiting) {
                                    c+=1;
                                    p.state = playing;
                                    ServerData.setIntValue(c);
                                    toclient(p, ServerData);
                                }
                            }


                    }
                }else{
                    System.out.println("already playing wtf");
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

            case "Speed" -> {

                ServerData ServerData = new ServerData();
                ServerData.setTime(time.getTime() + delay);
                ServerData.setIntValue(ClientData.getIntValue());
                ServerData.setGameObjects(ClientData.getGameObjects());
                ServerData.setName(ClientData.getName());
                toClients(ServerData);
            }

            case "Heal" -> {

                ServerData ServerData = new ServerData();
                ServerData.setTime(time.getTime() + delay);
                ServerData.setIntValue(ClientData.getIntValue());
                ServerData.setGameObjects(ClientData.getGameObjects());
                ServerData.setName(ClientData.getName());
                toClients(ServerData);
            }
        }
    }
    private Player getPlayer(ChannelId id){
        for (Player p : players){
            if (p.id==id){
                return p;
            }
        }
        System.out.println("ID not found for player you dumbfuck (jk you are amazing but I am making sure you know I made this warning and not the lib)");
        return null;
    }

    public void toClients(ServerData msg) throws JsonProcessingException {
        for (Player player : players) {
            player.getCtx().writeAndFlush(msg);
        }
    }
    public void toclient(Player player,ServerData msg) throws JsonProcessingException {
        player.getCtx().writeAndFlush(msg);
    }
}