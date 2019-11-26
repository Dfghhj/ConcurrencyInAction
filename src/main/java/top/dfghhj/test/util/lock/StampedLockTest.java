package top.dfghhj.test.util.lock;

import java.util.concurrent.locks.StampedLock;

/**
 * @author ：feifeng
 * @date ：Created in 2019/11/21 13:57
 * @description:  StampedLock 官方示例
 */
public class StampedLockTest {

    private double x, y;

    private final StampedLock sl = new StampedLock();

    void move(double deltaX, double deltaY) { // an exclusively locked method
      long stamp = sl.writeLock();
      try {
        x += deltaX;
        y += deltaY;
      } finally {
        sl.unlockWrite(stamp);
      }
    }
 
    double distanceFromOrigin() { // A read-only method
      long stamp = sl.tryOptimisticRead();
      double currentX = x, currentY = y;
      if (!sl.validate(stamp)) {
         stamp = sl.readLock();
         try {
           currentX = x;
           currentY = y;
         } finally {
            sl.unlockRead(stamp);
         }
      }
      return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    /**
     * 支持锁的升降级
     *  降级（通过 tryConvertToReadLock() 方法实现）和升级（通过 tryConvertToWriteLock() 方法实现）
     */
    void moveIfAtOrigin(double newX, double newY) { // upgrade
      // Could instead start with optimistic, not read mode
      long stamp = sl.readLock();
      try {
        while (x == 0.0 && y == 0.0) {
          long ws = sl.tryConvertToWriteLock(stamp);
          if (ws != 0L) {
            stamp = ws;
            x = newX;
            y = newY;
            break;
          } else {
            sl.unlockRead(stamp);
            stamp = sl.writeLock();
          }
        }
      } finally {
        sl.unlock(stamp);
      }
    }
    
}
