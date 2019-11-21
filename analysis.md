###1.[累加的问题](src/main/java/top/dfghhj/basic/CalcTest.java)  
- 读取缓存中的值来进行计算，导致多cpu的情况下，各cpu缓存中的数值和内存中的数值不是实时同步的；  
- 多线程同时对共享变量进行计算的时候，线程切换会导致操作结果被覆盖的情况  
(A线程读到i=1，切换到B线程也读到i=1，B线程执行i++,切换回A线程执行i++，最后i=2);  
- ps: 对共享变量加volatile只能解决上诉第一点的问题（可见性），不能解决第二点的问题（原子性）  

###2.[双重校验单例模式](src/main/java/top/dfghhj/basic/SingletonTest.java)
- （1）处的代码会被优化成“a.分配一块内存 M, b.将 M 的地址赋值给 instance 变量, c.最后在内存 M 上初始化 Singleton 对象”  
    导致A线程执行完b后线程切换到B线程，B线程判断instance!=null,就返回了instance对象，然而这时候instance内部的初始化并没有完成，就会导致NPE  

###3.[转账](src/main/java/top/dfghhj/basic/accountTest/AccountTest1.java)
- 模拟了并发场景下对同一个变量进行加减的情况，最后的结果总是错误的。
  有可见性，原子性的问题。
  
###3.[转账--死锁](src/main/java/top/dfghhj/basic/accountTest/AccountTest2.java)
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

###4.[Lock](src/main/java/top/dfghhj/util/lock/LockTest.java)
Lock工具类相较于synchronized，能够响应中断，支持超时，非阻塞地获取锁。

###5.[Condition](src/main/java/top/dfghhj/util/lock/ConditionTest.java)
Condition工具类实现了管程模型里面的条件变量。

###6.[Semaphore](src/main/java/top/dfghhj/util/SemaphoreTest.java)
信号量模型：一个计数器，一个等待队列，三个方法（init()、down() 和 up()）。  
Semaphore允许多个线程访问临界区。可以用来实现各种池。



