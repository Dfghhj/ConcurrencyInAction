package top.dfghhj.test.util.lock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/20 15:46
 * @description:
 */
public class ConditionTest {

    private final Lock lock = new ReentrantLock();

    private final Condition notEmpty = lock.newCondition();

    private List<String> list = new ArrayList<>();

    public ConditionTest(int size) {
        for (int i = 0; i < size; i++) {
            list.add("pool"+i);
        }
    }

    String getOne() {
        lock.lock();
        try {
            while (list.size() == 0) {
                notEmpty.await();
            }
            return list.get(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    void free(String pool) {
        lock.lock();
        try {
            list.add(pool);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

    }

}
