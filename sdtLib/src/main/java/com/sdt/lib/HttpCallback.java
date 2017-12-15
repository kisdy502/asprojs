package com.sdt.lib;

/**
 * Created by Administrator on 2017/12/15.
 */

public interface HttpCallback {
    /**
     * @param id     the reuqest id
     * @param request
     */
    public void onRequestSuccess(int id, BaseRequest request);

    public void onRequestFail(int id, BaseRequest request);

    public void onRequestCancel(int id);

    public static class SimpleCallback implements HttpCallback {

        @Override
        public void onRequestSuccess(int id, BaseRequest request) {
        }

        @Override
        public void onRequestFail(int id, BaseRequest request) {
        }

        @Override
        public void onRequestCancel(int id) {
        }

    }
}
