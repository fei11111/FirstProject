package com.fei.firstproject.download;


import android.os.Looper;
import android.os.Message;

import com.fei.firstproject.download.bean.ProgressBean;

/**
 * Created by ljd on 4/12/16.
 * 下载
 */
public abstract class DownloadProgressHandler extends ProgressHandler {

    private static final int DOWNLOAD_PROGRESS = 1;
    protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

    @Override
    public void sendMessage(ProgressBean progressBean) {
        mHandler.obtainMessage(DOWNLOAD_PROGRESS, progressBean).sendToTarget();

    }

    @Override
    protected void handleMessage(Message message) {
        switch (message.what) {
            case DOWNLOAD_PROGRESS:
                ProgressBean progressBean = (ProgressBean) message.obj;
                onProgress(progressBean.getBytesRead(), progressBean.getContentLength(), progressBean.isDone());

        }
    }


}
