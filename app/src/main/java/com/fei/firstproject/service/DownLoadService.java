package com.fei.firstproject.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.fei.firstproject.R;
import com.fei.firstproject.download.inter.ProgressListener;
import com.fei.firstproject.entity.DownLoadEntity;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.NetUtils;
import com.fei.firstproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/1/5.
 * http://blog.csdn.net/vipzjyno1/article/details/25248021/
 */

public class DownLoadService extends Service {

    private NotificationManager notificationManager;
    private int notificationId = 2000;
    private ExecutorService executorService;

    /*  开始下载*/
    private static final int DOWNLOAD_START = 0;
    /* 下载中 和 完成下载*/
    private static final int DOWNLOAD = 1;
    /* 下载失败 */
    private static final int DOWNLOAD_ERROR = 2;
    /* 保存成功 */
    private static final int SAVE_FINISHED = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_START:
                    Utils.showToast(DownLoadService.this, "下载中...");
                    break;
                case DOWNLOAD:
                    DownLoadEntity downLoadEntity = (DownLoadEntity) msg.obj;
                    refreshNotification(downLoadEntity);
                    break;
                case DOWNLOAD_ERROR:
                    Utils.showToast(DownLoadService.this, "下载失败");
                    break;
                case SAVE_FINISHED:
                    String path = (String) msg.obj;
                    Utils.showToast(DownLoadService.this, "已保存到" + path);
            }
        }
    };

    private void refreshNotification(DownLoadEntity downLoadEntity) {
        if (downLoadEntity != null) {
            synchronized (downLoadEntity) {
                if (downLoadEntity.isDone() && downLoadEntity.getProgress() == downLoadEntity.getTotalLength()) {
                    notificationManager.cancel(downLoadEntity.getFlag());
                    mHandler.removeMessages(downLoadEntity.getFlag());
                    if (downLoadEntity.getFlag() != -1) {
                        Utils.showToast(DownLoadService.this, downLoadEntity.getName() + "下载完成");
                        downLoadEntity.setFlag(-1);
                    }
                    if (downLoadEntity.isInstall()) {
                        String savePath = downLoadEntity.getSavePath();
                        if (!TextUtils.isEmpty(savePath)) {
                            installApk(savePath);
                        }
                    }
                } else {
                    NotificationCompat.Builder builder = downLoadEntity.getBuilder();
                    if (builder != null) {
                        RemoteViews contentView = builder.getContentView();
                        if (contentView != null) {
                            long progress = downLoadEntity.getProgress();
                            long total = downLoadEntity.getTotalLength();
                            contentView.setProgressBar(R.id.pb_notification, 100, (int) (progress * 1f / total) * 100, false);
                            contentView.setTextViewText(R.id.tv_notification_progress, Utils.bytes2kb(progress) + "/" + Utils.bytes2kb(total));
                            notificationManager.notify(downLoadEntity.getFlag(), builder.build());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**创建线程池*/
        int processors = Runtime.getRuntime().availableProcessors();
        executorService = new ThreadPoolExecutor(processors + 1, processors * 2 + 1,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
    }


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

    protected void setNotificationProgrss(final DownLoadEntity downloadEntity) {
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
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                download(downloadEntity);
            }
        });
    }

    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    /**
     * 下载
     */
    private void download(final DownLoadEntity downloadEntity) {
        mHandler.sendEmptyMessage(DOWNLOAD_START);
        final RequestApi downLoad = RetrofitFactory.getDownLoad(new ProgressListener() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                LogUtils.i("tag", "progress--" + progress + "--total--" + total);
                downloadEntity.setProgress(progress);
                downloadEntity.setTotalLength(total);
                downloadEntity.setDone(false);
                if (done) {
                    downloadEntity.setBuilder(null);
                    downloadEntity.setDone(true);
                }
                Message message = Message.obtain();
                message.obj = downloadEntity;
                message.what = DOWNLOAD;
                mHandler.sendMessage(message);
            }
        });
        downLoad.downloadFile(downloadEntity.getDownloadUrl())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        // 可添加网络连接判断等
                        if (!NetUtils.isConnected(DownLoadService.this)) {
                            mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        if (responseBody != null) {
                            try {
                                InputStream is = responseBody.byteStream();
                                File file = new File(downloadEntity.getSavePath());
                                if (file.exists()) {
                                    //存在，删除
                                    file.delete();
                                }
                                FileOutputStream fos = new FileOutputStream(file);
                                BufferedInputStream bis = new BufferedInputStream(is);
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = bis.read(buffer)) != -1) {
                                    fos.write(buffer, 0, len);
                                    fos.flush();
                                }
                                fos.close();
                                bis.close();
                                is.close();
                                Message message = Message.obtain();
                                message.what = SAVE_FINISHED;
                                message.obj = downloadEntity.getSavePath();
                                mHandler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
                    }

                    @Override
                    public void onComplete() {
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
