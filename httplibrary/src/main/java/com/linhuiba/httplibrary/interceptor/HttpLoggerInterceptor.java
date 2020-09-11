package com.linhuiba.httplibrary.interceptor;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author ：mp5a5 on 2018/12/27 11：24
 * @describe：
 * @email：wwb199055@126.com
 */
public class HttpLoggerInterceptor {

    public static HttpLoggingInterceptor getLoggerInterceptor() {
        //日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

}
