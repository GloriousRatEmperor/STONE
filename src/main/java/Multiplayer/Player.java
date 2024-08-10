package Multiplayer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import static Multiplayer.State.inactive;

public class Player {
    private ChannelHandlerContext ctx;
    public ChannelId id;
    public State state=inactive;
    public Player(ChannelHandlerContext ctx){
        this.ctx = ctx;
        this.id = ctx.channel().id();
    }
    public int allied;
    public ChannelHandlerContext getCtx(){
        return ctx;
    }
}
