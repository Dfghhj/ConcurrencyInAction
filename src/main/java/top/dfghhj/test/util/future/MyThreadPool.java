package top.dfghhj.test.util.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/22 15:19
 * @description: 简化的线程池，仅用来说明工作原理
 */
public class MyThreadPool {
    /**
     * 利用阻塞队列实现生产者-消费者模式
     */
    private BlockingQueue<Runnable> workQueue;
    /**
     * 保存内部工作线程
     */
    private List<WorkerThread> threads = new ArrayList<>();

    public MyThreadPool(int poolSize, BlockingQueue<Runnable> workQueue){
        this.workQueue = workQueue;
        // 创建工作线程
        for(int idx=0; idx<poolSize; idx++){
            WorkerThread work = new WorkerThread();
            work.start();
            threads.add(work);
        }
    }
    // 提交任务
    public void execute(Runnable command) throws InterruptedException {
        workQueue.put(command);
    }

    /**
     * 工作线程负责消费任务，并执行任务
     */
    class WorkerThread extends Thread{
        @Override
        public void run() {
            //循环取任务并执行
            while(true){
                Runnable task = null;
                try {
                    task = workQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 下面是使用示例 **/
    public static void main(String[] args) throws InterruptedException {
        // 创建有界阻塞队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2);
        // 创建线程池
        MyThreadPool pool = new MyThreadPool(10, workQueue);
        // 提交任务
        pool.execute(()->{
            System.out.println("hello");
        });
    }
}

