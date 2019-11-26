package top.dfghhj.test.basic.accountTest;

import com.alibaba.fastjson.JSONObject;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 16:23
 * @description: 如果transfer不加锁，会有原子性问题
 */
public class AccountTest1 {

    public static void main(String[] args) throws InterruptedException {
        Account1 a = new Account1(200000);
        Account1 b = new Account1(200000);
        Account1 c = new Account1(200000);
        Thread aT = new Thread(()-> {
            for (int i = 0; i < 100000; i++) {
                a.transfer(b, 1);
            }
        });
        Thread bT = new Thread(()-> {
            for (int i = 0; i < 100000; i++) {
                b.transfer(c, 1);
            }
        });
        Thread cT = new Thread(()-> {
            for (int i = 0; i < 100000; i++) {
                c.transfer(a, 1);
            }
        });
        aT.start();
        bT.start();
        cT.start();

        aT.join();
        bT.join();
        cT.join();

        System.out.println(JSONObject.toJSONString(a));
        System.out.println(JSONObject.toJSONString(b));
        System.out.println(JSONObject.toJSONString(c));
    }

}

class Account1 {

    private int balance;

    public Account1(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return this.balance;
    }

    public void transfer(Account1 target, int amt) {
        //加类锁可以同时保护对this和target的操作
        synchronized(Account1.class) {
            if (this.balance >= amt) {
                this.balance -= amt;
                target.balance += amt;
            }
        }
    }

}
