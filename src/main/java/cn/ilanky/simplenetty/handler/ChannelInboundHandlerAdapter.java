package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.enums.ChannelHandlerType;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月27日 21:58:03
 */
public class ChannelInboundHandlerAdapter extends ChannelHandlerAdapter {

    public ChannelInboundHandlerAdapter() {
        type = ChannelHandlerType.IN_BOUND;
    }

    @Override
    public void channelActive(ChannelHandlerContext var1) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext var1) throws Exception {

    }

    @Override
    public Object channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
        return null;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext var1) throws Exception {

    }

    @Override
    public Object channelWrite(ChannelHandlerContext var1, Object var2) throws Exception {
        return var2;
    }

    @Override
    public void channelWriteComplete(ChannelHandlerContext var1) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext var1, Throwable var2){

    }
}
