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
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.view_notification);
//        builder.setContent(remoteViews)
//                .setTicker("测试通知来啦")
//                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_NO_CREATE))
//                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
//                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
//                .setOngoing(true)
//                .setAutoCancel(false)
//                .setSmallIcon(R.drawable.ic_launcher);
//
//        Notification notification = builder.build();
//        notification.flags = Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(notificationId, notification);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.view_notification);
        mBuilder
                .setContent(mRemoteViews)//设置通知栏标题
                .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("正在播放")
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notify = mBuilder.build();
        if(android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
            notification.bigContentView = remoteViews;
        }
//        notify.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(notificationId, notify);

        downloadEntity.setBuilder(mBuilder);
        downloadEntity.setFlag(notificationId);
        downloadEntity.setNotification(notify);

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
//                downloadEntity.getRemoteView().setProgressBar(R.id.pb_notification, 100, (int) (progress * 1f / total), false);
//                downloadEntity.getNotification().contentView = downloadEntity.getRemoteView();
                downloadEntity.getBuilder().setProgress(100, (int) (progress * 1f / total), false);
                downloadEntity.getBuilder().setContentText(progress / total + "");
                notificationManager.notify(downloadEntity.getFlag(), downloadEntity.getNotification());
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
