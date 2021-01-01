package cn.ilanky.simplenetty.channel;

import cn.ilanky.simplenetty.core.EventLoop;

import java.nio.ByteBuffer;

/**
 * @author 嘿 林梓鸿
 * @date 2021年 01月01日 17:55:25
 */
public abstract class AbstractNioSocketChannel extends AbstractNioChannel{

    ByteBuffer readBuffer;
    ByteBuffer writeBuffer;

    public AbstractNioSocketChannel(EventLoop loop) {
        super(loop);
        init();
    }

    private void init(){
        Integer bufferSize = ChannelConfig.getChildOption(ChannelOption.BUFFER_SIZE.name, Integer.class);
        if (bufferSize == null){
            bufferSize = ChannelOption.BUFFER_SIZE.value;
        }
        readBuffer = ByteBuffer.allocate(bufferSize);
        writeBuffer = ByteBuffer.allocate(bufferSize);
    }

}
