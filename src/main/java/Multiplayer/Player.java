package Multiplayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import static Multiplayer.State.unregistered;

public class Player {
    private ChannelHandlerContext ctx;
    public ChannelId id;
    public State state=unregistered;
    public String name;
    public Player(ChannelHandlerContext ctx){
        this.ctx = ctx;
        this.id = ctx.channel().id();
        this.name="player"+id;
    }
    public int allied;
    public ChannelHandlerContext getCtx(){
        return ctx;
    }
}
