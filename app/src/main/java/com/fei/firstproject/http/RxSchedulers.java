package com.fei.firstproject.http;

import android.content.Context;

import com.fei.firstproject.utils.NetUtils;
import com.trello.rxlifecycle4.LifecycleTransformer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


/**
 * Created by Administrator on 2017/8/12.
 */

public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> compose(final Context mContext, final LifecycleTransformer<T> lifecycle, final OnConnectError onConnectError) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) {
                                // 可添加网络连接判断等
                                if (!NetUtils.isConnected(mContext)) {
                                    onConnectError.onError();
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(lifecycle);
            }
        };
    }

    public interface OnConnectError {
        void onError();
    }
}
