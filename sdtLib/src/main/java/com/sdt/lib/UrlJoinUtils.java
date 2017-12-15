package com.sdt.lib;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static java.net.Proxy.Type.HTTP;

/**
 * 专门用来拼接URL，参数
 * <p>
 * Created by Administrator on 2017/12/15.
 */

public class UrlJoinUtils {
    final static String TAG = "UrlJoinUtils";
    final static char QUESTION_MARK = '?';
    final static char AND_MARK = '&';
    final static char EQUAL_MARK = '=';


    /**
     * 按照http连接的要求,将参数拼接到url的后面
     *
     * @param url
     * @param segments
     * @return 拼接后的url
     */
    public static String joinUrlAndParams(String url,
                                          ArrayMap<String, String> segments) {
        if (segments == null || segments.isEmpty()) {
            return url;
        }
        StringBuilder stringBuilderUrl = new StringBuilder(url);
        if (urlContainQuestionMark(url)) {
            stringBuilderUrl.append(AND_MARK);
        } else {
            stringBuilderUrl.append(QUESTION_MARK);
        }
        Set<Map.Entry<String, String>> entrys = segments.entrySet();
        Iterator<Map.Entry<String, String>> it = entrys.iterator();
        Map.Entry<String, String> entry;
        String key, value;
        try {
            while (it.hasNext()) {
                entry = it.next();
                key = entry.getKey();
                value = entry.getValue();
                if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
                    Log.w(TAG, "key " + "[" + key + "] or value [" + value + "] is null or empty , ignore");
                    continue;  //key  or value should not be null;
                }
                //url?key1=value1&key2=value2&key3=value3&
                stringBuilderUrl.append(key).append(EQUAL_MARK)
                        .append(URLEncoder.encode(value.trim(), "UTF-8"))
                        .append(AND_MARK);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;        //这个异常不可能发生
        }
        stringBuilderUrl.setLength(stringBuilderUrl.length() - 1); //去掉结束的多的一个&
        return stringBuilderUrl.toString();
    }

    /**
     * 判断url中是否包含了？字符串
     *
     * @param url
     * @return
     */
    static boolean urlContainQuestionMark(String url) {
        int index = url.lastIndexOf(QUESTION_MARK);
        if (index > -1) {
            return true;
        } else {
            return false;
        }
    }
}
