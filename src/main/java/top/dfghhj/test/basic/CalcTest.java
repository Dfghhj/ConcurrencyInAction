package top.dfghhj.test.basic;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 9:58
 * @description : 模拟多线程对同一个变量操作
 */
public class CalcTest {

    private int a = 0;

//    private synchronized void addOne() {
    private void addOne() {
        this.a++;
    }

    private void add(int addend){
        for (int i = 0; i < addend; i++) {
            this.addOne();
        }
    }

    private int getA() {
        return this.a;
    }

    public static void main(String[] args) throws InterruptedException {
        CalcTest calcTest = new CalcTest();
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

        System.out.println(calcTest.getA());
    }

}
