package com.linhuiba.httplibrary.httpServre;

import com.linhuiba.httplibrary.BaseApi.BaseEntity;
import com.linhuiba.httplibrary.BaseApi.RetrofitFactory;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Call;

/**
 * @author Administrator
 * @date 2020/3/10 19:25
 * @PackageName com.linhuiba.httplibrary.httpServre
 */
public class UserService {
    private UserApi api;

    private UserService() {
        api = RetrofitFactory.getInstance().create(UserApi.class);
    }

    public static UserService  getInstance() {
        return NbaserviceHolder.S_INSTANCE;
    }

    private static class NbaserviceHolder {
        private static final UserService S_INSTANCE = new UserService();
    }

    public Observable<BaseEntity<Object>> getJONBAInfo(String vid) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("vid", vid);
        return api.getJONBAInfo(map);
    }

    public Call<OkHttpResponse> getArticles() {
        return api.getArticles();
    }

    /**
     * 绑定设备1
     * @return
     */
    public Observable<BaseEntity<String>> devices(HashMap<String, Object> map) {
        return api.devices(map);
    }


}
