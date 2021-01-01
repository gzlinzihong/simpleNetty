package cn.ilanky.simplenetty.channel;

import cn.ilanky.simplenetty.concurrent.ChannelFuture;
import cn.ilanky.simplenetty.concurrent.ChannelPromise;
import cn.ilanky.simplenetty.handler.ChannelPipeline;
import cn.ilanky.simplenetty.core.EventLoop;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.spi.AbstractSelectableChannel;

public interface Channel {

    AbstractSelectableChannel javaChannel();

    EventLoop eventLoop();

    boolean isOpen();

    boolean isRegistered();

    boolean isActive();

    SocketAddress localAddress();

    SocketAddress remoteAddress();

    ChannelFuture closeFuture();

    Channel.Unsafe unsafe();

    ChannelPipeline pipeline();

    public interface Unsafe {

        SocketAddress localAddress();

        SocketAddress remoteAddress();

        void bind(SocketAddress address, ChannelPromise promise);

        void connect(SocketAddress address,ChannelPromise promise);

//        void disconnect(ChannelPromise promise);
//
//        void close(ChannelPromise promise);
//
//        void closeForcibly();
//
//        void deregister(ChannelPromise promise);
//
//        void beginRead();

        void write(ByteBuffer buffer);

        void flush();
    }
}
