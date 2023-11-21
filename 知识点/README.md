# FirstProject
该项目由个人独立完成，图标和图片是阿里巴巴图标矢量图和千库网找的，接口由之前项目抽取出与业务无关的使用，业务有关的页面没有继续绘制<br/>
(因为是之前项目中抽取出来的接口和页面，所以没有写注释)<br />
1.主要面向接口编程<br />
2.并无业务逻辑，以模仿其他App为主<br />
3.主要用到BaseAcitivity,BaseListActivity,BaseFragment，BaseListFragment<br/>
     BaseActivity,BaseFragment由默认布局完成，且要加@Nullable标签，由loading，
  no_data,还有request_error页面完成，还包含刷新RefreshLayout。还包含一些基本设置，如
  ProgressDialog初始化，ButterKnife运用，授权等。注意授权Activity和Fragment有区别
     BseListActivity,BaseListFragment则集成Base,且增加了RecycleView和底部按钮，
  基本用来绘制只用到ListView的界面。<br/>
4.视频播放MediaPlayer和surfaceView结合，视频下载并通知栏显示下载进度。<br/>
5.用到框架用:<br/>
　1)网络框架：Retrofit2<br/>
　2)上拉刷新框架：smartrefresh<br/>
　3)圆形头像框架：RoundImageView<br/>
　4)图片加载框架: glide<br />
　5)定位：高德地图<br/>
　6)扫描：zxing<br/>
　7)viewpager:jazzyviewPager<br/>
　8)其他：EventBus<br/>
　9)图片选择：PictureSelector<br/>
　10)图片查看：PhotoView<br/>



6.WeakHandler 替换之前用的Handler



7.设备唯一ID
String id = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

8.后台启动APP
Android10中, 当App无前台显示的Activity时,其启动Activity会被系统拦截, 导致启动无效。
个人觉得官方这样做的目的是在用户使用的过程中, 不希望被其他App强制打断, 但这样就对闹钟类, 带呼叫功能的App不太友好了。
对此官方给予的折中方案是使用全屏Intent(full-screen intent), 既创建通知栏通知时, 加入full-screen intent 设置, 示例代码如下(基于官方文档修改):

//AndroidManifest中
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />

Intent fullScreenIntent = new Intent(this,CallActivity.class);
PendingIntent pendingIntent = PendingIntent.getActivity(this,0,fullScreenIntent,PendingIntent.FLAG_UPDATE_CURRENT);
NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"channelId")
.setSmallIcon(R.drawable.ic_app)
.setContentTitle("title")
.setContentText("content")
//以下为关键3行
.setPriority(NotificationCompat.PRIORITY_HIGH)
.setCategory(NotificationCompat.CATEGORY_CALL)
.setFullScreenIntent(pendingIntent,true);
NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
notificationManager.notify(1,builder.build());

9.文件路径
getFilesDir()
/data/user/0/com.fei.firstproject/files

getExternalFilesDir(DIRECTORY_PICTURES)
/storage/emulated/0/Android/data/com.fei.firstproject/files/Pictures

10.android10以上需要将沙盒中的视频或图片文件保存在外部存储时
保存到外部沙盒
FileUtil.savePictureFile(context,源文件,Environment.DIRECTORY_DCIM(目标路径))
FileUtil.saveVideoFile(context,源文件,Environment.DIRECTORY_MUSIC(目标路径))
获取手机所有图片
FileUtil.loadPhotoFiles(AlbumActivity.this);
BitmapFactory.Options options = new BitmapFactory.Options();
options.inJustDecodeBounds = true;
try {
FileUtil.getBitmapFromUri(mContext, uri, null, options);
int outWidth = options.outWidth;
int outHeight = options.outHeight;
if (outWidth > outHeight) {
options.inSampleSize = outWidth / imageWidth;
} else {
options.inSampleSize = outHeight / imageWidth;
}
options.outWidth = imageWidth;
options.outHeight = imageWidth;
options.inPreferredConfig = Bitmap.Config.RGB_565;
options.inJustDecodeBounds = false;
return FileUtil.getBitmapFromUri(mContext, uri, null, options);
} catch (IOException e) {
throw new RuntimeException(e);
}



11.连接无可访问外网wifi，用移动网访问接口
        
        
        
        
        
        
        
        
        
        


