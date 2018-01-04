package com.fei.firstproject.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fei.firstproject.download.bean.ProgressBean;

/**
 * Created by ljd on 4/12/16.
 */
public abstract class ProgressHandler {

    public abstract void sendMessage(ProgressBean progressBean);

    protected abstract void handleMessage(Message message);

    protected abstract void onProgress(long progress, long total, boolean done);

    protected static class ResponseHandler extends Handler{

        private ProgressHandler mProgressHandler;
        public ResponseHandler(ProgressHandler mProgressHandler, Looper looper) {
            super(looper);
            this.mProgressHandler = mProgressHandler;
        }

        @Override
        public void handleMessage(Message msg) {
            mProgressHandler.handleMessage(msg);
        }
    }

}
