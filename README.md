###1.[累加的问题](src/main/java/top/dfghhj/basic/CalcTest.java)  
- 读取缓存中的值来进行计算，导致多cpu的情况下，各cpu缓存中的数值和内存中的数值不是实时同步的；  
- 多线程同时对共享变量进行计算的时候，线程切换会导致操作结果被覆盖的情况  
(A线程读到i=1，切换到B线程也读到i=1，B线程执行i++,切换回A线程执行i++，最后i=2);  
- ps: 对共享变量加volatile只能解决上诉第一点的问题（可见性），不能解决第二点的问题（原子性）  

###2.[双重校验单例模式](src/main/java/top/dfghhj/basic/SingletonTest.java)
- （1）处的代码会被优化成“a.分配一块内存 M, b.将 M 的地址赋值给 instance 变量, c.最后在内存 M 上初始化 Singleton 对象”  
    导致A线程执行完b后线程切换到B线程，B线程判断instance!=null,就返回了instance对象，然而这时候instance内部的初始化并没有完成，就会导致NPE  

###3.