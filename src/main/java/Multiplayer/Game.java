package Multiplayer;

import Multiplayer.DataPacket.ClientData;
import Multiplayer.DataPacket.Cstart;
import Multiplayer.DataPacket.ServerData;
import Multiplayer.DataPacket.Sstart;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelId;
import jade.Time;
import org.joml.Vector2i;
import org.joml.Vector3i;
import util.MapMaker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static Multiplayer.State.*;

public class Game {
    private Server server;
    private int capacity;
    public State state=State.inactive;
    Thread botThread;
    private Time time=new Time();
    private BlockingQueue<ClientData> BotToServer =new ArrayBlockingQueue<>(15);
    private BlockingQueue<ServerData> ServerToBot =new ArrayBlockingQueue<>(150);
    private ArrayList<Player> players=new ArrayList<>();

    public Game(Server ownserver,int capacity){
        server=ownserver;
        this.capacity=capacity;
        resetBot();
    }
    public void addPlayer(Player player){
        player.game=this;
        players.add(player);
    }
    public ArrayList<Player> getPlayers(){
        return players;
    }
    public void toClients(ServerData data) throws JsonProcessingException {
        for(Player player:players){
            server.toClient(player,data);
        }
        ServerToBot.add(data);
    }
    public Player getPlayer(ChannelId id) {
        for(Player p:players){
            if(p.id==id){
                return p;
            }
        }
        return null;
    }
    public boolean isFull(){
        return players.size()>=capacity;
    }

    public void setReady(Player player, Cstart data){
        if(player.state==inactive){
            player.state=waiting;
            if(isFull()){
                for (Player p:players){
                    if(p.state!=waiting){
                        return;
                    }
                }
                startGame(data);
            }
        }else{
            System.out.println("trying to ready but you are "+player.state+" wha? that's not meant to happen bro");
            server.sendError(player,"trying to ready but you are "+player.state+" wha? that's not meant to happen bro");
        }

    }
    public void setNotReady(Player player){
        if(player.state==playing){
            player.state=inactive;
            for(Player p:players){
                if(p.state==playing){
                    return;
                }
                resetBot();
            }
        } else if (player.state != inactive) {
            player.state = inactive;
        } else {
            System.out.println("but he wasn't playing nor ready wtf");
            server.sendError(player,"but you aren't playing nor ready wtf do you wanna stop???");
        }
    }
    private void resetBot(){
            if(botThread!=null){
                botThread.interrupt();
                while(!botThread.isInterrupted()){
                    System.out.println("waiting for bot to terminate");
                    continue;
                }
            }

            BotToServer.clear();
            ServerToBot.clear();
            botThread= new Thread(new BotThread(BotToServer,ServerToBot));
    }

    private void startGame(Cstart start){

        util.MapMaker maker=new MapMaker();
        HashMap<Vector2i, Vector3i> map=maker.generate();
        long curTime = System.currentTimeMillis();
        time.setBeginTime(curTime);

        for (Player p : getPlayers()) {
            if (p.state == waiting) {
                Sstart serverData = new Sstart();
                serverData.setIdCounter(start.getIdCounter());
                serverData.setLevelSave(start.getLevelSave());
                serverData.setPlayerAmount(capacity+1);
                serverData.setStartTime(curTime);
                serverData.setTime(time.getTime());
                p.state = playing;
                serverData.setPlayerAllied(p.allied);
                serverData.setMineralSpacing(maker.boxsize * 2);
                serverData.setMineralCount(maker.size / (maker.boxsize * 2));
                List<Integer> map1 = new ArrayList<>();
                List<Integer> map2 = new ArrayList<>();
                List<Integer> map3 = new ArrayList<>();
                for (Vector2i key : map.keySet()) {
                    map1.add(key.x);
                    map2.add(key.y);
                    map3.add(map.get(key).x);
                    map3.add(map.get(key).y);
                    map3.add(map.get(key).z);
                }
                serverData.setMap(map1, map2, map3);
                server.toClient(p, serverData);

            }else{
                System.out.println("uh oh game started but not all players are waiting!");
            }
        }
    }
    public float getTime(){
        return time.getTime();
    }
}
