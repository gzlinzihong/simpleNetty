package cn.ilanky.simplenetty.concurrent;

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
 * @Date: 2020/12/25 16:42
 */
public abstract class AbstractFuture<V> implements Future<V>{
    protected EventExecutor executor;
    protected boolean isCancellable;

    public AbstractFuture(EventExecutor executor,boolean isCancellable) {
        this.executor = executor;
        this.isCancellable = isCancellable;
    }

    @Override
    public boolean isSuccess() {
        return executor.getState() == EventExecutor.NORMAL;
    }

    @Override
    public boolean isFail() {
        return executor.getState() == EventExecutor.EXCEPTIONAL;
    }

    @Override
    public boolean isCancellable() {
        return isCancellable;
    }

    @Override
    public Throwable cause() {
        return executor.getThrowable();
    }

    private void rethrowIfFailed(){
        Throwable cause = this.cause();
        if (cause != null){
            ExceptionHelper.throwException(cause);
        }
    }

    @Override
    public Future<V> sync() throws InterruptedException {
        // 1. 等待
        this.await();
        // 2. 重新抛出异常
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Future<V> syncUninterruptibly() {
        this.awaitUninterruptibly();
        this.rethrowIfFailed();
        return this;
    }

    @Override
    public Future<V> await() throws InterruptedException {
        if (this.isDone()){
            return this;
        }else if (Thread.interrupted()){
            throw new InterruptedException(this.toString());
        }else {
            while (!this.isDone()){
                if (executor.getRunner() == null){
                    Thread.yield();
                    continue;
                }
                if (executor.getRunner().isAlive()){
                    synchronized (executor){
                        executor.wait();
                    }
                }
            }
            return this;
        }
    }

    @Override
    public Future<V> awaitUninterruptibly() {
        if (this.isDone()) {
            return this;
        }else {
            while (!this.isDone()){
                if (executor.getRunner().isAlive()){
                    synchronized (executor){
                        try {
                            executor.wait();
                        }catch (InterruptedException ignore){
                            Thread.interrupted();
                        }
                    }
                }
            }
            return this;
        }
    }


    @Override
    public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
        return await0(timeUnit.toMillis(timeout),true);
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit timeUnit) {
        try {
            return await0(timeUnit.toMillis(timeout),false);
        } catch (InterruptedException ignore) {
        }
        return true;
    }

    private boolean await0(long timeout,boolean canInterrupt) throws InterruptedException {
        long waitTime = timeout;
        if (this.isDone()){
            return true;
        }else {
            long startTime = System.currentTimeMillis();
            while (waitTime < 0L){
                if (this.isDone()){
                    break;
                }
                if (executor.getRunner().isAlive()){
                    synchronized (executor){
                        try {
                            executor.wait(waitTime);
                        }catch (InterruptedException e){
                            // 被唤醒，算出下一轮要等待的时间
                            // 要等待的时间 减掉已经等待了的时间 则代表还要等待的时间
                            waitTime = waitTime - (System.currentTimeMillis() - startTime);
                            if (canInterrupt){
                                throw e;
                            }
                        }
                    }
                }
            }
            return waitTime > 0L;
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        // 如果是NEW状态 则直接取消成功
        // 如果是RUNNING状态 看是否打断
        if (isCancellable){
            if (executor.getState() == EventExecutor.NEW){
                return executor.setState(EventExecutor.NEW,EventExecutor.CANCELLED);
            }
            if (executor.getState() == EventExecutor.RUNNING){
                if (mayInterruptIfRunning){
                    executor.getRunner().interrupt();
                    return executor.setState(EventExecutor.RUNNING,EventExecutor.INTERRUPTED);
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return executor.getState() == EventExecutor.CANCELLED;
    }

    @Override
    public boolean isDone() {
        return executor.getState() >= EventExecutor.NORMAL;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        this.await();
        Throwable cause = this.cause();
        if (cause == null){
            return (V) this.executor.getResult();
        }else {
            throw new ExecutionException(cause);
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = this.await(timeout, unit);
        Throwable cause = this.cause();
        if (cause == null){
            return (V) this.executor.getResult();
        }else if (!success){
            throw new TimeoutException();
        } else {
            throw new ExecutionException(cause);
        }
    }

}
