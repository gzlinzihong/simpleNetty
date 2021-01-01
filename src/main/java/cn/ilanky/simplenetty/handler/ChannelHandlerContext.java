package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.EventLoop;
import cn.ilanky.simplenetty.core.EventLoopGroup;
import cn.ilanky.simplenetty.enums.ChannelHandlerType;

import java.nio.ByteBuffer;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月27日 13:11:07
 */
public interface ChannelHandlerContext{

    EventLoop eventLoop();

    EventLoopGroup eventLoopGroup();

    ChannelPipeline pipeline();

    Channel channel();

    void handlerAdded(ChannelHandler handler) throws Exception;

    void handlerRemoved(ChannelHandler handler) throws Exception;

    void fireActive() throws Exception;

    void fireInactive() throws Exception;

    void fireRead(Object var2) throws Exception;

    void fireReadComplete() throws Exception;

    void fireWrite(Object var2) throws Exception;

    void fireWriteComplete() throws Exception;

    void fireExceptionCaught(Throwable var2);

    void writeAndFlush(ByteBuffer buffer);
}
