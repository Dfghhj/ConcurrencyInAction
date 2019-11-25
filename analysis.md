### 1.[累加的问题](src/main/java/top/dfghhj/basic/CalcTest.java)  
- 读取缓存中的值来进行计算，导致多cpu的情况下，各cpu缓存中的数值和内存中的数值不是实时同步的；  
- 多线程同时对共享变量进行计算的时候，线程切换会导致操作结果被覆盖的情况  
(A线程读到i=1，切换到B线程也读到i=1，B线程执行i++,切换回A线程执行i++，最后i=2);  
- ps: 对共享变量加volatile只能解决上诉第一点的问题（可见性），不能解决第二点的问题（原子性）  

### 2.[双重校验单例模式](src/main/java/top/dfghhj/basic/SingletonTest.java)
- （1）处的代码会被优化成“a.分配一块内存 M, b.将 M 的地址赋值给 instance 变量, c.最后在内存 M 上初始化 Singleton 对象”  
    导致A线程执行完b后线程切换到B线程，B线程判断instance!=null,就返回了instance对象，然而这时候instance内部的初始化并没有完成，就会导致NPE  

### 3.[转账](src/main/java/top/dfghhj/basic/accountTest/AccountTest1.java)
- 模拟了并发场景下对同一个变量进行加减的情况，最后的结果总是错误的。
  有可见性，原子性的问题。
  
### 4.[转账--死锁](src/main/java/top/dfghhj/basic/accountTest/AccountTest2.java)
- 还是通过转账场景模拟死锁。
- transfer1:并发情况下会出现死锁的情况，互相持有对方等待的锁，查看堆栈：
```
"Thread-1" #13 prio=5 os_prio=0 tid=0x000000002023d000 nid=0x5a90 waiting for monitor entry [0x0000000020baf000]
   java.lang.Thread.State: BLOCKED (on object monitor)
	at top.dfghhj.basic.accountTest.Account2.transfer1(AccountTest2.java:77)
	- waiting to lock <0x000000076bd18820> (a top.dfghhj.basic.accountTest.Account2)
	- locked <0x000000076bd24970> (a top.dfghhj.basic.accountTest.Account2)
    ...

"Thread-0" #12 prio=5 os_prio=0 tid=0x000000002023c000 nid=0x5b10 waiting for monitor entry [0x0000000020aae000]
   java.lang.Thread.State: BLOCKED (on object monitor)
	at top.dfghhj.basic.accountTest.Account2.transfer1(AccountTest2.java:77)
	- waiting to lock <0x000000076bd24970> (a top.dfghhj.basic.accountTest.Account2)
	- locked <0x000000076bd18820> (a top.dfghhj.basic.accountTest.Account2)
	...
```
- transfer2，transfer2_1：破坏死锁条件之一：占用且等待  
同时申请this和target的锁
- transfer3：破坏死锁条件之一：不可抢占  
如果不能获加锁，就主动释放它占有的资源
- transfer4：破坏死锁条件之一：循环等待  
按顺序申请this和target的锁

### 5.[Lock](src/main/java/top/dfghhj/util/lock/LockTest.java)
Lock工具类相较于synchronized，能够响应中断，支持超时，非阻塞地获取锁。

### 6.[Condition](src/main/java/top/dfghhj/util/lock/ConditionTest.java)
Condition工具类实现了管程模型里面的条件变量。

### 7.[Semaphore](src/main/java/top/dfghhj/util/SemaphoreTest.java)
信号量模型：一个计数器，一个等待队列，三个方法（init()、down() 和 up()）。  
Semaphore允许多个线程访问临界区。可以用来实现各种池。

### 8.[ReadWriteLock](src/main/java/top/dfghhj/util/lock/ReadWriteLockTest.java)
读写锁适合读多写少的场景，比如缓存。  
读写锁，允许多个线程同时获取读锁，只能一个线程获取写锁。  
读锁和写锁互斥。  
获取读锁后，未释放前不能再获取写锁，写锁会一直等待；（不允许锁的升级）  
获取写锁后，未释放前可以再获取读锁。（允许锁的降级）

### 9.[StampedLock](src/main/java/top/dfghhj/util/lock/StampedLockTest.java)
StampedLock 支持三种模式：写锁、悲观读锁和乐观读。  
写锁、悲观读锁类似于ReadWriteLock的写锁和读锁；  
乐观读是无锁的读，通过tryOptimisticRead()来返回stamp，因为无锁，所以获取来返回stamp到读的过程中，数据可能被其他线程改了，所以需要配合validate(stamp)来使用，验证不通过就考虑升级成读锁。  
乐观读和数据库的乐观锁有异曲同工之妙。  
StampedLock 的功能仅仅是 ReadWriteLock 的子集！  
不支持重入；  
不支持条件变量；  
线程阻塞在 StampedLock 的 readLock() 或者 writeLock() 上时，此时调用该阻塞线程的 interrupt() 方法，会导致 CPU 飙升。
```
final StampedLock sl = new StampedLock();

// 乐观读
long stamp = sl.tryOptimisticRead();
// 读入方法局部变量
......
// 校验stamp
if (!sl.validate(stamp)){
  // 升级为悲观读锁
  stamp = sl.readLock();
  try {
    // 读入方法局部变量
    .....
  } finally {
    //释放悲观读锁
    sl.unlockRead(stamp);
  }
}
//使用方法局部变量执行业务操作
......

long stamp = sl.writeLock();
try {
  // 写共享变量
  ......
} finally {
  sl.unlockWrite(stamp);
}
```

### 10.[CountDownLatch](src/main/java/top/dfghhj/util/CountDownLatchTest.java)
使用线程池的情况下，无法使用join来等待线程完成，这时候就需要CountDownLatch了。  
创建CountDownLatch的时候初始化了一个计数器，线程完成时countDown()会使计数器减1。  
await()会阻塞调用线程等到CountDownLatch计数器减至0。

### 11.[CyclicBarrier](src/main/java/top/dfghhj/util/CyclicBarrierTest.java)
CyclicBarrier相较于CountDownLatch可以自动重置。  
CyclicBarrier的await()会使计数器减1，当减至0的时候，会自动重置。 
初始化的时候可以设置一个回调函数，用来执行减至0的时候的后续操作。  
这个操作建议用线程池去执行，因为如果不设置，这个后续操作将在把计数器减至0的线程中执行。

### 12.[Atomic](src/main/java/top/dfghhj/util/atomic/AtomicCalcTest.java)
原子类(Atomic)，是利用了Cpu指令-CAS指令(Copy and Swap, 比较并交换)， 指令本身是能够保证原子性的。  
注意ABA问题，可以添加版本号解决。

### 13.[ThreadPool](src/main/java/top/dfghhj/util/future/MyThreadPool.java)
实现了简化的线程池，说明工作原理。Java 提供的线程池相关的工具类中，最核心的是：  
```
ThreadPoolExecutor(
  int corePoolSize, //最小线程数
  int maximumPoolSize, //最大线程数
  long keepAliveTime, //线程空闲了keepAliveTime & unit，且当前线程数大于corePoolSize，就要被回收了
  TimeUnit unit,
  BlockingQueue<Runnable> workQueue, //工作队列
  ThreadFactory threadFactory, //自定义如何创建线程
  RejectedExecutionHandler handler) //自定义任务的拒绝策略
```
ThreadPoolExecutor 已经提供了以下 4 种策略。  
- CallerRunsPolicy：提交任务的线程自己去执行该任务。  
- AbortPolicy：默认的拒绝策略，会 throws RejectedExecutionException。  
- DiscardPolicy：直接丢弃任务，没有任何异常抛出。  
- DiscardOldestPolicy：丢弃最老的任务，其实就是把最早进入工作队列的任务丢弃，然后把新任务加入到工作队列。  

不建议使用Executors，提供的很多方法默认使用的都是无界的 LinkedBlockingQueue，高负载下容易oom。  
强烈建议使用有界队列。  


