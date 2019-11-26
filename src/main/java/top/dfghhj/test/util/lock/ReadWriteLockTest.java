package top.dfghhj.test.util.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/21 13:57
 * @description: 读写锁
 */
public class ReadWriteLockTest<K,V> {

    private final Map<K,V> cache = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Lock rLock = lock.readLock();

    private final Lock wLock = lock.writeLock();

    public V get(K key) {
        rLock.lock();
        try {
            return cache.get(key);
        } finally {
            rLock.unlock();
        }
    }

    public V put(K key, V value) {
        wLock.lock();
        try {
            return cache.put(key, value);
        } finally {
            wLock.unlock();
        }
    }

    //按需加载
    public V getOnDemand(K key) {
        V value = null;
        rLock.lock();
        try {
            value = cache.get(key);
        } finally {
            rLock.unlock();
        }
        if (value != null) {
            return value;
        }
        wLock.lock();
        try{
            value = cache.get(key);
            if (value != null) {
                return value;
            }
            // 获取数据，可以是重数据库中
            value = value;
            cache.put(key, value);
        } finally {
            wLock.unlock();
        }
        return value;
    }
}
