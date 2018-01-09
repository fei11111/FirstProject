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


