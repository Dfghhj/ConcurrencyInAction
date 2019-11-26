package top.dfghhj.test.util.atomic;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 9:58
 * @description : 模拟多线程对同一个变量操作,使用原子类的无锁同步方案
 */
public class AtomicCalcTest {

    private AtomicLong aLong = new AtomicLong();

    private void add(int addend){
        for (int i = 0; i < addend; i++) {
            this.aLong.getAndIncrement();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AtomicCalcTest calcTest = new AtomicCalcTest();
        Thread thread1 = new Thread(()->{
            calcTest.add(1000000);
        });
        Thread thread2 = new Thread(()->{
            calcTest.add(1000000);
        });
        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(calcTest.aLong.get());
    }

}
