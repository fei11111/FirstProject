package com.fei.firstproject.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher;

/**
 * Created by Administrator on 2017/8/8.
 */

public class TextSwitchView extends TextSwitcher implements ViewSwitcher.ViewFactory {



    public TextSwitchView(Context context) {
        super(context);
    }

    public TextSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View makeView() {
        return null;
    }
}
