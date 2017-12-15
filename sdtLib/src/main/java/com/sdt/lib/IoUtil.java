package com.sdt.lib;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2017/12/15.
 */

public class IoUtil {

    public static void silentClose(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
