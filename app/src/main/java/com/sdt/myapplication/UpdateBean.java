package com.sdt.myapplication;

/**
 * Created by Administrator on 2017/12/15.
 */

public class UpdateBean {

    protected int status;
    protected String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[").append(status).append(",").append(msg).append("]");
        return stringBuilder.toString();
    }
}
