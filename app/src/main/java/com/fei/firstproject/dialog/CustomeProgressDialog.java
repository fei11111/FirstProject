package com.fei.firstproject.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.fei.firstproject.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/8/3.
 */

public class CustomeProgressDialog extends ProgressDialog {

    private Context mContext;
    private long currentTime;
    private long timeout = 5000;
    private Timer timer;

    private Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (this != null) {
                try {
                    if (CustomeProgressDialog.this.isShowing())
                        CustomeProgressDialog.this.dismiss();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    };

    public CustomeProgressDialog(Context context) {
        super(context, R.style.CustomerDialog);
        mContext = context;
    }

    public CustomeProgressDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.loading, null);
        setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long l = System.currentTimeMillis() - currentTime;
        if (l > timeout) {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void show() {
        super.show();
        currentTime = System.currentTimeMillis();
        timer = new Timer();
        if (timeout > 0)
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    dialogHandler.sendEmptyMessage(0);
                    timer.cancel();

                }
            }, timeout);
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
