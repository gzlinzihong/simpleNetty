package cn.ilanky.simplenetty.core;

import java.util.concurrent.Callable;

public interface Executor {
    void execute(Runnable runnable,Object result);
    void execute(Callable callable);
}
