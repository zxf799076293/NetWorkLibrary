package com.linhuiba.httplibrary.httpServre;

import com.linhuiba.httplibrary.BaseApi.BaseEntity;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * @author Administrator
 * @date 2020/3/10 17:41
 * @PackageName com.linhuiba.crm.httpServre
 */
public interface UserApi {
    @GET("articles/207")
    Observable<BaseEntity<Object>> getJONBAInfo(@QueryMap HashMap<String, Object> map);

    @GET("articles/207")
    Call<OkHttpResponse> getArticles();

    @POST("devices")
    Observable<BaseEntity<String>> devices(@Body HashMap<String, Object> map);

}
