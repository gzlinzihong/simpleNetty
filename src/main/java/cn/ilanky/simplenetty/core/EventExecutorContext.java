package cn.ilanky.simplenetty.core;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月26日 13:28:21
 */
public class EventExecutorContext {
    private static final ThreadLocal<EventExecutor> HOLDER = new ThreadLocal<>();

    public static void set(EventExecutor e){
        HOLDER.set(e);
    }

    public static EventExecutor get(){
        return HOLDER.get();
    }

    public static void clear(){
        HOLDER.remove();
    }
}
