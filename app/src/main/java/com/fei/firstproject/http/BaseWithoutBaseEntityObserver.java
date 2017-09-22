package com.fei.firstproject.http;

import android.content.Context;

import com.fei.firstproject.http.exceptiion.ExceptionEngine;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/8/12.
 */

public abstract class BaseWithoutBaseEntityObserver<T> implements Observer<T> {

    private static final String TAG = "BaseObserver";
    private Context mContext;

    protected BaseWithoutBaseEntityObserver(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        onHandleSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        Utils.showToast(mContext,ExceptionEngine.handleException(e).getMessage());
    }

    @Override
    public void onComplete() {
        LogUtils.d(TAG, "onComplete");
    }


    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
        Utils.showToast(mContext, msg);
    }
}
