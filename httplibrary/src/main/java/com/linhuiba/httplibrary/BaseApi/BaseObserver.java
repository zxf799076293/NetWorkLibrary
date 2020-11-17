package com.linhuiba.httplibrary.BaseApi;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.linhuiba.httplibrary.dialog.CustomProgressDialogUtils;
import com.linhuiba.httplibrary.utils.ApiException;

import org.json.JSONException;

import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.ParseException;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * @author Administrator
 * @date 2020/3/9 18:53
 * @PackageName com.linhuiba.httplibrary.BaseApi
 */
public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {
    protected Context mContext;
    private boolean mShowLoading = false;
    private CustomProgressDialogUtils progressDialogUtils;
    private String mMsg;
    private CompositeDisposable mCompositeDisposable;
    protected Disposable mDisposable;

    private static final String CONNECT_ERROR = "网络连接失败,请检查网络";
    private static final String CONNECT_TIMEOUT = "连接超时,请稍后再试";
    private static final String BAD_NETWORK = "服务器异常";
    private static final String PARSE_ERROR = "解析服务器响应数据失败";
    private static final String UNKNOWN_ERROR = "未知错误";
    private static final String RESPONSE_RETURN_ERROR = "服务器返回数据失败";
    private static final String NOT_FOUND_MSG = "NOT_FOUND";
    private static final int NOT_FOUND_CODE = 404;
    private static final String SERVER_ERROR_MSG = "服务器内部出错";
    private static final int SERVER_ERROR_CODE = 500;
    private static final String TOKEN_ERROR_MSG = "登录失效";
    public static final int TOKEN_ERROR_CODE = -99;
    public static final int OUT_OF_SERVICE_ERROR_CODE = -1000;//企业账号已停用 联系客服
    public static final String OUT_OF_SERVICE_ERROR_MSG = "企业目前处于停用状态，请联系客服";//企业目前处于停用状态，请联系客服
    public static final int OUT_OF_SERVICE_AUTHORIZATION_ERROR_CODE = -1006;//企业账号已停用 上传授权书
    public static final int AUTHORIZATION_ERROR_CODE = -2001;//请完成企业认证
    public static final int AUTHORIZATION_INFO_ERROR_CODE = -2002;//请补全企业信息完成企业认证

    private static final int NOT_FOUND_ERROR_CODE = -999;//后台没有返回code 自定义code
    private static final int CONNECT_ERROR_CODE = -998;//网络连接失败code
    public BaseObserver(Context cxt) {
        this.mContext = cxt;
    }

    public BaseObserver() {

    }

    /**
     * 如果传入上下文，那么表示您将开启自定义的进度条
     *
     * @param context 上下文
     */
    public BaseObserver(Context context, boolean isShow) {
        this.mContext = context;
        this.mShowLoading = isShow;
    }

    /**
     * 如果传入上下文，那么表示您将开启自定义的进度条
     *
     * @param context 上下文
     */
    public BaseObserver(Context context, boolean isShow, String msg) {
        this.mContext = context;
        this.mShowLoading = isShow;
        this.mMsg = msg;
    }

    public BaseObserver(CompositeDisposable compositeDisposable) {
        mCompositeDisposable = compositeDisposable;
    }


    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        try {
            if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                mCompositeDisposable.add(mDisposable);
            }
        } catch (Exception ignored) {

        }
//        onRequestStart();
    }
    @Override
    public void onNext(BaseEntity<T> tBaseEntity) {
        if (tBaseEntity.isSuccess()) {
            try {
                success(tBaseEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                ApiException throwable = new ApiException(getErrorMsg(tBaseEntity.getMsg()), tBaseEntity.getCode());
                failure(throwable, (tBaseEntity.getResult() != null && tBaseEntity.getResult().toString().length() > 0)
                        ? new Gson().toJson(tBaseEntity.getResult()) : "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(Throwable e) {
//        onRequestEnd();
        if (e instanceof retrofit2.HttpException) {
            //HTTP错误
            if (((HttpException) e).code() == NOT_FOUND_CODE) {
                onException(e, ExceptionReason.NOT_FOUND);
            } else if (((HttpException) e).code() == SERVER_ERROR_CODE) {
                onException(e, ExceptionReason.LOCATION_SERVER_ERROR);
            } else {
                onException(e, ExceptionReason.BAD_NETWORK);
            }
        } else if (e instanceof ConnectException || e instanceof UnknownHostException) {
            //连接错误
            onException(e, ExceptionReason.CONNECT_ERROR);
        } else if (e instanceof InterruptedIOException) {
            //连接超时
            onException(e, ExceptionReason.CONNECT_TIMEOUT);
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            //解析错误
            onException(e, ExceptionReason.PARSE_ERROR);
        } else if (e instanceof ApiException) {
            try {
                if (((ApiException) e).getCode() == TOKEN_ERROR_CODE) {
                    tokenFailure(TOKEN_ERROR_CODE);
                } else if (((ApiException) e).getCode() == OUT_OF_SERVICE_ERROR_CODE
                        && ((ApiException) e).getMsg().indexOf(OUT_OF_SERVICE_ERROR_MSG) != -1) {
                    tokenFailure(OUT_OF_SERVICE_ERROR_CODE);
                } else if (((ApiException) e).getCode() == OUT_OF_SERVICE_AUTHORIZATION_ERROR_CODE) {
                    tokenFailure(OUT_OF_SERVICE_AUTHORIZATION_ERROR_CODE);
                } else if (((ApiException) e).getCode() == AUTHORIZATION_ERROR_CODE) {
                    tokenFailure(AUTHORIZATION_ERROR_CODE);
                } else if (((ApiException) e).getCode() == AUTHORIZATION_INFO_ERROR_CODE) {
                    tokenFailure(AUTHORIZATION_INFO_ERROR_CODE);
                } else {
                    failure((ApiException)e, getErrorMsg(((ApiException) e).getMsg()));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //其他错误
            onException(e, ExceptionReason.UNKNOWN_ERROR);
        }
    }

    @Override
    public void onComplete() {
//        onRequestEnd();
    }

    /**
     * 返回成功
     *
     * @param t
     * @throws Exception
     */
    protected abstract void success(BaseEntity<T> t) throws Exception;

    /**
     * 返回失败
     *
     *   是否是网络错误
     * @throws Exception
     */
    protected abstract void failure(ApiException e, String errorMsg) throws Exception;


    private void onException(Throwable e, ExceptionReason reason) {
        String msg;
        switch (reason) {
            case CONNECT_ERROR:
                msg = CONNECT_ERROR;
                break;

            case CONNECT_TIMEOUT:
                msg = CONNECT_TIMEOUT;
                break;

            case BAD_NETWORK:
                msg = BAD_NETWORK;
                break;

            case PARSE_ERROR:
                msg = PARSE_ERROR + e.getMessage();
                break;

            case UNKNOWN_ERROR:
                if (e.getMessage() != null) {
                    msg = e.getMessage();
                } else {
                    msg = UNKNOWN_ERROR;
                }
                break;
            case NOT_FOUND:
                msg = NOT_FOUND_MSG;
                break;
            case LOCATION_SERVER_ERROR:
                msg = SERVER_ERROR_MSG;
                break;

            default:
                msg = UNKNOWN_ERROR;
                break;
        }

        try {
            int code = NOT_FOUND_ERROR_CODE;
            if (msg.equals(CONNECT_ERROR)) {
                code = CONNECT_ERROR_CODE;
            } else if (msg.equals(NOT_FOUND_MSG)) {
                code = NOT_FOUND_CODE;
            } else if (msg.equals(SERVER_ERROR_MSG)) {
                code = SERVER_ERROR_CODE;
            }
            ApiException throwable = new ApiException(getErrorMsg(msg), code);
            failure(throwable, getErrorMsg(msg));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public enum ExceptionReason {
        /**
         * 解析数据失败
         */
        PARSE_ERROR,
        /**
         * 网络问题
         */
        BAD_NETWORK,
        /**
         * 连接错误
         */
        CONNECT_ERROR,
        /**
         * 连接超时
         */
        CONNECT_TIMEOUT,
        /**
         * 未知错误
         */
        UNKNOWN_ERROR,

        NOT_FOUND,

        LOCATION_SERVER_ERROR
    }

    private void showToast(String msg) {
        Toast mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        mToast.setText(msg);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 网络请求开始
     */
    protected void onRequestStart() {
        if (mShowLoading) {
            showProgressDialog();
        }
    }

    /**
     * 网络请求结束
     */
    protected void onRequestEnd() {
        closeProgressDialog();
    }

    /**
     * 开启Dialog
     */
    public void showProgressDialog() {
        progressDialogUtils = new CustomProgressDialogUtils();
        if (TextUtils.isEmpty(mMsg)) {
            progressDialogUtils.showProgress(mContext);
        } else {
            progressDialogUtils.showProgress(mContext, mMsg);
        }
    }

    /**
     * 关闭Dialog
     */
    public void closeProgressDialog() {
        if (progressDialogUtils != null) {
            progressDialogUtils.dismissProgress();
        }
    }


    private String getErrorMsg(Object msg) {
        if (msg == null) {
            return "";
        } else {
            return msg.toString();
        }
    }

    /**
     * 失去token后的操作 可以根据具体要求重写此方法
     */
    protected void tokenFailure(int code)  {
        try {
            ApiException throwable = new ApiException("", code);
            failure(throwable, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
