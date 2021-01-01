package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.concurrent.ChannelPromise;
import cn.ilanky.simplenetty.concurrent.DefaultChannelPromise;
import cn.ilanky.simplenetty.concurrent.Future;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 15:02
 */
public class AbstractEventLoop implements EventLoop{

    EventLoopGroup parent;
    EventExecutor executor;
    Selector selector;

    public AbstractEventLoop(EventExecutor executor,EventLoopGroup group) {
        this.executor = executor;
        this.parent = group;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public EventExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(EventExecutor executor) {
        this.executor = executor;
    }

    @Override
    public EventLoopGroup parent() {
        return parent;
    }

    @Override
    public ChannelPromise register(Channel channel, int op) {
        DefaultChannelPromise<SelectionKey> promise = new DefaultChannelPromise<>(executor, channel, false);
        SelectionKey key = null;
        try {
            key = channel.javaChannel().register(selector, op);
            promise.success(key);
        }catch (ClosedChannelException e){
            e.printStackTrace();
            promise.setFail(e);
        }
        return promise;
    }

    @Override
    public void execute(Runnable runnable, Object result) {
        parent.execute( () -> {
            executor.execute(runnable,result);
        },result);
    }

    @Override
    public void execute(Callable callable) {
        EventExecutorContext.set(executor);
        parent.execute( () -> {
            executor.execute(callable);
            return executor.getResult();
        });
    }

    @Override
    public EventLoop get() {
        return this;
    }

    @Override
    public List<EventLoop> list() {
        return parent.list();
    }

    @Override
    public AbstractBootstrap bootstrap() {
        return parent.bootstrap();
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
        return parent().schedule(var1,var2,var4);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
        return parent().schedule(var1,var2,var4);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
        return parent().scheduleAtFixedRate(var1,var2,var4,var6);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
        return parent().scheduleWithFixedDelay(var1,var2,var4,var6);
    }

    @Override
    public void shutdown() {
        parent.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return parent().shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return parent.isShutdown();
    }

    @Override
    public <T> Future<T> submit(Callable<T> var1) {
        return parent.submit(var1);
    }

    @Override
    public <T> Future<T> submit(Runnable var1, T var2) {
        return parent.submit(var1,var2);
    }

    @Override
    public Future<?> submit(Runnable var1) {
        return parent.submit(var1);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
        return parent.invokeAll(var1);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
        return invokeAll(var1,var2,var4);
    }
}
