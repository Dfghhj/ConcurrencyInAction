package top.dfghhj.util.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/20 15:23
 * @description:
 */
public class LockTest {

//    //公平锁
//    private final Lock lock = new ReentrantLock(true);
    private final Lock lock = new ReentrantLock();

    private int count = 0;

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 可重入锁：线程可以重复获取同一把锁
     */
    public void addOne() {
        lock.lock();
        try {
            count = getCount() + 1;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LockTest lockTest = new LockTest();
        Thread t1 = new Thread(()->{
            for (int i = 0; i < 100000; i++) {
                lockTest.addOne();
            }
        });
        Thread t2 = new Thread(()->{
            for (int i = 0; i < 100000; i++) {
                lockTest.addOne();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(lockTest.getCount());
    }

}


