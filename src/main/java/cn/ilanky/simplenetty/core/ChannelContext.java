package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.handler.ChannelPipeline;

public interface ChannelContext{

    ChannelPipeline pipeline();

    Channel channel();

    EventLoop eventLoop();

    EventLoopGroup eventLoopGroup();
}
