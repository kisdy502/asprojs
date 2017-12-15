package com.sdt.myapplication;

import android.content.Context;

import com.google.gson.Gson;
import com.sdt.lib.BaseRequest;
import com.sdt.lib.IoUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;

/**
 * Created by Administrator on 2017/12/15.
 */

public abstract class JsonRequest<V> extends BaseRequest {

    public JsonRequest(Context context) {
        super(context);
    }

    @Override
    protected V getT(InputStream is) throws Exception {
        Class<V> entityClass = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        InputStreamReader reader = new InputStreamReader(is);
        V bean = new Gson().fromJson(reader, entityClass);
        IoUtil.silentClose(reader);
        return bean;
    }
}
