package cn.ilanky.simplenetty.listen;

import cn.ilanky.simplenetty.concurrent.ChannelFuture;

import java.util.Objects;

/**
 * @author 嘿 林梓鸿
 * @date 2021年 01月01日 16:55:32
 */
public class ListenerWrapper implements Comparable<ListenerWrapper>{
    private Listener listener;
    private int op;

    public ListenerWrapper(Listener listener, int op) {
        this.listener = listener;
        this.op = op;
    }

    public void listen(ChannelFuture future){
        listener.complete(future);
    }

    @Override
    public int compareTo(ListenerWrapper listenerWrapper) {
        return this.op - listenerWrapper.op;
    }
}
