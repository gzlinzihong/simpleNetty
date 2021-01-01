package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.concurrent.ChannelPromise;

public interface EventLoop extends EventLoopGroup{

    EventLoopGroup parent();

    ChannelPromise register(Channel channel,int op);

}
