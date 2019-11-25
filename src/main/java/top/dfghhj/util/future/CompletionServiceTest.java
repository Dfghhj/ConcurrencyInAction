package top.dfghhj.util.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/25 11:26
 * @description: 批量执行
 */
public class CompletionServiceTest {

    public static void main(String[] args) {
        System.out.println(geocoder());
    }

    private static Integer geocoder() {
        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(3);
        // 创建CompletionService
        CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
        // 用于保存Future对象
        List<Future<Integer>> futures = new ArrayList<>(3);
        //提交异步任务，并保存future到futures
        futures.add(cs.submit(CompletionServiceTest::geocoderByS1));
        futures.add(cs.submit(CompletionServiceTest::geocoderByS2));
        futures.add(cs.submit(CompletionServiceTest::geocoderByS3));
        // 获取最快返回的任务执行结果
        Integer r = 0;
        try {
            // 只要有一个成功返回，则break
            for (int i = 0; i < 3; ++i) {
                try {
                    r = cs.take().get();
                } catch (InterruptedException | ExecutionException ignore) {}
                //简单地通过判空来检查是否成功返回
                if (r != null) {
                    break;
                }
            }
        } finally {
            //取消所有任务
            for(Future<Integer> f : futures) {
                f.cancel(true);
            }
        }
        // 返回结果
        return r;
    }

    private static Integer geocoderByS1() {return 1;}
    private static Integer geocoderByS2() {return 2;}
    private static Integer geocoderByS3() {return 3;}

}
