package com.linhuiba.httplibrary.httpServre;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.linhuiba.httplibrary.BaseApi.BaseEntity;
import com.linhuiba.httplibrary.BaseApi.BaseObserver;
import com.linhuiba.httplibrary.R;
import com.linhuiba.httplibrary.utils.ApiConfig;
import com.linhuiba.httplibrary.utils.ApiException;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Administrator
 * @date 2020/3/10 21:18
 * @PackageName com.linhuiba.httplibrary.httpServre
 */
public class NetMainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_activity_main);

        HashMap<String, String> headMap = new HashMap<String, String>();
        headMap.put("x-Client", "android");
        headMap.put("x-Client-version", "3.19Beta");
        ApiConfig build = new ApiConfig.Builder()
                .setBaseUrl("https://hz.lanhanba.net/api/")//BaseUrl，这个地方加入后项目中默认使用该url
                .setHeads(headMap)//动态添加的header，也可以在其他地方通过ApiConfig.setHeads()设置
                .build();
        ApiConfig.setShowLog(true);

        UserService.getInstance()
                .getJONBAInfo("1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Object>() {
                    @Override
                    protected void success(BaseEntity<Object> t) throws Exception {
                        Toast.makeText(NetMainActivity.this,t.getResult().toString(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void failure(ApiException e, String errorMsg) throws Exception {
                        Toast.makeText(NetMainActivity.this,errorMsg,Toast.LENGTH_LONG).show();
                    }
                });
    }
}
