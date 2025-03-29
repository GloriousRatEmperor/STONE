package Multiplayer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jade.Rng;
import jade.Time;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static Multiplayer.State.*;

@ChannelHandler.Sharable
public class Server extends ChannelInboundHandlerAdapter {
    private float playerCount=0;
    public Time time=new Time();
    private ArrayList<Player> players=new ArrayList<>();
    private float delay=1;
    private int maxPlayerCount;
    private int playerCountAdd;


    private int space=2048;
    private int count=1;
    private int colorMax=245;
    //allows for more precision due to there being intiger constraints
    private int colorEmbiggen=4;
    private int colorMin=10;
    private float dechaos=1.3f;
    private int randchange=250*4;
    private int boxCount=8;
    private int maxrand=200;

    private int colorLotto=100;
    public int allied=0;
    private int pow=3;
    private int division=20;
    private int size=count*space;
    private Vector3i last= new Vector3i(Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen),
            Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen),
            Rng.randint(colorMin * colorEmbiggen, colorMax * colorEmbiggen));
    private int colorRand=Rng.randint(-maxrand,maxrand);


    public Server(int maxPlayers) {
        this.maxPlayerCount=maxPlayers;
        this.playerCountAdd=maxPlayers;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("server connected to sumone");
        Player p=new Player (ctx);
        players.add(p);
        allied+=1;
        p.allied=allied;
        if(allied==maxPlayerCount){
            allied=0;
        }
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {



        ClientData clientData = (ClientData) msg;
        Player player = getPlayer(ctx.channel().id());
        switch (clientData.getName()) {
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
                        if (playerCount == maxPlayerCount) {
                            maxPlayerCount+=playerCountAdd;

                            HashMap<Vector2i, Vector3i> map= new HashMap<>();


//                            for (int i=0; i<count; i++){
//                                last=Math.max( Math.min( last+colorRand,colorMax*colorEmbiggen),colorMin*colorEmbiggen);
//                                colorRand+=Math.min(Math.max((Rng.randint(-randchange,randchange)/4+Rng.wild(-colorLotto,colorLotto,4,division))/4,-maxrand-colorRand),maxrand-colorRand);
//                                colorRand/=dechaos;
//                                map.put(new Vector2i(-size/2+i*space,-size/2),new Vector2i(last/colorEmbiggen,colorRand));
//
//                            }
//                            last=map.get(new Vector2i(-size/2,-size/2)).x*colorEmbiggen;
//                            for (int i=1; i<count; i++){
//                                last=Math.max( Math.min( last+colorRand,colorMax*colorEmbiggen),colorMin*colorEmbiggen);
//                                colorRand+=Math.min(Math.max((Rng.randint(-randchange,randchange)/4+Rng.wild(-colorLotto,colorLotto,4,division))/4,-maxrand-colorRand),maxrand-colorRand);
//                                colorRand/=dechaos;
//                                map.put(new Vector2i(-size/2,-size/2+i*space),new Vector2i(last/colorEmbiggen,colorRand));
//                            }
//                            for (int i=1; i<count; i++){
//                                for (int b=1; b<count; b++){
//                                    last=Math.max(Math.min((map.get(new Vector2i(-size/2+b*space-space, -size/2+i*space)).x
//                                            +map.get(new Vector2i(-size/2+b*space, -size/2+i*space-space)).x)*colorEmbiggen/2
//                                            +colorRand,colorMax*colorEmbiggen),colorMin*colorEmbiggen);
//                                    colorRand=(map.get(new Vector2i(-size/2+b*space, -size/2+i*space-space)).y+map.get(new Vector2i(-size/2+b*space-space, -size/2+i*space)).y)/2;
//
//                                    colorRand+=Math.min(Math.max(Rng.randint(-randchange,randchange)+Rng.wild(-colorLotto,colorLotto,4,division),-maxrand-colorRand),maxrand-colorRand);
//                                    colorRand/=dechaos;
//                                    map.put(new Vector2i(-size/2+b*space, -size/2+i*space),new Vector2i(last/colorEmbiggen,colorRand));
//                                }
//                            };
                            int boxsize= size/2;
                            colorRand= 100;
                            last.x=Rng.randint(colorMin,colorMax);
                            last.y=Rng.randint(colorMin,colorMax);
                            last.z=Rng.randint(colorMin,colorMax);
                            map.put(new Vector2i(-boxsize,-boxsize),new Vector3i(last.x,last.y,last.z));
                            last.x=Rng.randint(colorMin,colorMax);
                            last.y=Rng.randint(colorMin,colorMax);
                            last.z=Rng.randint(colorMin,colorMax);
                            map.put(new Vector2i(boxsize,-boxsize),new Vector3i(last.x,last.y,last.z));
                            last.x=Rng.randint(colorMin,colorMax);
                            last.y=Rng.randint(colorMin,colorMax);
                            last.z=Rng.randint(colorMin,colorMax);
                            map.put(new Vector2i(boxsize,boxsize),new Vector3i(last.x,last.y,last.z));
                            last.x=Rng.randint(colorMin,colorMax);
                            last.y=Rng.randint(colorMin,colorMax);
                            last.z=Rng.randint(colorMin,colorMax);
                            map.put(new Vector2i(-boxsize,boxsize),new Vector3i(last.x,last.y,last.z));
                            List<Vector2i> squarePositions= new ArrayList<>();
                            for (int i = 0; i <boxCount;i++) {

                                for (int a = -size/2+boxsize ; a <size/2f;a+=boxsize*2) {
                                    for (int c = -size/2+boxsize ; c <size/2;c+=boxsize*2) {
                                        squarePositions.add(new Vector2i( a,  c));
                                    }
                                }


                                for (Vector2i pos :squarePositions) {
                                    last.x=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).x+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).x+
                                            map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).x+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).x)/4;
                                    last.y=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).y+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).y+
                                            map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).y+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).y)/4;
                                    last.z=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).z+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).z+
                                            map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).z+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).z)/4;
                                    map.put(new Vector2i(pos.x, pos.y),randomC());
                                    last.x=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).x+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).x+
                                            map.get(new Vector2i(pos.x,pos.y)).x)/3;
                                    last.y=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).y+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).y+
                                            map.get(new Vector2i(pos.x,pos.y)).y)/3;
                                    last.z=(map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).z+map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).z+
                                            map.get(new Vector2i(pos.x,pos.y)).z)/3;
                                    map.put(new Vector2i(pos.x, pos.y-boxsize), randomC());
                                    last.x+=(-map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).x+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).x)/3;
                                    last.y+=(-map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).y+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).y)/3;
                                    last.z+=(-map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).z+map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).z)/3;
                                    map.put(new Vector2i(pos.x+boxsize, pos.y), randomC());
                                    last.x+=(-map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).x+map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).x)/3;
                                    last.y+=(-map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).y+map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).y)/3;
                                    last.z+=(-map.get(new Vector2i(pos.x+boxsize,pos.y-boxsize)).z+map.get(new Vector2i(pos.x-boxsize,pos.y+boxsize)).z)/3;
                                    map.put(new Vector2i(pos.x, pos.y+boxsize), randomC());
                                    last.x+=(-map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).x+map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).x)/3;
                                    last.y+=(-map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).y+map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).y)/3;
                                    last.z+=(-map.get(new Vector2i(pos.x+boxsize,pos.y+boxsize)).z+map.get(new Vector2i(pos.x-boxsize,pos.y-boxsize)).z)/3;
                                    map.put(new Vector2i(pos.x-boxsize, pos.y), randomC());




                                }
                                boxsize/=2;
                                randchange/=dechaos;
                                squarePositions.clear();
                            }

                            long curTime=System.currentTimeMillis();
                            time.setBeginTime(curTime);

                            for (Player p:players) {
                                if(p.state==waiting) {

                                    ServerData serverData = new ServerData();
                                    serverData.setIdCounter(clientData.getIntValue2());
                                    serverData.setstrValue(clientData.getString());
                                    serverData.setPlayerAmount(playerCountAdd);
                                    serverData.setStart(curTime);
                                    serverData.setTime(time.getTime());
                                    serverData.setName(clientData.getName());
                                    p.state = playing;
                                    serverData.setIntValue(p.allied);
                                    serverData.setIntValue2(boxsize*2);
                                    serverData.setIntValue3(size/(boxsize*2));
                                    List<Integer> map1=new ArrayList<>();
                                    List<Integer> map2=new ArrayList<>();
                                    List<Integer> map3=new ArrayList<>();
                                    for ( Vector2i key : map.keySet() ) {
                                        map1.add( key.x);
                                        map2.add( key.y);
                                        map3.add(map.get(key).x);
                                        map3.add(map.get(key).y);
                                        map3.add(map.get(key).z);
                                    }
                                    serverData.setMap(map1, map2, map3);
                                    toclient(p, serverData);

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
                ServerData.setIntValue(clientData.getIntValue());
                ServerData.setGameObjects(clientData.getGameObjects());
                ServerData.setName(clientData.getName());
                ServerData.setPos(clientData.getPos());
                ServerData.intValue2= clientData.getIntValue2();
                toClients(ServerData);
            }
            case "Cast" -> {

                ServerData ServerData = new ServerData();
                ServerData.setIntValue2(clientData.getIntValue2());
                ServerData.setIntValue3(clientData.getTarget());
                ServerData.setIntValue(clientData.getIntValue());
                ServerData.setTime(time.getTime() + delay);
                ServerData.setGameObjects(clientData.getGameObjects());
                ServerData.setName("Cast");

                ServerData.setPos(clientData.getPos());
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
    private Vector3i randomC() {
        //ugh writing the random was annoying
        int r=Math.max(Math.min( (last.x*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);
        int g=Math.max(Math.min( (last.y*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);
        int b=Math.max(Math.min( (last.z*colorEmbiggen+Rng.randint(-randchange,+randchange)+Rng.wild(-colorLotto,colorLotto,pow,division))/colorEmbiggen,colorMax),colorMin);


        return new Vector3i(r,
                g,
                b);
    }
}