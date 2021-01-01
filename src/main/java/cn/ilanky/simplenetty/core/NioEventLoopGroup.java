package cn.ilanky.simplenetty.core;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月12日 13:27:48
 */
public class NioEventLoopGroup extends AbstractEventLoopGroup{
    public NioEventLoopGroup(boolean isParent) {
        super(isParent);
    }

    public NioEventLoopGroup(int coreSize, boolean isParent) {
        super(coreSize, isParent);
    }
}
