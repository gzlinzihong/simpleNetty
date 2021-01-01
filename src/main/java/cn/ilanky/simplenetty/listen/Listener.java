package cn.ilanky.simplenetty.listen;

import cn.ilanky.simplenetty.concurrent.ChannelFuture;

public interface Listener<V>{

    void complete(ChannelFuture<V> future);
}
