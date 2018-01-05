package com.fei.firstproject.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.fei.firstproject.R;
import com.fei.firstproject.download.inter.ProgressListener;
import com.fei.firstproject.entity.DownLoadEntity;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.Utils;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/1/5.
 * http://blog.csdn.net/vipzjyno1/article/details/25248021/
 */

public class DownLoadService extends Service {

    private NotificationManager notificationManager;
    private int notificationId = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            DownLoadEntity downloadEntity = (DownLoadEntity) intent.getSerializableExtra("downloadEntity");
            if (downloadEntity != null) {
                setNotificationProgrss(downloadEntity);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    protected void setNotificationProgrss(DownLoadEntity downloadEntity) {
        notificationId++;
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_notification);
        mRemoteViews.setImageViewResource(R.id.iv_notification, R.drawable.ic_app);
        mRemoteViews.setTextViewText(R.id.tv_notification_name, downloadEntity.getName());
        mRemoteViews.setProgressBar(R.id.pb_notification, 100, 0, false);
        mBuilder
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("下载中...")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setCustomContentView(mRemoteViews)//设置通知栏标题
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setSmallIcon(R.drawable.ic_download);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notify);

        downloadEntity.setBuilder(mBuilder);
        downloadEntity.setFlag(notificationId);

        //下载
        download(downloadEntity);
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 下载
     */
    private void download(final DownLoadEntity downloadEntity) {
        RetrofitFactory.setProgressListener(new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                LogUtils.i("tag", "progress--" + progress + "--total--" + total);
                NotificationCompat.Builder builder = downloadEntity.getBuilder();
                if (builder != null) {
                    RemoteViews contentView = builder.getContentView();
                    if (contentView != null) {
                        float v = progress * 1f / total;
                        contentView.setProgressBar(R.id.pb_notification, 100, (int) (progress * 1f / total) * 100, true);
                        contentView.setTextViewText(R.id.tv_notification_progress, Utils.bytes2kb(progress) + "/" + Utils.bytes2kb(total));
                        notificationManager.notify(downloadEntity.getFlag(), builder.build());
                    }
                }
                if (done) {
                    notificationManager.cancel(downloadEntity.getFlag());
                    if (downloadEntity.isInstall()) {
                        String savePath = downloadEntity.getSavePath();
                        if (!TextUtils.isEmpty(savePath)) {
                            installApk(savePath);
                        }
                    }
                }
            }
        });
        RetrofitFactory.getBigDb().downloadFile(downloadEntity.getDownloadUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {

                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 安装APK文件
     */
    private void installApk(String filePath) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(filePath)),
                "application/vnd.android.package-a rchive");
        startActivity(intent);
    }
}
