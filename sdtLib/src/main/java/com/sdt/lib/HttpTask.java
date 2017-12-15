package com.sdt.lib;

import android.content.Context;

/**
 * Created by Administrator on 2017/12/15.
 */

public class HttpTask implements Runnable {

    private int id;
    private Context context;
    private BaseRequest mRequest;
    private TaskDispatcher dispatcher;
    private HttpCallback callback;
    /**
     * 执行请求的结果
     */
    private boolean mRequestResult;

    public HttpTask(Context context, BaseRequest request, int id) {
        this(context, request, id, null);
    }

    public HttpTask(Context context, BaseRequest request,
                    int id, HttpCallback callback) {
        this.id = id;
        this.context = context;
        this.mRequest = request;
        if (callback == null) {
            this.callback = new HttpCallback.SimpleCallback();
        } else {
            this.callback = callback;
        }
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        mRequestResult = mRequest.send();
        TaskDispatcher.HANDLER.post(r);
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            if (callback == null) {
                return;
            }
            if (mRequest.isAlive()) {
                if (mRequestResult) {
                    callback.onRequestSuccess(id, mRequest);
                } else {
                    callback.onRequestFail(id, mRequest);
                }
            } else {
                callback.onRequestCancel(id);
            }
        }
    };

    public void setDispatcher(TaskDispatcher taskDispatcher) {
        this.dispatcher = dispatcher;
    }

    public void cancel() {
        this.mRequest.cancel();
    }
}
