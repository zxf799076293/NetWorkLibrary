package com.linhuiba.httplibrary.interceptor;

import android.os.Build;

import com.linhuiba.httplibrary.utils.ApiConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        HashMap<String, String> heads = ApiConfig.getHeads();

        String token = ApiConfig.getToken();

        final Request.Builder authorization = originalRequest.newBuilder();

        //    @Headers({"AcceptVersion:2", "BaseUrl:https://www.baidu.com/"})// version替换和baseUrl替换
        int version = 1;
        String baseUrl ="";
        List<String> headerValues = originalRequest.headers("AcceptVersion");
        if (headerValues != null && headerValues.size() > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            authorization.removeHeader("AcceptVersion");
            version = Integer.parseInt(headerValues.get(0));
        }

        List<String> baseUrlHeaderList = originalRequest.headers("BaseUrl");
        if (baseUrlHeaderList != null && baseUrlHeaderList.size() > 0) {
            //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
            authorization.removeHeader("BaseUrl");
            baseUrl = baseUrlHeaderList.get(0);
        }

        authorization.header("Authorization", "Bearer " + token);
        authorization.header("x-client", "android");
        authorization.header("x-client-version", "1");
        //动态添加Header
        if (null != heads) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                heads.forEach(new BiConsumer<String, String>() {
                    @Override
                    public void accept(String key, String value) {
                        authorization.header(key, value);
                    }
                });
            } else {
                Iterator<Map.Entry<String, String>> iterator = heads.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    authorization.header(entry.getKey(), entry.getValue());
                }
            }

        }
        //接口版本大于1替换
        if (version > 1) {
            authorization.header("Accept", new StringBuffer("application/vnd.linhuiba.v").append(version).append("+json").toString());
        }
        //接口的baseUrl替换
        if (baseUrl.length() > 0) {
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = originalRequest.url();
            //baseurl 动态设置
            HttpUrl newBaseUrl = HttpUrl.parse(baseUrl);
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl;

            if (baseUrl.equals("https://api.linhuiba.com/prs/api/")) {
                newFullUrl = oldHttpUrl
                        .newBuilder()
                        .setEncodedPathSegment(0, "prs/api")
                        .build();
            } else {
                newFullUrl = oldHttpUrl
                        .newBuilder()
                        .scheme(newBaseUrl.scheme())
                        .host(newBaseUrl.host())
                        .port(newBaseUrl.port())
                        .removePathSegment(0)//删除多余的path
                        .build();
            }
            //重建这个request，通过builder.url(newFullUrl).build()；
            return chain.proceed(authorization.url(newFullUrl).build());
        } else {
            Request build = authorization.build();
            return chain.proceed(build);
        }
    }
}
