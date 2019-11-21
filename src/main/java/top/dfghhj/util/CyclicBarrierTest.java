package top.dfghhj.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/21 15:28
 * @description: 可以重置的同步工具
 */
public class CyclicBarrierTest {

    private final Executor executor1 = Executors.newFixedThreadPool(1);

    private final Executor executor2 = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        CyclicBarrierTest cyclicBarrierTest = new CyclicBarrierTest();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2, () -> {cyclicBarrierTest.executor1.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务结束。开始新任务...");
        });});
        cyclicBarrierTest.executor2.execute(() -> {
            while (true) {
                System.out.println("开始任务1...");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务1结束...");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
        cyclicBarrierTest.executor2.execute(() -> {
            while (true) {
                System.out.println("开始任务2...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("任务2结束...");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
