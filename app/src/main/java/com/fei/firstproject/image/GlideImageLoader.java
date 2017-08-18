package com.fei.firstproject.image;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fei.banner.loader.ImageLoader;
import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/7/31.
 */

public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide 加载图片简单用法
        Glide.with(context).load(path).error(R.drawable.ic_banner_default).into(imageView);

        //用fresco加载图片简单用法，记得要写下面的createImageView方法
        Uri uri = Uri.parse((String) path);
        imageView.setImageURI(uri);
    }
}
