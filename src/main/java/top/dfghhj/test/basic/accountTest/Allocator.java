package top.dfghhj.test.basic.accountTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 17:18
 * @description:
 */
public class Allocator {

    private List<Object> als = new ArrayList<>();

    private static Allocator allocator;

    public static Allocator getInstance() {
        if (allocator == null) {
            synchronized (Allocator.class) {
                if (allocator == null) {
                    allocator = new Allocator();
                }
            }
        }
        return allocator;
    }

    synchronized boolean apply(Object from, Object to) {
        if (als.contains(from) || als.contains(to)) {
            return false;
        } else {
            als.add(from);
            als.add(to);
        }
        return true;
    }

    synchronized void free(Object from, Object to) {
        als.remove(from);
        als.remove(to);
    }

}
