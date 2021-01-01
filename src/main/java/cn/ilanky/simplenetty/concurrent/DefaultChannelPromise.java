package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 11:36
 */
public class DefaultChannelPromise<V> extends AbstractChannelFuture<V> implements ChannelPromise<V> {


    public DefaultChannelPromise(EventExecutor executor, Channel channel, boolean isCancellable) {
        super(executor, channel, isCancellable);
    }

    @Override
    public ChannelPromise<V> addListener(Listener<V> listener, int op) {
        super.addListener(listener,op);
        return this;
    }

    @Override
    public ChannelPromise<V> addListeners(Map<Listener<V>, Integer> listeners) {
        super.addListeners(listeners);
        return this;
    }

    @Override
    public ChannelPromise<V> removeListener(Listener<V> listener) {
        super.removeListener(listener);
        return this;
    }

    @Override
    public ChannelPromise<V> removeListeners(List<Listener<V>> listeners) {
        super.removeListeners(listeners);
        return this;
    }

    @Override
    public ChannelPromise<V> sync() throws InterruptedException {
        super.sync();
        return this;
    }

    @Override
    public ChannelPromise<V> syncUninterruptibly() {
        super.syncUninterruptibly();
        return this;
    }

    @Override
    public ChannelPromise<V> await() throws InterruptedException {
        super.await();
        return this;
    }

    @Override
    public ChannelPromise<V> awaitUninterruptibly() {
        super.awaitUninterruptibly();
        return this;
    }

    @Override
    public boolean setUnCancellable() {
        super.isCancellable = false;
        return true;
    }

    @Override
    public ChannelPromise<V> success(V result) {
        if (setSuccess0(result)){
            return this;
        }else {
            String msg;
            int state = super.executor.getState();
            if (state == EventExecutor.NEW){
                msg = "task has not start running";
            }else {
                msg = "task has been completed";
            }
            throw new IllegalStateException(msg);
        }
    }

    @Override
    public boolean trySuccess(V result) {
        return this.setSuccess0(result);
    }

    private boolean setSuccess0(V result){
        boolean success = super.executor.setState(EventExecutor.RUNNING, EventExecutor.NORMAL);
        if (success){
            super.executor.setResult(result);
        }
        return success;
    }

    @Override
    public ChannelPromise<V> setFail(Throwable throwable) {
        if (setFail0(throwable)){
            return this;
        }else {
            throw new IllegalStateException("task has been completed");
        }
    }

    @Override
    public boolean tryFail(Throwable throwable) {
        return this.setFail0(throwable);
    }

    private boolean setFail0(Throwable throwable){
        boolean success = super.executor.setState(EventExecutor.RUNNING, EventExecutor.EXCEPTIONAL);
        if (success){
            super.executor.setThrowable(throwable);
        }
        return success;
    }
}
