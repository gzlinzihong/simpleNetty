package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ChannelPromise<V> extends Promise<V>, ChannelFuture<V> {

    Channel channel();

    @Override
    ChannelPromise<V> success(V result);

    @Override
    ChannelPromise<V> setFail(Throwable throwable);

    @Override
    ChannelPromise<V> addListener(Listener<V> listener, int op);
    @Override
    ChannelPromise<V> addListeners(Map<Listener<V>,Integer> listeners);

    @Override
    ChannelPromise<V> removeListener(Listener<V> listener);
    @Override
    ChannelPromise<V> removeListeners(List<Listener<V>> listeners);

    @Override
    ChannelPromise<V> sync() throws InterruptedException;
    @Override
    ChannelPromise<V> syncUninterruptibly();

    @Override
    ChannelPromise<V> await() throws InterruptedException;
    @Override
    ChannelPromise<V> awaitUninterruptibly();
    @Override
    boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;
    @Override
    boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit);
}
