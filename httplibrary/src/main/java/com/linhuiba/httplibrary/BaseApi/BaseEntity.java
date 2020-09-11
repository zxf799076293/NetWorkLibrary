package com.linhuiba.httplibrary.BaseApi;

import java.io.Serializable;

/**
 * @author Administrator
 * @date 2020/3/9 18:59
 * @PackageName com.linhuiba.httplibrary.BaseApi
 */
public class BaseEntity<T> implements Serializable {
    private int code;
    private Object msg;
    private T result;
    private static int SUCCESS_CODE = 1;

    public boolean isSuccess(){
        return getCode() == SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
