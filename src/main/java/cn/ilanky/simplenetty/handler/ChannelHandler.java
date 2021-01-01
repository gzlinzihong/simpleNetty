package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.enums.ChannelHandlerType;

public interface ChannelHandler {

    ChannelHandlerType type();

    void channelActive(ChannelHandlerContext var1) throws Exception;

    void channelInactive(ChannelHandlerContext var1) throws Exception;

    Object channelRead(ChannelHandlerContext var1, Object var2) throws Exception;

    void channelReadComplete(ChannelHandlerContext var1) throws Exception;

    Object channelWrite(ChannelHandlerContext var1,Object var2) throws Exception;

    void channelWriteComplete(ChannelHandlerContext var1) throws Exception;

    void exceptionCaught(ChannelHandlerContext var1, Throwable var2);
}
