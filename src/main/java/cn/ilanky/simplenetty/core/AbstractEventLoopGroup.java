package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.ChannelConfig;
import cn.ilanky.simplenetty.channel.ChannelOption;
import cn.ilanky.simplenetty.concurrent.DefaultFuture;
import cn.ilanky.simplenetty.concurrent.Future;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 15:04
 */
public abstract class AbstractEventLoopGroup implements EventLoopGroup{

    private boolean isParent;
    private static int coreSize;
    private ScheduledThreadPoolExecutor executor;
    private AbstractEventLoop[] eventLoops;
    private int index = 0;
    AbstractBootstrap bootstrap;

    public AbstractEventLoopGroup(boolean isParent) {
        this.isParent = isParent;
    }

    public AbstractEventLoopGroup(int coreSize,boolean isParent) {
        coreSize = coreSize;
        this.isParent = isParent;
    }

    protected void init() {
        initConfig();
        executor = new ScheduledThreadPoolExecutor(coreSize);
        eventLoops = new AbstractEventLoop[coreSize];
        for (int i = 0;i<coreSize;i++){
            eventLoops[i] = new AbstractEventLoop(new EventExecutor(),this);
        }
    }

    private void initConfig(){
        if (isParent){
            coreSize = ChannelConfig.getParentOption(ChannelOption.CORE_SIZE.getName(), Integer.class);
        }else {
            coreSize = ChannelConfig.getChildOption(ChannelOption.CORE_SIZE.getName(), Integer.class);
        }
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4) {
        return executor.schedule(var1,var2,var4);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4) {
        return executor.schedule(var1,var2,var4);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6) {
        return executor.scheduleAtFixedRate(var1,var2,var4,var6);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6) {
        return executor.scheduleWithFixedDelay(var1,var2,var4,var6);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return executor.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public <T> Future<T> submit(Callable<T> var1) {
        EventExecutor eventExecutor = new EventExecutor();
        eventExecutor.execute( () -> executor.submit(var1));
        return new DefaultFuture<T>(eventExecutor,false);
    }

    @Override
    public <T> Future<T> submit(Runnable var1, T var2) {
        EventExecutor eventExecutor = new EventExecutor();
        eventExecutor.execute(() -> executor.submit(var1,var2));
        return new DefaultFuture<T>(eventExecutor,false);
    }

    @Override
    public Future<?> submit(Runnable var1) {
        EventExecutor eventExecutor = new EventExecutor();
        eventExecutor.execute(() -> executor.submit(var1));
        return new DefaultFuture<>(eventExecutor,false);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException {
        List<Future<T>> list = new ArrayList<>();
        var1.forEach( (item) -> {
            EventExecutor eventExecutor = new EventExecutor();
            eventExecutor.execute(() -> executor.submit(item));
            list.add(new DefaultFuture<>(eventExecutor,false));
        });
        return list;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException {
        List<Future<T>> list = new ArrayList<>();
//        var1.forEach( (item) -> {
//            EventExecutor eventExecutor = new EventExecutor();
//            eventExecutor.execute(() -> executor.submit(item,var2,var4));
//            list.add(new DefaultFuture<>(eventExecutor,false));
//        });
        return list;
    }


    @Override
    public void execute(Runnable runnable, Object result) {
        this.submit(runnable, result);
    }

    @Override
    public void execute(Callable callable) {
        this.submit(callable);
    }

    @Override
    public EventLoop get() {
        if (index == coreSize){
            index = 0;
        }
        return eventLoops[index++];
    }

    @Override
    public List<EventLoop> list() {
        return Arrays.asList(eventLoops);
    }

    @Override
    public AbstractBootstrap bootstrap() {
        return bootstrap;
    }

}
