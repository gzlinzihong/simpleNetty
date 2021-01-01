package cn.ilanky.simplenetty.exception;

/**
 * @Author: linzihong
 * @Date: 2020/12/22 17:14
 */
public class CanceledException extends RuntimeException{

    public CanceledException(String msg){
        super(msg);
    }
}
