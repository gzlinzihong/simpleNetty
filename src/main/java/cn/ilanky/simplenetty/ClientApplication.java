package cn.ilanky.simplenetty;

import cn.ilanky.simplenetty.channel.ChannelOption;
import cn.ilanky.simplenetty.channel.NioSocketChannel;
import cn.ilanky.simplenetty.concurrent.ChannelFuture;
import cn.ilanky.simplenetty.core.Bootstrap;
import cn.ilanky.simplenetty.core.NioEventLoopGroup;
import cn.ilanky.simplenetty.handler.ChannelHandlerContext;
import cn.ilanky.simplenetty.handler.ChannelInboundHandlerAdapter;
import cn.ilanky.simplenetty.handler.ChannelInitializer;
import cn.ilanky.simplenetty.listen.Listener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author: linzihong
 * @Date: 2020/12/25 17:45
 */
public class ClientApplication {
    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup worker = new NioEventLoopGroup(false);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture cf = bootstrap.group(null, worker)
                .channel(NioSocketChannel.class)
                .childOption(ChannelOption.CORE_SIZE,1)
                .childOption(ChannelOption.BUFFER_SIZE,1024)
                .handler(new ChannelInitializer() {
                    @Override
                    public void init(ChannelHandlerContext context) {
                        context.pipeline().addLast(new SimpleChannelHandler());
                    }
                })
                .connect("localhost",7676);
        // 由于连接的时候就可能失败 所以要监听
        cf.addListener(future -> {
            if (future.isFail()){
                System.out.println("连接失败");
                worker.shutdown();
            }
        },1);
        cf.sync();
        ChannelFuture closeFuture = cf.channel().closeFuture();
        closeFuture.addListener(new Listener() {
            @Override
            public void complete(ChannelFuture future) {
                System.out.println("关闭");
                worker.shutdown();
            }
        },1);
        closeFuture.sync();
    }

    static class SimpleChannelHandler extends ChannelInboundHandlerAdapter{
        Scanner scanner = new Scanner(System.in);
        @Override
        public void channelActive(ChannelHandlerContext var1) throws Exception {
            var1.writeAndFlush(ByteBuffer.wrap("hi server".getBytes()));
        }

        @Override
        public Object channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
            ByteBuffer buffer = (ByteBuffer) var2;
            System.out.println(new String(buffer.array(),0,buffer.position()));
            System.out.println("请输入你的话:");
            String s = scanner.nextLine();
            var1.writeAndFlush(ByteBuffer.wrap(s.getBytes()));
            return buffer;
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext var1, Throwable var2){
            var2.printStackTrace();
        }
    }
}
