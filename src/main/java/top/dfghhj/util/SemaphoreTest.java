package top.dfghhj.util;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/20 16:46
 * @description: 信号量
 */
public class SemaphoreTest<T, R> {

    private final List<T> pool;

    private final Semaphore semaphore;

    public SemaphoreTest(int size, T t) {
        semaphore = new Semaphore(size);
        pool = new Vector<>();
        for (int i = 0; i < size; i++) {
            pool.add(t);
        }
    }

    R exec(Function<T,R> func) throws InterruptedException {
        T t = null;
        semaphore.acquire();
        try {
            t = pool.remove(0);
            return func.apply(t);
        } finally {
            pool.add(t);
            semaphore.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SemaphoreTest pool = new SemaphoreTest<Long, String>(10, 2L);
        pool.exec(t -> {    System.out.println(t);    return t.toString();});
    }

}
