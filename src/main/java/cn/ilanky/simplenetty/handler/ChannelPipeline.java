package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.ChannelContext;
import cn.ilanky.simplenetty.handler.ChannelHandler;

public interface ChannelPipeline {

    Channel channel();

    ChannelHandlerContext context();

    ChannelHandler head();

    ChannelHandler tail();

    void addBefore(ChannelHandler reference,ChannelHandler handler);

    void addAfter(ChannelHandler reference,ChannelHandler handler);

    void addLast(ChannelHandler handler);

    void addFirst(ChannelHandler handler);
}
