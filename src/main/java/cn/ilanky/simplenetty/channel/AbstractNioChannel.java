package cn.ilanky.simplenetty.channel;

import cn.ilanky.simplenetty.concurrent.ChannelFuture;
import cn.ilanky.simplenetty.concurrent.DefaultChannelPromise;
import cn.ilanky.simplenetty.core.*;
import cn.ilanky.simplenetty.handler.ChannelHandlerContext;
import cn.ilanky.simplenetty.handler.ChannelPipeline;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月17日 17:26:44
 */
public abstract class AbstractNioChannel implements Channel {

    AbstractEventLoop eventLoop;
    SelectionKey key;
    ChannelHandlerContext context;

    public AbstractNioChannel(EventLoop loop) {
        this.eventLoop = (AbstractEventLoop) loop;
//        this.channelConfig = channelConfig;
    }

    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }



    @Override
    public ChannelFuture closeFuture() {
        EventExecutor eventExecutor = new EventExecutor();
        DefaultChannelPromise<Void> promise = new DefaultChannelPromise<>(eventExecutor, this, false);
        new Thread(() -> {
            eventExecutor.execute( () ->{
                while (this.isActive()){
                    try {
                        synchronized (eventLoop.getExecutor()){
                            eventLoop.getExecutor().wait();
                        }
                    }catch (InterruptedException ignore){
                    }
                }
                promise.success(null);
                return promise;
            });
        }).start();
        return promise;
    }

    @Override
    public ChannelPipeline pipeline() {
        return context.pipeline();
    }
}
