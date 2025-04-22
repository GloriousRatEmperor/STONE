package Multiplayer;

import Multiplayer.DataPacket.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.ArrayList;

import static Multiplayer.State.inactive;

@ChannelHandler.Sharable
public class Server extends ChannelInboundHandlerAdapter {
    private float playerCount=0;
    private ArrayList<Game> games=new ArrayList<>();
    private float delay=0.2f;
    private int maxPlayerCount;
    private int playerCountAdd;
    public int allied=0;


    public Server(int maxPlayers) {
        this.maxPlayerCount=maxPlayers;
        this.playerCountAdd=maxPlayers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server connected to sumone");
        Player p=new Player (ctx);
        addPlayer(p);
        p.state=inactive;
        allied += 1;
        p.allied = allied;
        if (allied == maxPlayerCount) {
            allied = 0;
        }
    }
    public void addPlayer(Player p){
        boolean added=false;
        for(Game game:games){
            if(!game.isFull()){
                added=true;
                game.addPlayer(p);
                break;
            }
        }
        if(!added){
            Game newGame=new Game(this,maxPlayerCount);
            newGame.addPlayer(p);
            games.add(newGame);

        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ClientData data = (ClientData) msg;
        Player player = getPlayer(ctx.channel().id());
        Game game=player.game;
        Class<? extends ClientData> dataClass=data.getClass();


        if (dataClass.equals(Cstop.class)) {
            Cstop clientData = (Cstop) data;
            game.setNotReady(player);
        } else if (dataClass.equals(Cstart.class)) {
            Cstart clientData = (Cstart) data;
            game.setReady(player,clientData);
        } else if (dataClass.equals(Cmove.class)) {
            Cmove clientData = (Cmove) data;
            Smove serverData = new Smove();

            serverData.setTime(game.getTime() + delay);
            serverData.setTargetID(clientData.getTargetID());
            serverData.setGameObjects(clientData.getGameObjects());
            serverData.setPos(clientData.getPos());
            serverData.setQmove(clientData.isQmove());
            serverData.setShiftCommand(clientData.isShiftCommand());
            game.toClients(serverData);
        } else if (dataClass.equals(Ccast.class)) {
            Ccast clientData = (Ccast) data;
            Scast serverData = new Scast();
            serverData.setAbilityID(clientData.getAbilityID());
            serverData.setTarget(clientData.getTarget());
            serverData.setShiftCommand(clientData.isShiftCommand());
            serverData.setTime(game.getTime() + delay);
            serverData.setGameObjects(clientData.getGameObjects());
            serverData.setPos(clientData.getPos());
            game.toClients(serverData);
        } else if (dataClass.equals(Cmessage.class)) {
            Cmessage clientData = (Cmessage) data;
            if(clientData.getMessage().startsWith("rename ")){
                String newname=clientData.getMessage().substring(7);
                rename(player,newname);
            }else{
                Smessage serverData = new Smessage();
                serverData.setTime(-1);
                serverData.setMessage(clientData.getMessage());
                serverData.setColor(clientData.getColor());
                serverData.setSenderName(player.name);

                game.toClients(serverData);
            }


        }
    }
    private void rename(Player player,String newname){
        for(Player p: player.game.getPlayers()){
            if(p.name.equals(newname)){
                sendError(p,"NAME TAKEN");
            }
            player.name=newname;
        }
    }
    public void sendError(Player player,String message) {
        Smessage serverData = new Smessage();
        serverData.setTime(-1);
        serverData.setMessage(message);
        serverData.setColor(util.Img.color(255,0,0,255));
        serverData.setSenderName("Server");
        toClient(player, serverData);
    }

    private Player getPlayer(ChannelId id){
        for (Game game : games){
            Player p=game.getPlayer(id);
            if(p!=null){
                return p;
            }
        }
        System.out.println("ID not found for player you dumbfuck (jk you are amazing but I am making sure you know I made this warning and not the lib)");
        return null;
    }

    public void toAllClients(ServerData msg) throws JsonProcessingException {
        for (Game game : games) {
            game.toClients(msg);
        }
    }
    public void toClient(Player player, ServerData msg) {
        player.getCtx().writeAndFlush(msg);
    }
}