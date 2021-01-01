package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.channel.AbstractNioChannel;
import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.EventLoop;
import cn.ilanky.simplenetty.core.EventLoopGroup;
import cn.ilanky.simplenetty.enums.ChannelHandlerType;

import java.nio.ByteBuffer;
import java.util.ListIterator;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月27日 22:11:49
 */
public class DefaultChannelHandlerContext implements ChannelHandlerContext {

    DefaultChannelPipeline pipeline;
    Channel channel;
    EventLoop eventLoop;
    EventLoopGroup group;

    public DefaultChannelHandlerContext(Channel channel, EventLoop eventLoop, EventLoopGroup group) {
        this.channel = channel;
        this.eventLoop = eventLoop;
        this.group = group;
    }

    public void setPipeline(DefaultChannelPipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public EventLoop eventLoop() {
        return eventLoop;
    }

    @Override
    public EventLoopGroup eventLoopGroup() {
        return group;
    }

    @Override
    public ChannelPipeline pipeline() {
        return pipeline;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public void handlerAdded(ChannelHandler handler) throws Exception {
        pipeline.handlers.add(handler);
    }

    @Override
    public void handlerRemoved(ChannelHandler handler) throws Exception {
        pipeline.handlers.remove(handler);
    }

    @Override
    public void fireActive() throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            handler.channelActive(this);
        }
    }

    @Override
    public void fireInactive() throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            handler.channelInactive(this);
        }
    }

    @Override
    public void fireRead(Object var2) throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        Object res = var2;
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            if (handler.type() == ChannelHandlerType.IN_BOUND){
                res = handler.channelRead(this, res);
            }
        }
    }

    @Override
    public void fireReadComplete() throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            if (handler.type() == ChannelHandlerType.IN_BOUND){
                handler.channelReadComplete(this);
            }
        }
    }

    @Override
    public void fireWrite(Object var2) throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        Object res = var2;
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            if (handler.type() == ChannelHandlerType.OUT_BOUND){
                res = handler.channelWrite(this,res);
            }
        }
    }

    @Override
    public void fireWriteComplete() throws Exception {
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            if (handler.type() == ChannelHandlerType.OUT_BOUND){
                handler.channelWriteComplete(this);
            }
        }
    }


    @Override
    public void fireExceptionCaught(Throwable var2){
        ListIterator<ChannelHandler> list = pipeline.handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler handler = list.next();
            handler.exceptionCaught(this,var2);
        }
    }

    @Override
    public void writeAndFlush(ByteBuffer buffer) {
        channel.unsafe().write(buffer);
        channel.unsafe().flush();
    }
}
