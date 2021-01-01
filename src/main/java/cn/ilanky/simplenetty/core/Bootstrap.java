package cn.ilanky.simplenetty.core;

import cn.ilanky.simplenetty.channel.Channel;
import cn.ilanky.simplenetty.channel.ChannelOption;
import cn.ilanky.simplenetty.concurrent.ChannelFuture;
import cn.ilanky.simplenetty.handler.ChannelInitializer;

/**
 * @Author: linzihong
 * @Date: 2020/12/25 17:32
 */
public class Bootstrap extends AbstractBootstrap{
    @Override
    public Bootstrap group(NioEventLoopGroup parent, NioEventLoopGroup child) {
        super.group(parent, child);
        return this;
    }

    @Override
    public <T> Bootstrap option(ChannelOption<T> option, T value) {
        super.option(option, value);
        return this;
    }

    @Override
    public <T> Bootstrap childOption(ChannelOption<T> option, T value) {
        super.childOption(option, value);
        return this;
    }

    @Override
    public <T extends Channel> Bootstrap channel(Class<T> channel) {
        super.channel(channel);
        return this;
    }

    @Override
    public Bootstrap handler(ChannelInitializer handler) {
        super.handler(handler);
        return this;
    }

    @Override
    public ChannelFuture bind(int port) {
        return super.bind(port);
    }

    @Override
    public ChannelFuture connect(String host, int port) {
        return super.connect(host, port);
    }

    @Override
    public NioEventLoopGroup getChild() {
        return super.getChild();
    }
}
