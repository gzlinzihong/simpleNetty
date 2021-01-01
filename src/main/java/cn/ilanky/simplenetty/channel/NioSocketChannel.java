package cn.ilanky.simplenetty.channel;

import cn.ilanky.simplenetty.concurrent.ChannelPromise;
import cn.ilanky.simplenetty.concurrent.DefaultChannelPromise;
import cn.ilanky.simplenetty.core.AbstractEventLoop;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.core.EventLoop;
import cn.ilanky.simplenetty.handler.ChannelHandlerContext;
import cn.ilanky.simplenetty.handler.ChannelInitializer;
import cn.ilanky.simplenetty.handler.DefaultChannelHandlerContext;
import cn.ilanky.simplenetty.handler.DefaultChannelPipeline;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: linzihong
 * @Date: 2020/12/24 16:56
 */
public class NioSocketChannel extends AbstractNioSocketChannel{

    private volatile Unsafe unsafe;
    private SocketChannel socketChannel;
    private NioSocketChannel that = this;
    private ChannelHandlerContext context;
    private SelectionKey nowKey;

    public NioSocketChannel(EventLoop eventLoop) {
        super(eventLoop);
        unsafe = unsafe();
    }

    public NioSocketChannel(EventLoop loop, SocketChannel socketChannel) {
        super(loop);
        this.socketChannel = socketChannel;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }

    @Override
    public AbstractSelectableChannel javaChannel() {
        return socketChannel;
    }

    @Override
    public boolean isOpen() {
        return socketChannel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return socketChannel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && socketChannel.isConnected();
    }

    @Override
    public SocketAddress localAddress() {
        return unsafe().localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return unsafe().remoteAddress();
    }

    @Override
    public Unsafe unsafe() {
        if (unsafe == null){
            synchronized (this){
                if (unsafe == null){
                    return new UnsafeImpl();
                }
            }
        }
        return unsafe;
    }

    public void run() throws Exception {
        eventLoop.register(this, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
        doRun();
    }

    protected void doRun() throws Exception{
        if (this.isActive()){
            context.fireActive();
        }
        while (this.isActive()){
            try {
                int select = eventLoop.getSelector().select(2000);
                if (select < 1){
                    continue;
                }
                Set<SelectionKey> keys = eventLoop.getSelector().selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();
                while (keyIterator.hasNext()) {
                    nowKey = keyIterator.next();
                    keyIterator.remove();
                    try {
                        if (!nowKey.isValid()) {
                            continue;
                        }
                        if (nowKey.isReadable()) {
                            if (!read(nowKey,eventLoop.getSelector())){
                                continue;
                            }
                            context.fireRead(readBuffer);
                            context.fireReadComplete();
                        }
                        if (nowKey.isWritable()){
                            context.fireWrite(writeBuffer);
                            write(nowKey,eventLoop.getSelector());
                            context.fireWriteComplete();
                        }
                    }catch (Exception e){
                        context.fireExceptionCaught(e);
                    }
                    //该事件已经处理，可以丢弃
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            context.fireInactive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean read(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        readBuffer.clear();
//        readBuffer.flip();
        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
            if (numRead == -1){
                key.cancel();
                socketChannel.close();
                return false;
            }
            // 若一次读满 则做扩容操作
            if (numRead == readBuffer.capacity()){
                do {
                    ByteBuffer tmp = readBuffer;
                    readBuffer = ByteBuffer.allocate(readBuffer.capacity() * 2);
                    readBuffer.put(tmp.array());
                    numRead = socketChannel.read(readBuffer);
                } while ((numRead + readBuffer.remaining()) >= readBuffer.capacity());
            }
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            e.printStackTrace();
            key.cancel();
            socketChannel.close();
            return false;
        }
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
        return true;
    }
    private void write(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        writeBuffer.flip();
        while (writeBuffer.hasRemaining()){
            socketChannel.write(writeBuffer);
        }
        writeBuffer.clear();
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
    }

    private class UnsafeImpl implements Unsafe{

        public UnsafeImpl() {
            try {
                if (socketChannel == null){
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public SocketAddress localAddress() {
            try {
                return socketChannel.getLocalAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            try {
                return socketChannel.getRemoteAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void bind(SocketAddress address, ChannelPromise promise) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void connect(SocketAddress address, ChannelPromise promise) {
            try {
                socketChannel.connect(address);
                while(! socketChannel.finishConnect() ){
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                ChannelPromise<SelectionKey> p = null;
                p = eventLoop.register(that,SelectionKey.OP_CONNECT);
                key = p.get();
                synchronized (eventLoop.getExecutor()){
                    eventLoop.getExecutor().notifyAll();
                }
                ChannelInitializer initializer = eventLoop.bootstrap().getInitializer();
                DefaultChannelHandlerContext context = new DefaultChannelHandlerContext(that, eventLoop, eventLoop.parent());
                that.setContext(context);
                DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
                pipeline.setContext(context);
                context.setPipeline(pipeline);
                initializer.init(context);
                doRun();
            } catch (Exception e) {
                try {
                    promise.setFail(e);
                    context.fireExceptionCaught(e);
                    javaChannel().close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }


        @Override
        public void write(ByteBuffer buffer) {
            for (int i = 0;i<buffer.limit();i++){
                writeBuffer.put(buffer.get(i));
            }
        }

        @Override
        public void flush() {
            SocketChannel channel = null;
            if (nowKey == null){
                channel = (SocketChannel) javaChannel();
            }else {
                channel = (SocketChannel) nowKey.channel();
            }
            try {
                channel.register(eventLoop.getSelector(), SelectionKey.OP_WRITE);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
}
