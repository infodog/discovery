package net.xinshi.discovery.search.client.util;

/**
 * Created with IntelliJ IDEA.
 * User: benzhao
 * Date: 8/23/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class MidThreadLocal {
    public static final ThreadLocal<String> midThreadLocal = new ThreadLocal<String>();

    public static void set(String mid) {
        midThreadLocal.set(mid);
    }

    public static void unset() {
        midThreadLocal.remove();
    }

    public static String get() {
        return midThreadLocal.get();
    }

}
