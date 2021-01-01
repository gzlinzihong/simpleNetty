package cn.ilanky.simplenetty.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ScheduledExecutorService extends ExecutorService {
    ScheduledFuture<?> schedule(Runnable var1, long var2, TimeUnit var4);

    <V> ScheduledFuture<V> schedule(Callable<V> var1, long var2, TimeUnit var4);

    ScheduledFuture<?> scheduleAtFixedRate(Runnable var1, long var2, long var4, TimeUnit var6);

    ScheduledFuture<?> scheduleWithFixedDelay(Runnable var1, long var2, long var4, TimeUnit var6);
}
