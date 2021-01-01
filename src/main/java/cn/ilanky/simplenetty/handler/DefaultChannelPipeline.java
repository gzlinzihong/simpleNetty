package cn.ilanky.simplenetty.handler;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.ChannelContext;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月27日 22:17:15
 */
public class DefaultChannelPipeline implements ChannelPipeline {

    LinkedList<ChannelHandler> handlers;
    ChannelHandlerContext context;

    public DefaultChannelPipeline() {
        handlers = new LinkedList<>();
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public Channel channel() {
        return null;
    }

    @Override
    public ChannelHandlerContext context() {
        return context;
    }

    @Override
    public ChannelHandler head() {
        return handlers.getFirst();
    }

    @Override
    public ChannelHandler tail() {
        return handlers.getLast();
    }

    @Override
    public void addBefore(ChannelHandler reference, ChannelHandler handler) {
        ListIterator<ChannelHandler> list = handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler next = list.next();
            if (next.equals(reference)){
                handlers.add(list.previousIndex(),handler);
                break;
            }
        }
    }

    @Override
    public void addAfter(ChannelHandler reference, ChannelHandler handler) {
        ListIterator<ChannelHandler> list = handlers.listIterator();
        while (list.hasNext()){
            ChannelHandler next = list.next();
            if (next.equals(reference)){
                handlers.add(list.nextIndex(),handler);
                break;
            }
        }
    }

    @Override
    public void addLast(ChannelHandler handler) {
        handlers.addLast(handler);
    }

    @Override
    public void addFirst(ChannelHandler handler) {
        handlers.addFirst(handler);
    }

}
