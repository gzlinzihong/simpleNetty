package cn.ilanky.simplenetty.concurrent;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.listen.Listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author: linzihong
 * @Date: 2020/12/22 10:58
 */
public class DefaultChannelFuture<V> extends AbstractChannelFuture<V>{

    public DefaultChannelFuture(EventExecutor executor, Channel channel, boolean isCancellable) {
        super(executor, channel, isCancellable);
    }
}
