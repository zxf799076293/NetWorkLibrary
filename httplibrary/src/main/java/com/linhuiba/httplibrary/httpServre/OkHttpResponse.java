package com.linhuiba.httplibrary.httpServre;

import android.text.TextUtils;


import java.io.UnsupportedEncodingException;

public class OkHttpResponse {
    public int code;
    public Object msg;
    public Object data;
    public int total;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
