package cn.ilanky.simplenetty.channel;

import java.util.*;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月17日 17:16:29
 */
public class ChannelConfig {

    private static Map<String,Object> parentOptions;
    private static Map<String,Object> childOptions;

    static {
        parentOptions = new HashMap<>();
        childOptions = new HashMap<>();
        parentOptions.put(ChannelOption.CORE_SIZE.name,2);
        childOptions.put(ChannelOption.CORE_SIZE.name,ChannelOption.CORE_SIZE.value);
    }

    public static <T> T getParentOption(String name,Class<T> tClass){
        return (T) parentOptions.get(name);
    }

    public static <T> T getChildOption(String name,Class<T> tClass){
        return (T) childOptions.get(name);
    }

    public static void addParentOption(ChannelOption option){
        parentOptions.put(option.name,option.value);
    }

    public static void addChildOption(ChannelOption option){
        childOptions.put(option.name,option.value);
    }


}
