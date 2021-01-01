package cn.ilanky.simplenetty.core;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import cn.ilanky.simplenetty.concurrent.Future;

public interface ExecutorService extends Executor {
    void shutdown();

    List<Runnable> shutdownNow();

    boolean isShutdown();

    <T> Future<T> submit(Callable<T> var1);

    <T> Future<T> submit(Runnable var1, T var2);

    Future<?> submit(Runnable var1);

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException;

    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException;
}
