package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.concurrent.AbstractChannelFuture;
import cn.ilanky.simplenetty.concurrent.FutureHolder;
import cn.ilanky.simplenetty.exception.ExceptionHelper;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 15:08
 */
public class EventExecutor implements Executor{

    private volatile int state;

    public static final int NEW          = 0;
    public static final int RUNNING      = 1;
    public static final int COMPLETING   = 2;
    public static final int NORMAL       = 3;
    public static final int EXCEPTIONAL  = 4;
    public static final int CANCELLED    = 5;
    public static final int INTERRUPTED  = 6;

    private static final Unsafe UNSAFE;
    private static final long stateOffset;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
            Field f = EventExecutor.class.getDeclaredField("state");
            f.setAccessible(true);
            stateOffset = UNSAFE.objectFieldOffset(f);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("unsafe get fail");
        }
    }

    public EventExecutor() {
        state = NEW;
    }

    private Object result;
    private Thread runner;
    private Throwable throwable;

    @Override
    public void execute(Runnable runnable, Object result) {
        Callable<Object> callable = Executors.callable(runnable, result);
        this.execute(callable);
    }

    @Override
    public void execute(Callable callable) {
        if (this.state != NEW){
            return;
        }
        if (UNSAFE.compareAndSwapInt(this, stateOffset,NEW,RUNNING)){
            try {
                runner = Thread.currentThread();
                Object result = callable.call();
                doSuccess(result);
            }catch (Throwable e){
                doFail(e);
            }finally {
                if (this.state == INTERRUPTED){
                    if (UNSAFE.compareAndSwapInt(this,stateOffset,INTERRUPTED,CANCELLED)){
                        Thread.yield();
                    }
                }
                AbstractChannelFuture channelFuture = FutureHolder.get(this);
                if (channelFuture != null){
                    channelFuture.fireListen();
                }
                synchronized (this){
                    this.notifyAll();
                }
            }
        }
    }


    private void doFail(Throwable e){
        if (UNSAFE.compareAndSwapInt(this,stateOffset,RUNNING,EXCEPTIONAL)){
            result = null;
            throwable = e;
        }
    }

    private void doSuccess(Object result){
        if (UNSAFE.compareAndSwapInt(this,stateOffset,RUNNING,COMPLETING)){
            this.result = result;
            UNSAFE.putOrderedInt(this,stateOffset,NORMAL);
        }
    }

    public int getState() {
        return state;
    }

    public boolean setState(int check,int state) {
        return UNSAFE.compareAndSwapInt(this,stateOffset,check,state);
    }

    public Object getResult() {
        if (this.state == NORMAL){
            return result;
        }
        ExceptionHelper.throwException(throwable);
        return null;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Thread getRunner() {
        return runner;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
