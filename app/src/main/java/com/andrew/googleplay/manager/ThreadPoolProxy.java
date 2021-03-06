package com.andrew.googleplay.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @项目名: GooglePlay
 * @包名: com.andrew.googleplay.manager
 * @创建者: 谢康
 * @创建时间: 2016/10/14 上午 09:43
 * @描述: 线程池
 * *
 * @svn版本: $Rev$
 * @更新人: $Author$
 * @更新时间: $Date$
 * @更新描述: TODO
 */

public class ThreadPoolProxy {
    private ThreadPoolExecutor mExecutor;//线程池
    private int                mCorePoolSize;
    private int                mMaximumPoolSize;
    private long               mKeepAliveTime;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.mCorePoolSize = corePoolSize;
        this.mMaximumPoolSize = maximumPoolSize;
        this.mKeepAliveTime = keepAliveTime;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void excute(Runnable task) {
        initThreadPoolExecutor();
        //执行线程
        mExecutor.execute(task);

        //工厂类Executors 以ThreadPoolExecutor为基础
        //Executors.newFixedThreadPool();
    }

    public Future<?> submit(Runnable task) {
        initThreadPoolExecutor();
        return mExecutor.submit(task);
    }

    public void remove(Runnable task) {
        if (mExecutor != null) {
            mExecutor.remove(task);
        }
    }

    private synchronized void initThreadPoolExecutor() {
        if (mExecutor == null || mExecutor.isShutdown() || mExecutor.isTerminated()) {
            TimeUnit unit = TimeUnit.MILLISECONDS;
//            BlockingQueue<Runnable>  workQueue       = new ArrayBlockingQueue<Runnable>(10);//阻塞队列 FIFO队列
            BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>();//阻塞队列 FIFO队列
//            BlockingQueue<Runnable>  workQueue       =  new PriorityBlockingQueue<>(initialCapacity,comparator);//阻塞队列 优先级队列
//            BlockingQueue<Runnable>  workQueue       = new SynchronousQueue<>();//阻塞队列 交替队列
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
//            RejectedExecutionHandler handler       = new ThreadPoolExecutor.AbortPolicy();//如果出现错误，则直接抛出异常
//            RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//如果出现错误，直接执行加入的任务
//            RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();//如果出现错误,移除第一个任务,执行加入的任务
            RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();//如果出现错误，不做处理

            mExecutor = new ThreadPoolExecutor(mCorePoolSize,//核心线程数
                                               mMaximumPoolSize,//最大线程数
                                               mKeepAliveTime,//额外线程空闲时保持的时间长度
                                               unit,//keepAliveTime的单位
                                               workQueue,//任务队列
                                               threadFactory,//线程工厂
                                               handler);//错误的捕获器 任务队列添加异常的捕捉器
        }
    }
}
