package top.dfghhj.basic.accountTest;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/19 16:23
 * @description:
 * 加类锁的效率太低，类似转账操作，同时有成千上万的转账操作的话，加类锁同一时间只能有一个转账操作执行
 * 所以需要更加细粒度的锁来解决
 * 但是细粒度锁容易发生“死锁”
 *
 */
public class AccountTest2 {

    public static void main(String[] args) throws InterruptedException {
        Account2 a = new Account2(1,200000);
        Account2 b = new Account2(2,200000);
        Thread aT = new Thread(()-> {
            for (int i = 0; i < 100000; i++) {
                a.transfer(b, 1);
            }
        });
        Thread bT = new Thread(()-> {
            for (int i = 0; i < 100000; i++) {
                b.transfer(a, 1);
            }
        });

        aT.start();
        bT.start();

        aT.join();
        bT.join();

        System.out.println(JSONObject.toJSONString(a));
        System.out.println(JSONObject.toJSONString(b));
    }

}

class Account2 {

    private int id;

    private int balance;

    private Allocator allocator = Allocator.getInstance();

    private Allocator2 allocator2 = Allocator2.getInstance();

    private final Lock lock = new ReentrantLock();

    Account2(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getBalance() {
        return this.balance;
    }

    void transfer(Account2 target, int amt) {
        this.transfer1(target, amt);
    }

    /**
     * 会死锁 ！！
     * 查看堆栈，可以看到线程互相等待获取对方持有的锁
     */
    private void transfer1(Account2 target, int amt) {
        synchronized(this) {
            synchronized (target) {
                if (this.balance >= amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }

    /**
     * 1.同时申请this和target的锁
     * (破坏占用且等待条件)
     */
    private void transfer2(Account2 target, int amt) {
        while (!allocator.apply(this, target));
        try {
            synchronized (this) {
                synchronized (target) {
                    if (this.balance >= amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            allocator.free(this, target);
        }
    }

    /**
     * 1.1同时申请this和target的锁
     * (破坏占用且等待条件)
     *  (1)中‘while (!allocator.apply(this, target));’如果申请不到会一直死循环，如果apply耗时长或者并发冲突量大时，可能需要循环上万次才会获取到锁，非常消耗cpu
     *  等待-通知 wait(),notify(),notifyAll()
     */
    private void transfer2_1(Account2 target, int amt) {
        allocator2.apply(this, target);
        try {
            synchronized (this) {
                synchronized (target) {
                    if (this.balance >= amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        } finally {
            allocator2.free(this, target);
        }
    }

    /**
     * 2.如果不能获加锁，就主动释放它占有的资源
     * （破坏不可抢占条件）
     */
    private void transfer3(Account2 target, int amt) {
        while (true) {
            if (this.lock.tryLock()){
                try {
                    if (target.lock.tryLock()) {
                        try {
                            if (this.balance >= amt) {
                                this.balance -= amt;
                                target.balance += amt;
                                break;
                            }
                        } finally {
                            target.lock.unlock();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        }
    }

    /**
     * 3.按顺序申请this和target的锁
     * （破坏循环等待条件）
     */
    private void transfer4(Account2 target, int amt) {
        Account2 left = this;
        Account2 right = target;
        if (this.id > target.id) {
            left = target;
            right = this;
        }
        synchronized(left) {
            synchronized (right) {
                if (this.balance >= amt) {
                    this.balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }

}
