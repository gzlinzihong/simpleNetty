package cn.ilanky.simplenetty.handler;

import java.util.LinkedList;

public abstract class ChannelInitializer {

    LinkedList<ChannelHandler> handlers;

    public abstract void init(ChannelHandlerContext context);
}
