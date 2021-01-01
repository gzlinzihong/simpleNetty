package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.concurrent.AbstractFuture;
import cn.ilanky.simplenetty.core.EventExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 嘿 林梓鸿
 * @date 2021年 01月01日 16:50:02
 */
public class FutureHolder {

    private static Map<EventExecutor, AbstractChannelFuture> map = new HashMap<>();


    public static void put(EventExecutor eventExecutor, AbstractChannelFuture future){
        map.put(eventExecutor,future);
    }

    public static AbstractChannelFuture get(EventExecutor eventExecutor){
        return map.get(eventExecutor);
    }

    public static void clear(){
        map.clear();
    }
}
