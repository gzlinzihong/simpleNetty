package cn.ilanky.simplenetty.core;

import java.util.List;

public interface EventLoopGroup extends ScheduledExecutorService {

    EventLoop get();

    List<EventLoop> list();

    AbstractBootstrap bootstrap();
}
