package com.fei.firstproject.http;


import android.content.Context;

import com.fei.firstproject.activity.BaseProjectActivity;
import com.fei.firstproject.fragment.BaseProjectFragment;
import com.fei.firstproject.http.inter.CallBack;
import com.fei.firstproject.inter.IBase;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.core.Observer;


public class HttpUtils<T> {

    private static HttpUtils httpUtils;

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {
                if (httpUtils == null) {
                    httpUtils = new HttpUtils();
                }
            }
        }
        return httpUtils;
    }


    /**
     * @param iBase
     * @param observable
     * @param isBaseEntity    返回的数据格式是不是基于基础数据格式
     * @param isShowErrorView 没网络时，是否显示错误页面
     * @param callBack
     */
    public void request(IBase iBase, Observable<T> observable, boolean isBaseEntity, boolean isShowErrorView, final CallBack<T> callBack) {

        ObservableTransformer<T, T> transformer = null;
        BaseProjectActivity activity = null;
        BaseProjectFragment fragment = null;
        Context context = null;
        Observer<T> observer = null;

        if (iBase instanceof BaseProjectActivity) {
            activity = (BaseProjectActivity) iBase;
            context = activity;
            transformer = createTransformer(activity, isShowErrorView);
        } else if (iBase instanceof BaseProjectFragment) {
            fragment = (BaseProjectFragment) iBase;
            context = fragment.getActivity();
            transformer = createTransformer(fragment, isShowErrorView);
        }

        if (isBaseEntity) {
            observer = (Observer<T>) new BaseObserver<T>(context) {
                @Override
                protected void onHandleSuccess(T t) {
                    callBack.onSuccess(t);
                }

                @Override
                protected void onHandleError(String msg) {
                    super.onHandleError(msg);
                    callBack.onFail();
                }
            };
        } else {
            observer = new BaseWithoutBaseEntityObserver<T>(context) {
                @Override
                protected void onHandleSuccess(T t) {
                    callBack.onSuccess(t);
                }

                @Override
                protected void onHandleError(String msg) {
                    super.onHandleError(msg);
                    callBack.onFail();
                }
            };
        }

        if (transformer != null && observer != null) {
            if (activity != null) {
                observable.compose(this.<T>createTransformer(activity, isShowErrorView)).subscribe(observer);
            } else if (fragment != null) {
                observable.compose(this.<T>createTransformer(fragment, isShowErrorView)).subscribe(observer);
            }
        }

    }


    private <T> ObservableTransformer<T, T> createTransformer(final BaseProjectActivity activity, final boolean isShow) {
        return RxSchedulers.compose(activity, activity.<T>bindToLifecycle(), new RxSchedulers.OnConnectError() {
            @Override
            public void onError() {
                if (isShow) {
                    activity.showRequestErrorView();
                }
            }
        });
    }

    private <T> ObservableTransformer<T, T> createTransformer(final BaseProjectFragment fragment, final boolean isShow) {
        return RxSchedulers.compose(fragment.getActivity(), fragment.<T>bindToLifecycle(), new RxSchedulers.OnConnectError() {
            @Override
            public void onError() {
                if (isShow) {
                    fragment.showRequestErrorView();
                }
            }
        });
    }

}
