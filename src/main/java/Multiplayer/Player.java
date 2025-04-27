package Multiplayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import static Multiplayer.State.inactive;

public class Player {
    private ChannelHandlerContext ctx;
    public ChannelId id;
    public State state=inactive;
    public String name;
    static int botID=0;
    public Game game=null;
    public boolean isBot=false;
    public Player(ChannelHandlerContext ctx){
        this.ctx = ctx;
        if(ctx!=null){
            this.id = ctx.channel().id();
        }else{
            id=null;
        }
        this.name="player"+id;
    }
    public int allied;
    public ChannelHandlerContext getCtx(){
        return ctx;
    }

}
