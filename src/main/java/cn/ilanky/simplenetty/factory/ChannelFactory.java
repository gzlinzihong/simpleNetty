package cn.ilanky.simplenetty.factory;

import cn.ilanky.simplenetty.core.EventLoop;

import java.lang.reflect.InvocationTargetException;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月31日 18:14:54
 */
public class ChannelFactory{

    public static <T> T create(Class<T> tClass, EventLoop eventLoop) {
        try {
            return tClass.getDeclaredConstructor(EventLoop.class).newInstance(eventLoop);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
