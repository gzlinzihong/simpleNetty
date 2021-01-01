package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.*;
import cn.ilanky.simplenetty.concurrent.*;
import cn.ilanky.simplenetty.factory.ChannelFactory;
import cn.ilanky.simplenetty.handler.ChannelInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月12日 13:26:48
 */
public abstract class AbstractBootstrap {

    NioEventLoopGroup parent;
    NioEventLoopGroup child;
    Class<? extends Channel> channelClass;
    ChannelInitializer initializer;


    AbstractBootstrap group(NioEventLoopGroup parent,NioEventLoopGroup child){
        this.parent = parent;
        if (parent != null){
            parent.bootstrap = this;
        }
        child.bootstrap = this;
        this.child = child;
        return this;
    }

    <T> AbstractBootstrap option(ChannelOption<T> option,T value){
        ChannelConfig.addParentOption(new ChannelOption<T>(option.getName(),value));
        return this;
    }

    <T> AbstractBootstrap childOption(ChannelOption<T> option,T value){
        ChannelConfig.addChildOption(new ChannelOption<T>(option.getName(),value));
        return this;
    }

    <T extends Channel> AbstractBootstrap channel(Class<T> channel){
        if (channel.isInterface()){
            throw new IllegalArgumentException("channel class should not be interface");
        }
        channelClass = channel;
        return this;
    }

    AbstractBootstrap handler(ChannelInitializer initializer){
        this.initializer = initializer;
        return this;
    }

    ChannelFuture bind(int port){
        parent.init();
        child.init();
        InetSocketAddress socketAddress = new InetSocketAddress(port);
        if (socketAddress == null){
            throw new NullPointerException("port must not be null");
        }else {
            return this.doBind(socketAddress);
        }
    }

    ChannelFuture connect(String host,int port){
        child.init();
        InetSocketAddress socketAddress = new InetSocketAddress(host, port);
        if (socketAddress == null){
            throw new NullPointerException("port must not be null");
        }else {
            return this.doConnect(socketAddress);
        }
    }

    private ChannelFuture doConnect(InetSocketAddress socketAddress) {
        AbstractEventLoop eventLoop = (AbstractEventLoop)child.get();
        EventExecutor eventExecutor = eventLoop.getExecutor();
        Channel channel = ChannelFactory.create(channelClass, eventLoop);
        DefaultChannelPromise promise = new DefaultChannelPromise(eventExecutor, channel, false);
        eventLoop.execute(() -> {
            channel.unsafe().connect(socketAddress,promise);
            return promise;
        });
        return promise;
    }

    private ChannelFuture doBind(final InetSocketAddress socketAddress) {
        // 1. 生成一个channel对象
        // 2. 生成此channel对象绑定的channelFuture对象并返回
        AbstractEventLoop eventLoop = (AbstractEventLoop) parent.get();
        EventExecutor eventExecutor = eventLoop.getExecutor();
        Channel channel = ChannelFactory.create(channelClass, eventLoop);
        ChannelPromise promise = new DefaultChannelPromise(eventExecutor,channel,false);
        eventLoop.execute(() -> {
            channel.unsafe().bind(socketAddress,promise);
            return promise;
        });
        return promise;
    }

    public NioEventLoopGroup getChild() {
        return child;
    }

    public ChannelInitializer getInitializer() {
        return initializer;
    }
}
