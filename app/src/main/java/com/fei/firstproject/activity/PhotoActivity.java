package com.fei.firstproject.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fei.firstproject.R;
import com.fei.firstproject.adapter.PhotoPagerAdapter;
import com.fei.firstproject.adapter.SingleTextAdapter;
import com.fei.firstproject.dialog.BottomListDialog;
import com.fei.firstproject.http.factory.RetrofitFactory;
import com.fei.firstproject.http.inter.RequestApi;
import com.fei.firstproject.utils.LogUtils;
import com.fei.firstproject.utils.NetUtils;
import com.fei.firstproject.utils.PathUtils;
import com.fei.firstproject.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/21.
 */

public class PhotoActivity extends BaseActivity {

    private static final int REQUEST_PERMISSION_CODE_STORAGE = 100;

    @BindView(R.id.vp_photo)
    ViewPager vpPhoto;
    @BindView(R.id.tv_page)
    TextView tvPage;

    private List<String> pics;
    private BottomListDialog bottomListDialog;
    private int position = -1;//图片位置

    @Override
    public void permissionsDeniedCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            showMissingPermissionDialog(getString(R.string.need_storage_permission), requestCode);
        }
    }

    @Override
    public void permissionsGrantCallBack(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            if (pics != null && pics.size() > position) {
                String url = pics.get(position);
                if (url.contains("/")) {
                    int indexOf = url.lastIndexOf('/');
                    String fileName = url.substring(indexOf + 1, url.length());
                    String filePath = PathUtils.getImgPath() + File.separator + fileName;
                    File file = new File(filePath);
                    if (file.exists()) {
                        Utils.showToast(this, "你已经保存过了");
                    } else {
                        //下载图片
                        downloadImage(url, filePath);
                    }
                }
            }
        }
    }

    //下载保存
    private void downloadImage(String url, final String filePath) {
        RequestApi downLoad = RetrofitFactory.getDownLoad(null);
        downLoad.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        // 可添加网络连接判断等
                        if (!NetUtils.isConnected(PhotoActivity.this)) {
                            Utils.showToast(PhotoActivity.this, "网络不给力，下载失败");
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
                        LogUtils.i("tag", "onNext");
                        if (responseBody != null) {
                            try {
                                InputStream is = responseBody.byteStream();
                                File file = new File(filePath);
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
                                Utils.showToast(PhotoActivity.this, "保存成功,保存在" + filePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Utils.showToast(PhotoActivity.this, "下载失败");
                    }

                    @Override
                    public void onComplete() {
                    }

                });
    }

    @Override
    public void permissionDialogDismiss(int requestCode) {
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE) {
            Utils.showToast(this, getString(R.string.storage_permission_fail));
        }
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_photo;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initData();
        initPage();
        initViewPager();
    }

    private void initViewPager() {
        PhotoPagerAdapter photoPagerAdapter = new PhotoPagerAdapter(this, pics);
        vpPhoto.setAdapter(photoPagerAdapter);
        initListener(photoPagerAdapter);
    }

    private void initListener(PhotoPagerAdapter photoPagerAdapter) {
        photoPagerAdapter.setOnPhotoTpListener(new PhotoPagerAdapter.OnPhotoTpListener() {
            @Override
            public void onPhotoTap() {
                onBackPressed();
            }

            @Override
            public void onLongTap(int position) {
                PhotoActivity.this.position = position;
                if (bottomListDialog == null) {
                    List<String> name = new ArrayList<String>();
                    name.add("保存");
                    bottomListDialog = new BottomListDialog(PhotoActivity.this);
                    bottomListDialog.setRlDialogHeadVisibility(View.GONE);
                    SingleTextAdapter singleTextAdapter = new SingleTextAdapter(PhotoActivity.this, name);
                    bottomListDialog.setAdapter(singleTextAdapter);
                    bottomListDialog.setOnItemClickListener(new BottomListDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                //保存
                                checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE_STORAGE);
                            }
                        }
                    });
                }
                bottomListDialog.show();
            }
        });
        vpPhoto.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvPage.setText((position + 1) + "/" + pics.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initPage() {
        tvPage.setText("1/" + pics.size());
    }

    private void initData() {
        pics = getIntent().getStringArrayListExtra("pics");
        if (pics == null) {
            onBackPressed();
        }
    }

    @Override
    public void initRequest() {

    }

}
