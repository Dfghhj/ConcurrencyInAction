package top.dfghhj.basic;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 13:23
 * @description: 单例模式中的指令重排
 *  javap -verbose -p .\SingletonTest.class
 */
public class SingletonTest {

//    private volatile static SingletonTest singletonTest;
    private static SingletonTest singletonTest;

    public static SingletonTest getInstance() {
        if (singletonTest == null) {
            synchronized (SingletonTest.class) {
                if (singletonTest == null) {
                    singletonTest = new SingletonTest();//1
                }
            }
        }
        return singletonTest;
    }
}
