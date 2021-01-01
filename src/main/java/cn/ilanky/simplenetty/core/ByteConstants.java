package cn.ilanky.simplenetty.core;

/**
 * @author 嘿 林梓鸿
 * @date 2021年 01月01日 17:26:20
 */
public class ByteConstants {

    public static final int KB = 1024;
    public static final int MB = KB * 1024;
    public static final int GB = MB * 1024;

    public static String toKbString(int value){
        return String.valueOf(value/KB) + "K";
    }
    public static int toKb(int value){
        return value/KB;
    }

    public static String toMbString(int value){
        return String.valueOf(value/MB) + "M";
    }
    public static int toMb(int value){
        return value/MB;
    }

    public static String toGbString(int value){
        return String.valueOf(value/GB) + "G";
    }
    public static int toGb(int value){
        return value/GB;
    }
}
