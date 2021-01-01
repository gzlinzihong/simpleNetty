package cn.ilanky.simplenetty.channel;

/**
 * @author 嘿 林梓鸿
 * @date 2020年 12月12日 13:41:45
 */
public class ChannelOption<T> {

    public static final ChannelOption<Integer> BUFFER_SIZE = new ChannelOption<>("BUFFER_SIZE",1024 * 1024 );
    public static final ChannelOption<Integer> CORE_SIZE = new ChannelOption<>("CORE_SIZE",Runtime.getRuntime().availableProcessors());
    String name;
    T value;

    public ChannelOption(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
