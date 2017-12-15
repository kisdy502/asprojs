package com.sdt.lib;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.squareup.okhttp.OkHttpClient;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务分派
 * Created by Administrator on 2017/12/15.
 */

public class TaskDispatcher {

    private static final int DEFAULT_THREAD_COUNT = 2;
    static final Handler HANDLER = new Handler(Looper.getMainLooper());
    protected ThreadPoolExecutor threadPool;

    private static TaskDispatcher INSTANCE = null;

    public static TaskDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized (TaskDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * http请求任务集合
     */
    private SparseArray<HttpTask> taskMap;

    private TaskDispatcher() {
        threadPool = new ThreadPoolExecutor(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        taskMap = new SparseArray<HttpTask>();
    }

    public void dispatch(HttpTask task) {
        synchronized (taskMap) {
            task.setDispatcher(this);
            taskMap.put(task.getId(), task);
            if (threadPool == null) {
                threadPool = new ThreadPoolExecutor(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0,
                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            }
        }
        threadPool.execute(task);
    }

    public void cancel(int id) {
        HttpTask tsk = get(id);
        if (tsk != null) {
            tsk.cancel();
        }
    }

    /**
     * 考虑到多线程，并发，需要锁定taskMap
     *
     * @param id
     * @return
     */
    public HttpTask get(int id) {
        HttpTask tsk = null;
        synchronized (taskMap) {
            tsk = taskMap.get(id);
        }
        return tsk;
    }
}
