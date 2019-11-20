package top.dfghhj.basic.accountTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 17:18
 * @description: 等待-通知
 */
public class Allocator2 {

    private List<Object> als = new ArrayList<>();

    private static Allocator2 allocator;

    public static Allocator2 getInstance() {
        if (allocator == null) {
            synchronized (Allocator2.class) {
                if (allocator == null) {
                    allocator = new Allocator2();
                }
            }
        }
        return allocator;
    }

    /**
     * 等待：
     *   while(条件不满足) {
     *      // wait() 会阻塞当前线程，所以不会一直循环判断条件
     *      wait();
     *   }
     */
    synchronized void apply(Object from, Object to) {
        while (als.contains(from) || als.contains(to)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        als.add(from);
        als.add(to);
    }

    /**
     * 通知：
     *   notify(), notifyAll()
     *   notify() 是会随机地通知等待队列中的一个线程，而 notifyAll() 会通知等待队列中的所有线程
     */
    synchronized void free(Object from, Object to) {
        als.remove(from);
        als.remove(to);
        notifyAll();
    }

}
