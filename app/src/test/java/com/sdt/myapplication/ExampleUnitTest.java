package com.sdt.myapplication;

import com.sdt.lib.BaseRequest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testBaseRequestCode(){
        System.out.println(BaseRequest.STATUS_OK);
        System.out.println(BaseRequest.STATUS_IO_ERROR);
        System.out.println(BaseRequest.STATUS_INTERRUPTED_ERROR);
        System.out.println(BaseRequest.STATUS_PASRESTREAM_ERROR);
    }
}