package com.fei.firstproject.utils;

import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.fei.firstproject.R;

/**
 * Created by Administrator on 2017/9/21.
 */

public class GlideUtils {

    private static RequestOptions options;

    public static RequestOptions getOptions() {
        if (options == null) {
            options = new RequestOptions()
                    .placeholder(R.drawable.ic_app)
                    .error(R.drawable.ic_pic_error)
                    .priority(Priority.HIGH);
        }
        return options;
    }

}
