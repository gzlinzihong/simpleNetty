package cn.ilanky.simplenetty.exception;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @Author: linzihong
 * @Date: 2020/12/23 10:19
 */
public class ExceptionHelper {

    static final Unsafe UNSAFE;

    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException("UNSAFE get fail");
        }
    }

    public static <E extends Throwable> void throwException(E e){
        UNSAFE.throwException(e);
    }


}
