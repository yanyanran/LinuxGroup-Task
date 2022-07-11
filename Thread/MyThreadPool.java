import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

//final --> 不能被修改

public class MyThreadPool {
    private volatile ReentrantLock lock = new ReentrantLock();// ReentrantLock锁
    private volatile ArrayBlockingQueue<Runnable> queue = new BlockingQueueWithLock<Runnable>(10);// 任务阻塞队列
    private volatile ArrayList<Thread> threads = new ArrayList<>();// 可以动态修改的数组存储线程
    private volatile HashSet<MyThreadPool.Running> runningSet = new HashSet<>();// 不重复的运行集

    private volatile int poolSize;  // 当前运行线程数
    private volatile int coreSize;  // 核心线程数(min/基本)
    private volatile int maxSize;  // 最大线程数(max)
    //private final int timeOut;
    private volatile boolean RUNNING = true;// 是否正在运行
    private volatile boolean SHUTDOWN = false;// 是否停止工作

    // MyThreadPool(int corePoolSize, int maxPoolSize, int timeout)
    public MyThreadPool(int corePoolSize, int maxPoolSize) {
        ExecutorService ex = Executors.newFixedThreadPool(10);
        int num = ((ThreadPoolExecutor)ex).getActiveCount();  // 获取当前运行线程数

        this.coreSize = corePoolSize;
        this.maxSize = maxPoolSize;
        //this.timeOut = timeout;
        this.poolSize = 1;  // null error

        queue = new ArrayBlockingQueue<Runnable>(poolSize);

    }

    //class Running
    public final class Running implements Runnable {
        public Running(Runnable task) {
            queue.offer(task);  // 队尾添加
        }

        @Override
        public void run() {
            while (RUNNING) {
                if (SHUTDOWN) {
                    Thread.interrupted();
                }
                try {
                     Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // execute(Runnable task)
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException();
        }

        if (poolSize < maxSize || poolSize >= coreSize) { // 当前线程数>=基本大小 且任务队列未满时
            lock.lock();
            try {
                poolSize++;
                MyThreadPool.Running r = new MyThreadPool.Running(task); // 提交到阻塞队列排队等候处理
                runningSet.add(r);
                Thread thread = new Thread(r);
                threads.add(thread); // 新增线程处理
                thread.start();
            } finally {
                lock.unlock();
            }
        } else {
            try {
                queue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // shutdown()
    public void shutdown() {
        RUNNING = false;
        if (!runningSet.isEmpty()) {
            for (MyThreadPool.Running r : runningSet) {  // 遍历运行集
                for (Thread thread : threads) {  // 遍历线程
                    System.out.println(thread.getName() + " interrupt！\n");
                    thread.interrupt();
                }
            }
        }
        SHUTDOWN = true;
        Thread.currentThread().interrupt();
    }

    // Main Test
    public static void main(String[] args) throws IllegalArgumentException {
        MyThreadPool ex = new MyThreadPool(2, 5);
        for (int i = 2; i < 5; i++) {
            ex.execute(() ->
                    System.out.println("Thread " + Thread.currentThread().getName() + " still working!\n"));
        }
        ex.shutdown();
    }
}

/**
 * IllegalArgumentException 方法收到非法参数
 * */

/*
Thread-0 interrupt！

Thread-1 interrupt！

Thread-2 interrupt！

Thread-0 interrupt！

Thread-1 interrupt！

Thread-2 interrupt！

Thread-0 interrupt！

Thread-1 interrupt！

Thread-2 interrupt！

Thread Thread-1 still working!

Thread Thread-0 still working!
* */