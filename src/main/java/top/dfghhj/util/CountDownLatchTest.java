package top.dfghhj.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/21 15:28
 * @description: 计数器，线程等待
 */
public class CountDownLatchTest {

    private final Executor executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        CountDownLatchTest task = new CountDownLatchTest();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        task.executor.execute(() -> {
            System.out.println("开始任务1...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务1结束...");
            countDownLatch.countDown();
        });
        task.executor.execute(() -> {
            System.out.println("开始任务2...");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2结束...");
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("任务结束...");
    }
}
