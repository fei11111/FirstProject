package com.fei.firstproject.utils;


/**
 * @author huangwz
 * @des 线程任务的工厂类
 */
public class ThreadPoolFactory {

    /**
     * 提交任务超时延时时间 10分钟
     */
    private static int timeout = 1000 * 60 * 5;

    static ThreadPoolProxy mNormalPool;
    static ThreadPoolProxy mDownLoadPool;
    static ThreadPoolProxy mNetStatusPool;

    /**
     * 得到一个普通的线程池(5个线程)
     */
    public static ThreadPoolProxy getNormalPool() {
        if (mNormalPool == null) {
            synchronized (ThreadPoolProxy.class) {
                mNormalPool = new ThreadPoolProxy(5, 5, 3000);
            }
        }
        return mNormalPool;
    }

    /**
     * 得到一个下载的线程池(3个线程)
     */
    public static ThreadPoolProxy getDownLoadPool() {
        if (mDownLoadPool == null) {
            synchronized (ThreadPoolProxy.class) {
                mDownLoadPool = new ThreadPoolProxy(3, 3, 3000);
            }
        }
        return mDownLoadPool;
    }
}
