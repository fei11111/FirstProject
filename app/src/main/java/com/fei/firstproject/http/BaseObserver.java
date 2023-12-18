package com.fei.firstproject.http;

import android.content.Context;
import android.text.TextUtils;

import com.fei.firstproject.entity.BaseEntity;
import com.fei.firstproject.http.exceptiion.ExceptionEngine;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;


/**
 * Created by Administrator on 2017/8/12.
 */

public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {

    private static final String TAG = "BaseObserver";
    private Context mContext;

    protected BaseObserver(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(BaseEntity<T> value) {
        if (value.isState()) {
            T t = value.getData();
            onHandleSuccess(t);
        } else {
            onHandleError(value.getMessage());
        }
    }

    @Override
    public void onError(Throwable e) {
        onHandleError(ExceptionEngine.handleException(e).getMessage());
    }

    @Override
    public void onComplete() {
        LogUtils.d(TAG, "onComplete");
    }


    protected abstract void onHandleSuccess(T t);

    protected void onHandleError(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Utils.showToast(mContext, msg);
        }
    }
}
