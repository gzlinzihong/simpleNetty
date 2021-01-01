# simpleNetty

一个基于多Reactor多线程模型实现的JavaNIO框架

基于netty的思想,由自己实现

仅实现TCP连接之间的相互传输

包括 客户端/服务器

因为大三上学期 JavaSE实训2自选题 "自定义一个http server"

所以自己实现了一下reactor多线程模型

水平有限,有些细节没处理好(x

仅作学习使用

阅读此源码需 
1. 熟悉Java多线程的使用
2. 熟悉JavaNIO
3. 对设计模式有一定了解
4. 熟悉Future Promise模式

## 下载jar

[jar包下载](http://www.ilanky.cn/upload/2021/01/simpleNetty-1.0-SNAPSHOT-ca58321dc2174ab08a2ba47d129f16cf.jar)

## 流程

数据的进出称为入站出站

数据过来 是入站 写回去 是出站

一个ChannelHandlerContext对象对应一个channel一个pipeline

## 如何使用


新建一个类 继承ChannelInboundHandlerAdapter类(入站),ChannelOutboundHandlerAdapter类(出站)

重写方法即可

**在最前面的handler读到的数据对象一定是ByteBuffer,请自己转换给后续的handler,写的方法同理**


## demo

**server端**

```java
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
```

**client端**

```java
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
```