package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isCancellable();
    boolean isSuccess();
    boolean isFail();

    Future<V> addListener(Listener<V> listener, int op);
    Future<V> addListeners(Map<Listener<V>,Integer> listeners);

    Future<V> removeListener(Listener<V> listener);
    Future<V> removeListeners(List<Listener<V>> listeners);

    Throwable cause();

    Future<V> sync() throws InterruptedException;
    Future<V> syncUninterruptibly();

    Future<V> await() throws InterruptedException;
    Future<V> awaitUninterruptibly();
    boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException;
    boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit);
}
