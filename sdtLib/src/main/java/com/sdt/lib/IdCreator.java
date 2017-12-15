package com.sdt.lib;

/**
 * 产生一个唯一的Id
 * Created by Administrator on 2017/12/15.
 */

public class IdCreator {

    private static int id = 0;

    public static int gen() {
        int newId;
        synchronized (IdCreator.class) {
            if (id == Integer.MAX_VALUE - 1) {
                id = 0;
            }
            ++id;
            newId = id;
        }
        return newId;
    }
}
