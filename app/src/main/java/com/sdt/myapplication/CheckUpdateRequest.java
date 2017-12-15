package com.sdt.myapplication;


import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.google.gson.Gson;
import com.sdt.lib.BaseRequest;
import com.sdt.lib.IoUtil;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/12/15.
 */
public class CheckUpdateRequest extends BaseRequest<UpdateBean> {

    public CheckUpdateRequest(Context context) {
        super(context);
    }

    @Override
    protected ArrayMap<String, String> getHeaders() {
        return null;
    }

    @Override
    protected String getUrl() {
        //偷懒了，其实后面的参数，应该卸载 appendUrlSegment方法里面
        return "http://ad.mipt.cn:7855/oms/client/checkUpdate.action?&channelCode=test&modelId=H4S02&mac=74:ff:4c:e3:5c:e9&pkgName=cn.mipt.ad&version=10209%E8%BF%99%E4%B8%AA%E6%98%AF%E5%8D%87%E7%BA%A7";
    }

    @Override
    protected RequestType getMethod() {
        return RequestType.GET;
    }

    @Override
    protected ArrayMap<String, String> appendUrlSegment() {
        return null;
    }

    @Override
    protected UpdateBean getT(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        UpdateBean bean = new Gson().fromJson(reader, UpdateBean.class);
        IoUtil.silentClose(reader);
        IoUtil.silentClose(is);
        return bean;
    }

}
