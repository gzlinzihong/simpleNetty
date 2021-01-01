package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface Promise<V> extends Future<V> {

    boolean setUnCancellable();

    Promise<V> success(V result);
    boolean trySuccess(V result);

    Promise<V> setFail(Throwable throwable);
    boolean tryFail(Throwable throwable);

    @Override
    Promise<V> addListener(Listener<V> listener, int op);
    @Override
    Promise<V> addListeners(Map<Listener<V>,Integer> listeners);

    @Override
    Promise<V> removeListener(Listener<V> listener);
    @Override
    Promise<V> removeListeners(List<Listener<V>> listeners);

    @Override
    Promise<V> sync() throws InterruptedException;
    @Override
    Promise<V> syncUninterruptibly();

    @Override
    Promise<V> await() throws InterruptedException;
    @Override
    Promise<V> awaitUninterruptibly();
    @Override
    boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;
    @Override
    boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit);
}
