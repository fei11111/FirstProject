package com.fei.banner;

import androidx.viewpager.widget.ViewPager.PageTransformer;
import com.fei.banner.transformer.AccordionTransformer;
import com.fei.banner.transformer.BackgroundToForegroundTransformer;
import com.fei.banner.transformer.CubeInTransformer;
import com.fei.banner.transformer.CubeOutTransformer;
import com.fei.banner.transformer.DefaultTransformer;
import com.fei.banner.transformer.DepthPageTransformer;
import com.fei.banner.transformer.FlipHorizontalTransformer;
import com.fei.banner.transformer.FlipVerticalTransformer;
import com.fei.banner.transformer.ForegroundToBackgroundTransformer;
import com.fei.banner.transformer.RotateDownTransformer;
import com.fei.banner.transformer.RotateUpTransformer;
import com.fei.banner.transformer.ScaleInOutTransformer;
import com.fei.banner.transformer.StackTransformer;
import com.fei.banner.transformer.TabletTransformer;
import com.fei.banner.transformer.ZoomInTransformer;
import com.fei.banner.transformer.ZoomOutSlideTransformer;
import com.fei.banner.transformer.ZoomOutTranformer;

public class Transformer {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}
