package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ChannelFuture<V> extends Future<V>{
    Channel channel();

    @Override
    ChannelFuture<V> addListener(Listener<V> listener, int op);
    @Override
    ChannelFuture<V> addListeners(Map<Listener<V>,Integer> listeners);

    @Override
    ChannelFuture<V> removeListener(Listener<V> listener);
    @Override
    ChannelFuture<V> removeListeners(List<Listener<V>> listeners);

    @Override
    ChannelFuture<V> sync() throws InterruptedException;
    @Override
    ChannelFuture<V> syncUninterruptibly();

    @Override
    ChannelFuture<V> await() throws InterruptedException;
    @Override
    ChannelFuture<V> awaitUninterruptibly();
    @Override
    boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;
    @Override
    boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit);
}
