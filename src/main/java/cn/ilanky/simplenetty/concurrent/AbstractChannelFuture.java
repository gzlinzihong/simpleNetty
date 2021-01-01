package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.exception.ExceptionHelper;
import cn.ilanky.simplenetty.listen.Listener;
import cn.ilanky.simplenetty.listen.ListenerWrapper;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 10:05
 */
public abstract class AbstractChannelFuture<V> extends AbstractFuture<V> implements ChannelFuture<V>{
    protected Channel channel;
    protected TreeSet<ListenerWrapper> listeners;

    public AbstractChannelFuture(EventExecutor executor, Channel channel, boolean isCancellable) {
        super(executor,isCancellable);
        FutureHolder.put(executor,this);
        listeners = new TreeSet<>();
        this.channel = channel;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public boolean isCancellable() {
        return isCancellable;
    }

    @Override
    public ChannelFuture<V> addListener(Listener<V> listener, int op) {
        listeners.add(new ListenerWrapper(listener,op));
        return this;
    }

    @Override
    public ChannelFuture<V> addListeners(Map<Listener<V>, Integer> listeners) {
        listeners.forEach((l,op) -> {
            this.listeners.add(new ListenerWrapper(l,op));
        });
        return this;
    }

    @Override
    public ChannelFuture<V> removeListener(Listener<V> listener) {
        listeners.remove(listener);
        return this;
    }

    @Override
    public ChannelFuture<V> removeListeners(List<Listener<V>> listeners) {
        this.listeners.removeAll(listeners);
        return this;
    }

    @Override
    public Throwable cause() {
        return super.cause();
    }

    @Override
    public ChannelFuture<V> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelFuture<V> syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ChannelFuture<V> await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ChannelFuture<V> awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }


    @Override
    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return super.await(timeout,timeUnit);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
        return super.awaitUninterruptibly(timeout,timeUnit);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled();
    }

    @Override
    public boolean isDone() {
        return super.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return super.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return super.get(timeout,unit);
    }

    public void fireListen(){
        listeners.forEach(item -> item.listen(this));
    }
}
