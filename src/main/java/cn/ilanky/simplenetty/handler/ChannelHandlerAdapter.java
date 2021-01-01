package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.enums.ChannelHandlerType;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月27日 13:15:23
 */
public abstract class ChannelHandlerAdapter implements ChannelHandler {
    ChannelHandlerType type;

    @Override
    public ChannelHandlerType type() {
        return type;
    }
}
