package com.sdt.lib;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 在原生的java HttpUrlConnection基础上封装的请求框架
 * Created by Administrator on 2017/12/15.
 */

public abstract class BaseRequest<T> {

    public final static int STATUS_OK = 200;

    public final static int STATUS_NETWORK_ERROR = 2;
    public final static int STATUS_IO_ERROR = 2 << 1;
    public final static int STATUS_INTERRUPTED_ERROR = 2 << 2;
    public final static int STATUS_PASRESTREAM_ERROR = 2 << 3;
    public final static int STATUS_URL_ERROR = 2 << 4;
    private static final String TAG = "BaseRequest";

    public static enum RequestType {
        GET, POST
    }

    public int getStatusCode() {
        return statusCode;
    }

    private int statusCode;
    private boolean isCancel = false;       //true 是否请求取消了请求  false未取消
    protected String mRequestUrl;
    protected RequestType requestMethod;
    protected Context context;
    protected int retryTimes = InternalUtils.DEF_HTTP_RETRY_TIMES;
    protected TaskDispatcher dispatcher;

    protected ArrayMap<String, String> customCommonHeaders;  //公共头
    protected ArrayMap<String, String> customPathSegments;   //公共参数

    protected ArrayMap<String, String> headers;              //业务请求需要头参数
    protected ArrayMap<String, String> pathSegments;         //各自业务请求的业务参数

    T data; //请求返回的数据

    public T getResultData() {
        return data;
    }

    public BaseRequest(Context context) {
        this.context = context;
        this.retryTimes = InternalUtils.DEF_HTTP_RETRY_TIMES;
    }


    public boolean send() {
        boolean ret = false;
        HttpURLConnection connection = null;
        InputStream is = null;
        requestMethod = getMethod();
        boolean success = configUrl();
        if (success) {
            try {
                for (int i = 0; !isCancel && is == null && i < retryTimes; i++) {
                    connection = openConnection();
                    setCommonHeaders(connection);
                    setAdditonalHeaders(connection);
                    switch (requestMethod) {
                        case GET:
                            connection.setRequestMethod(InternalUtils.REQUEST_METHOD_GET);
                            break;
                        case POST:
                            connection.setRequestMethod(InternalUtils.REQUEST_METHOD_POST);
                            connection.setDoOutput(true);
                            flushPostData(connection);
                            break;
                        default:
                            if (BuildConfig.DEBUG) {
                                Log.e(TAG, "unsupport, request method[" + requestMethod + "]!");
                            }
                            break;
                    }
                    int statusCode = connection.getResponseCode();
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "statusCode : " + statusCode);
                    }
                    is = connection.getInputStream();
                    if (is == null) {
                        Thread.sleep(1000);
                        continue;
                    }
                }
                if (is != null) {
                    ret = parseResponse(is);
                    if (ret) {
                        ret = doExtraJob();
                    }
                } else {
                    statusCode = STATUS_NETWORK_ERROR;
                }
            } catch (IOException e) {
                statusCode = STATUS_IO_ERROR;
                e.printStackTrace();
            } catch (InterruptedException e) {
                statusCode = STATUS_INTERRUPTED_ERROR;
                e.printStackTrace();
            } catch (Exception e) {
                statusCode = STATUS_PASRESTREAM_ERROR;
                e.printStackTrace();
            }
        } else {
            statusCode = STATUS_URL_ERROR;
        }
        return ret;
    }

    private HttpURLConnection openConnection() throws IOException {
        URL url = new URL(mRequestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(InternalUtils.DEFAULT_CONNECT_TIMEOUT);
        connection.setReadTimeout(InternalUtils.DEFAULT_READ_TIMEOUT);
        connection.setDoInput(true);
        return connection;
    }

    private void flushPostData(HttpURLConnection conn) throws IOException {
        byte[] data = composePostData();
        if (data == null || data.length <= 0) {
            return;
        }
        conn.addRequestProperty(InternalUtils.HEADER_CONTENT_LENGTH, String.valueOf(data.length));
        OutputStream os = conn.getOutputStream();
        if (os == null) {
            return;
        }
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(data);
            byte[] buffer = new byte[2048];
            int count = -1;
            while ((count = bais.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            os.flush();
        } finally {
            IoUtil.silentClose(bais);
            IoUtil.silentClose(os);
        }
    }

    /**
     * 根据具体业务去添加 在BaseRequest中 customCommonHeaders 为空
     *
     * @param connection
     */
    private void setCommonHeaders(HttpURLConnection connection) {
        if (customCommonHeaders == null) return;
        Set<Map.Entry<String, String>> entrys = customCommonHeaders.entrySet();
        Iterator<Map.Entry<String, String>> it = entrys.iterator();
        Map.Entry<String, String> entry = null;
        String key, value;
        while (it.hasNext()) {
            entry = it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                continue;
            }
            connection.addRequestProperty(key, value);
        }
    }

    private void setAdditonalHeaders(HttpURLConnection connection) {
        if (headers == null || headers.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, String>> entrys = headers.entrySet();
        Iterator<Map.Entry<String, String>> it = entrys.iterator();
        Map.Entry<String, String> entry = null;
        String key, value;
        while (it.hasNext()) {
            entry = it.next();
            key = entry.getKey();
            value = entry.getValue();
            if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                continue;
            }
            if (connection.getHeaderFields().containsKey(key)) {
                break;
//                connection.setRequestProperty(key,value);   //看采取什么策略 ，是否允许覆盖父类的header
            }
            connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private boolean configUrl() {
        mRequestUrl = getUrl();
        if (isRequestUrlIllegal()) {
            return false;
        }
        pathSegments = appendUrlSegment();
        headers = getHeaders();
        mRequestUrl = UrlJoinUtils.joinUrlAndParams(mRequestUrl, pathSegments);
        mRequestUrl = UrlJoinUtils.joinUrlAndParams(mRequestUrl, customPathSegments);
        Log.i(TAG, "real url: " + mRequestUrl);  //真正请求所带的url
        return true;
    }

    private boolean isRequestUrlIllegal() {
        if (mRequestUrl == null || mRequestUrl.trim().length() < 0) {
            return true;
        }
        return false;
    }

    public void cancel() {
        isCancel = true;
    }

    /**
     * 请求是否被取消了
     *
     * @return
     */
    public boolean isAlive() {
        return !isCancel;
    }

    protected abstract ArrayMap<String, String> getHeaders();

    protected abstract String getUrl();

    protected abstract RequestType getMethod();

    protected abstract ArrayMap<String, String> appendUrlSegment();

    /**
     * implemented when method is post
     *
     * @return entity for post
     */
    protected byte[] composePostData() {
        return null;
    }

    protected boolean parseResponse(InputStream is) throws Exception {
        data = getT(is);
        return data != null;
    }

    protected abstract T getT(InputStream is) throws Exception;

    /**
     * 请求结束后如果需要做其他的耗时操作,重写该方法
     *
     * @return
     */
    protected boolean doExtraJob() {
        return true;
    }
}
