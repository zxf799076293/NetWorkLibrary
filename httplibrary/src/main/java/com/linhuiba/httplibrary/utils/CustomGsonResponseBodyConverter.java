package com.linhuiba.httplibrary.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.linhuiba.httplibrary.BaseApi.BaseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Administrator
 * @date 2020/5/11 16:20
 * @PackageName com.linhuiba.httplibrary.utils
 */
public class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        BaseEntity<Object> httpStatus = gson.fromJson(response, BaseEntity.class);
        if (!httpStatus.isSuccess() && (httpStatus.getResult() == null || httpStatus.getResult().toString().length() == 0)) {
            value.close();
            throw new ApiException(httpStatus.getMsg() != null ? httpStatus.getMsg().toString() : "", httpStatus.getCode());
        }

        MediaType contentType = value.contentType();
        Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
        InputStream inputStream = new ByteArrayInputStream(response.getBytes());
        Reader reader = new InputStreamReader(inputStream, charset);
        JsonReader jsonReader = gson.newJsonReader(reader);

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}