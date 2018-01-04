package com.fei.firstproject.download;

import android.os.Looper;
import android.os.Message;

import com.fei.firstproject.download.bean.ProgressBean;

/**
 * Created by ljd on 4/18/16.
 * 上传
 */
public abstract class UploadProgressHandler extends ProgressHandler{

    private static final int UPLOAD_PROGRESS = 0;
    protected ResponseHandler mHandler = new ResponseHandler(this, Looper.getMainLooper());

    @Override
    public void sendMessage(ProgressBean progressBean) {
        mHandler.obtainMessage(UPLOAD_PROGRESS,progressBean).sendToTarget();

    }

    @Override
    protected void handleMessage(Message message){
        switch (message.what){
            case UPLOAD_PROGRESS:
                ProgressBean progressBean = (ProgressBean)message.obj;
                onProgress(progressBean.getBytesRead(),progressBean.getContentLength(),progressBean.isDone());
        }
    }

}
