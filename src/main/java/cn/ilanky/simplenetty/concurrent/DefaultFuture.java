package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;

/**
 * @Author: linzihong
 * @Date: 2020/12/25 16:48
 */
public class DefaultFuture<V> extends AbstractFuture<V>{
    public DefaultFuture(EventExecutor executor, boolean isCancellable) {
        super(executor, isCancellable);
    }

    @Override
    public Future<V> addListener(Listener<V> listener, int op) {
        throw new UnsupportedOperationException("listener should be ChannelFuture");
    }

    @Override
    public Future<V> addListeners(Map<Listener<V>, Integer> listeners) {
        throw new UnsupportedOperationException("listener should be ChannelFuture");
    }

    @Override
    public Future<V> removeListener(Listener<V> listener) {
        throw new UnsupportedOperationException("listener should be ChannelFuture");
    }

    @Override
    public Future<V> removeListeners(List<Listener<V>> listeners) {
        throw new UnsupportedOperationException("listener should be ChannelFuture");
    }
}
