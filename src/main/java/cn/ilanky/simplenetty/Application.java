package cn.ilanky.simplenetty;

import cn.ilanky.simplenetty.channel.ChannelOption;
import cn.ilanky.simplenetty.channel.NioServerSocketChannel;
import cn.ilanky.simplenetty.concurrent.ChannelFuture;
import cn.ilanky.simplenetty.concurrent.DefaultChannelFuture;
import cn.ilanky.simplenetty.concurrent.DefaultChannelPromise;
import cn.ilanky.simplenetty.core.Bootstrap;
import cn.ilanky.simplenetty.core.ByteConstants;
import cn.ilanky.simplenetty.core.EventExecutor;
import cn.ilanky.simplenetty.core.NioEventLoopGroup;
import cn.ilanky.simplenetty.handler.ChannelHandlerContext;
import cn.ilanky.simplenetty.handler.ChannelInboundHandlerAdapter;
import cn.ilanky.simplenetty.handler.ChannelInitializer;
import cn.ilanky.simplenetty.listen.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月12日 10:52:52
 */
public class Application {
    public static void main(String[] args) throws InterruptedException, IOException {
        // NioEventLoopGroup 即一个线程池 需指定是否为parent。也就是做accept事件的
        NioEventLoopGroup boss = new NioEventLoopGroup(true);
        NioEventLoopGroup worker = new NioEventLoopGroup(false);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture cf = bootstrap.group(boss, worker)
                // 指定通道
                .channel(NioServerSocketChannel.class)
                // accept事件线程的属性
                .option(ChannelOption.CORE_SIZE,1)
                // read/write事件线程的属性
                .childOption(ChannelOption.CORE_SIZE,5)
                .childOption(ChannelOption.BUFFER_SIZE, ByteConstants.KB * 2)
                // 处理器的初始化
                .handler(new ChannelInitializer() {
                    @Override
                    public void init(ChannelHandlerContext context) {
                        context.pipeline().addLast(new SimpleChannelHandler());
                    }
                })
                // 绑定端口
                .bind(7676);
        cf.addListener(future -> {
            if (future.isFail()){
                System.out.println("绑定失败");
                worker.shutdown();
                boss.shutdown();
            }
        },1);
        // 阻塞等待该Future对应的任务完成
        cf.sync();
        // 通道关闭时的future
        ChannelFuture closeFuture = cf.channel().closeFuture();
        // 监听
        closeFuture.addListener(new Listener() {
            @Override
            public void complete(ChannelFuture future) {
                worker.shutdown();
                boss.shutdown();
            }
        },1);
        // 监听器有优先级，数字越小越先执行
        closeFuture.addListener(future -> System.out.println("第二"),2);
        // 等待
        closeFuture.sync();
    }

    static class SimpleChannelHandler extends ChannelInboundHandlerAdapter {

        @Override
        public Object channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
            ByteBuffer buffer = (ByteBuffer) var2;
            System.out.println(new String(buffer.array(),0,buffer.position()));
            var1.writeAndFlush(ByteBuffer.wrap("hello world".getBytes()));
            return buffer;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext var1, Throwable var2){
            var2.printStackTrace();
        }
    }
}
