package com.linhuiba.httplibrary.utils;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * 网络请求配置文件
 *
 */
public class ApiConfig implements Serializable {

    private static String mBaseUrl;
    private static int mDefaultTimeout = 20000;
    private static HashMap<String, String> mHeads;
    private static String mToken = "";
    private static boolean showLog;
    /**
     * 接口的版本号
     */
    private static int version;

    private ApiConfig(Builder builder) {

        mBaseUrl = builder.baseUrl;

        mDefaultTimeout = builder.defaultTimeout;
        mHeads = builder.heads;
        mToken = builder.token;
        version = builder.version;
        showLog = builder.showLog;
    }

    public static String getBaseUrl() {
        return mBaseUrl;
    }

    public static int getDefaultTimeout() {
        return mDefaultTimeout;
    }

    public static HashMap<String, String> getHeads() {
        return mHeads;
    }

    public static void setHeads(HashMap<String, String> mHeads) {
        ApiConfig.mHeads = mHeads;
    }

    public static String getToken() {
        return mToken;
    }

    public static void setToken(String mToken) {
        ApiConfig.mToken = mToken;
    }

    public static boolean isShowLog() {
        return showLog;
    }

    public static void setShowLog(boolean showLog) {
        ApiConfig.showLog = showLog;
    }

    public static int getVersion() {
        return version;
    }

    public static void setVersion(int version) {
        ApiConfig.version = version;
    }

    public static final class Builder  {
        private String baseUrl;
        private int defaultTimeout;
        private HashMap<String, String> heads;
        private boolean showLog;
        private String token;
        private int version;
        public Builder setHeads(HashMap<String, String> heads) {
            this.heads = heads;
            return this;
        }

        public Builder setBaseUrl(String mBaseUrl) {
            this.baseUrl = mBaseUrl;
            return this;
        }

        public Builder setDefaultTimeout(int defaultTimeout) {
            this.defaultTimeout = defaultTimeout;
            return this;
        }

        public Builder setShowLog(boolean showLog) {
            this.showLog = showLog;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public ApiConfig build() {
            return new ApiConfig(this);
        }
    }
}
