package cn.ilanky.simplenetty.channel;

import cn.ilanky.simplenetty.concurrent.ChannelPromise;
import cn.ilanky.simplenetty.core.AbstractEventLoop;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.core.EventLoop;
import cn.ilanky.simplenetty.factory.ChannelFactory;
import cn.ilanky.simplenetty.handler.ChannelInitializer;
import cn.ilanky.simplenetty.handler.DefaultChannelHandlerContext;
import cn.ilanky.simplenetty.handler.DefaultChannelPipeline;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月17日 17:26:00
 */
public class NioServerSocketChannel extends AbstractNioChannel{

    private volatile static Unsafe unsafe;
    private ServerSocketChannel serverSocketChannel;
    private NioServerSocketChannel that = this;

    public NioServerSocketChannel(EventLoop loop) {
        super(loop);
        unsafe();
    }

    @Override
    public AbstractSelectableChannel javaChannel() {
        return serverSocketChannel;
    }

    @Override
    public boolean isOpen() {
        return serverSocketChannel.isOpen();
    }

    @Override
    public boolean isRegistered() {
        return serverSocketChannel.isRegistered();
    }

    @Override
    public boolean isActive() {
        return this.isOpen() && serverSocketChannel.socket().isBound();
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
            synchronized (NioServerSocketChannel.class){
                if (unsafe == null){
                    return new UnsafeImpl();
                }
            }
        }
        return unsafe;
    }

    class UnsafeImpl implements Unsafe{

        public UnsafeImpl() {
            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public SocketAddress localAddress() {
            try {
                return serverSocketChannel.getLocalAddress();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public SocketAddress remoteAddress() {
            throw new UnsupportedOperationException("serverSocketChannel not support remoteAddress");
        }

        @Override
        public void bind(SocketAddress address, ChannelPromise promise) {
            try {
                serverSocketChannel.bind(address);
                ChannelPromise<SelectionKey> p = null;
                p = eventLoop.register(that,SelectionKey.OP_ACCEPT);
                key = p.get();
                synchronized (eventLoop.getExecutor()){
                    eventLoop.getExecutor().notifyAll();
                }

                eventLoop.parent().list().forEach(item -> {
                    if (item != eventLoop){
                        item.execute(() -> {
                            run((AbstractEventLoop) item);
                            return null;
                        });
                    }
                });
                run(eventLoop);
                // bind成功 写成功
            } catch (IOException | InterruptedException | ExecutionException e) {
                try {
                    javaChannel().close();
                    promise.setFail(e);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void run(AbstractEventLoop eventLoop){
            try {
                while (isActive()){
                    int select = eventLoop.getSelector().select(2000);
                    if (select < 1){
                        continue;
                    }
                    Set<SelectionKey> keys = eventLoop.getSelector().selectedKeys();
                    Iterator<SelectionKey> keyIterator = keys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            accept();
                        }
                        keyIterator.remove(); //该事件已经处理，可以丢弃
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void accept(){
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel == null){
                    return;
                }
                socketChannel.configureBlocking(false);
                AbstractEventLoop loop = (AbstractEventLoop) eventLoop.bootstrap().getChild().get();
                ChannelInitializer initializer = eventLoop.bootstrap().getInitializer();
                if (loop.getExecutor().getState() > EventExecutor.RUNNING){
                    loop.setExecutor(new EventExecutor());
                }
                loop.execute(() -> {
                    try {
                        NioSocketChannel nioSocketChannel = new NioSocketChannel(loop,socketChannel);
                        DefaultChannelHandlerContext context = new DefaultChannelHandlerContext(nioSocketChannel, eventLoop, eventLoop.parent());
                        nioSocketChannel.setContext(context);
                        DefaultChannelPipeline pipeline = new DefaultChannelPipeline();
                        pipeline.setContext(context);
                        context.setPipeline(pipeline);
                        initializer.init(context);
                        nioSocketChannel.run();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void connect(SocketAddress address, ChannelPromise promise) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void write(ByteBuffer buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException();
        }
    }
}
