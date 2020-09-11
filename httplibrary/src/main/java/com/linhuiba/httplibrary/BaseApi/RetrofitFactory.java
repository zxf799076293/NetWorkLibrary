package com.linhuiba.httplibrary.BaseApi;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.linhuiba.httplibrary.interceptor.HttpHeaderInterceptor;
import com.linhuiba.httplibrary.interceptor.HttpLoggerInterceptor;
import com.linhuiba.httplibrary.utils.ApiConfig;
import com.linhuiba.httplibrary.utils.CustomGsonConverterFactory;
import com.linhuiba.httplibrary.utils.HttpsUtil;
import com.linhuiba.httplibrary.utils.NullTypeAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * @author Administrator
 * @date 2020/3/9 19:44
 * @PackageName com.linhuiba.httplibrary.BaseApi
 */
public class RetrofitFactory {
    private final Retrofit.Builder retrofit;
    public Retrofit build;

    private RetrofitFactory() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient().newBuilder()
                .readTimeout(ApiConfig.getDefaultTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(ApiConfig.getDefaultTimeout(), TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpHeaderInterceptor());

        if (ApiConfig.isShowLog()) {
            httpClientBuilder.addInterceptor(HttpLoggerInterceptor.getLoggerInterceptor());
        }

        if (ApiConfig.getBaseUrl() != null && ApiConfig.getBaseUrl().startsWith("https")){
            HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory();
            httpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory,sslParams.trustManager);
        }

        OkHttpClient httpClient = httpClientBuilder.build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .serializeNulls()
                .registerTypeAdapterFactory(new NullTypeAdapterFactory())
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        retrofit = new Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(CustomGsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        if (!TextUtils.isEmpty(ApiConfig.getBaseUrl())) {
            build = retrofit.baseUrl(ApiConfig.getBaseUrl()).build();
        }

    }

    private static class RetrofitFactoryHolder {
        private static final RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    public static final RetrofitFactory getInstance() {
        if (!RetrofitFactoryHolder.INSTANCE.build.baseUrl().equals(ApiConfig.getBaseUrl())) {
            RetrofitFactoryHolder.INSTANCE.build = RetrofitFactoryHolder.INSTANCE.retrofit.baseUrl(ApiConfig.getBaseUrl()).build();
        }

        return RetrofitFactoryHolder.INSTANCE;
    }

    /**
     * 根据Api接口类生成Api实体
     *
     * @param clazz 传入的Api接口类
     * @return Api实体类
     */
    public <T> T create(Class<T> clazz) {
        checkNotNull(build, "BaseUrl not init,you should init first!");
        return build.create(clazz);
    }

    /**
     * 根据Api接口类生成Api实体
     *
     * @param baseUrl baseUrl
     * @param clazz   传入的Api接口类
     * @return Api实体类
     */
    public <T> T create(String baseUrl, Class<T> clazz) {
        return retrofit.baseUrl(baseUrl).build().create(clazz);
    }

    private <T> T checkNotNull(@Nullable T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }
}
