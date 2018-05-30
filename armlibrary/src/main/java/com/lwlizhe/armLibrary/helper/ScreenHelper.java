package com.lwlizhe.armLibrary.helper;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2018/5/29 0029.
 */

public class ScreenHelper {

    private int mDesignWidth;
    private int mDesignHeight;

    private int mScreenWidth;
    private int mScreenHeight;

    private String mDesignUnit;
    private boolean mHeightAdaptationEnable = false;


    private static ScreenHelper mInstance;

    public synchronized static ScreenHelper getInstance(int designWidth, int designHeight, String designUnit, boolean heightAdaptationEnable) {

        if (mInstance == null) {
            synchronized (ScreenHelper.class) {
                if (mInstance == null) {
                    mInstance = new ScreenHelper(designWidth, designHeight, designUnit, heightAdaptationEnable);
                }
            }
        }
        return mInstance;
    }

    private ScreenHelper(int designWidth, int designHeight, String designUnit, boolean heightAdaptationEnable) {
        mDesignWidth = designWidth;
        mDesignHeight = designHeight;
        mDesignUnit = designUnit;
        mHeightAdaptationEnable = heightAdaptationEnable;
    }

    public void reset(Activity mContext) {
        resetDensity(mContext);
    }

    public void resetTarget(Activity mContext) {
        resetDensity(mContext);
    }

    /**
     * density计算公式：
     * 因为 density=px/dp
     * 所以 适配density=px/适配dp，即 适配density=DisplayMetrics.widthPixels/适配dp
     * 因此 当设计图的px不是设备px的情况下，先等比缩放px，再套用上面的公式。
     *
     * @param mActivity
     */
    private void resetDensity(Activity mActivity) {
        if (mActivity == null)
            return;

        int orientation = mActivity.getRequestedOrientation();
        Resources resources = mActivity.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight=displayMetrics.heightPixels;
        }else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mScreenWidth = displayMetrics.widthPixels;
            mScreenHeight=displayMetrics.heightPixels;
        }

//        mScreenWidth = displayMetrics.widthPixels;
//        mScreenHeight=displayMetrics.heightPixels;

        float nonAdaptationDensity = displayMetrics.density;
        float nonAdaptationScaledDensity = displayMetrics.scaledDensity;

        float targetDensity = 0;
        float targetScaledDensity = 0;
        int targetDensityDpi = 0;

        switch (mDesignUnit) {

            case "dp":
                //dp情况下直接套上面所说的公式

                targetDensity = mScreenWidth / (float) mDesignWidth;

                break;

            case "px":

                targetDensity = nonAdaptationDensity * ((float) mDesignWidth / (float) mScreenWidth);

                calculatePx(mActivity);
                break;

        }
        // 跟字体，即 sp 有关的，做个简单的等比变化，要不然字体大小有问题。
        targetScaledDensity = targetDensity * (nonAdaptationScaledDensity / nonAdaptationDensity);
        // 更改dpi
        targetDensityDpi = (int) (targetDensity * 160);

        displayMetrics.density = targetDensity;
        displayMetrics.scaledDensity = targetScaledDensity;
        displayMetrics.densityDpi = targetDensityDpi;

        //application 获取的对象是不一样的，保险起见一起设置了
        DisplayMetrics applicationDisplayMetrics = mActivity.getApplication().getResources().getDisplayMetrics();

        applicationDisplayMetrics.density = targetDensity;
        applicationDisplayMetrics.scaledDensity = targetScaledDensity;
        applicationDisplayMetrics.densityDpi = targetDensityDpi;

    }

    /**
     * 因为 TypedValue的applyDimension方法不会对px做任何修改，因此目前采用的方式是手动重新计算并赋值给LayoutParams
     */
    private void calculatePx(Activity activity) {

        loadPxView(activity.getWindow().getDecorView());
    }

    private void loadPxView(View targetView) {

        if (targetView instanceof ViewGroup) {
            int childCount = ((ViewGroup) targetView).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView=((ViewGroup) targetView).getChildAt(i);
                loadPxView(childView);
            }
        }else{
            setPxView(targetView);
        }


    }

    private void setPxView(View targetView) {

        ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
        if (layoutParams.width > 0) {
            layoutParams.width = setValue(layoutParams.width,mScreenWidth,mScreenHeight);
        }
        if (layoutParams.height > 0) {
            layoutParams.height = setValue(layoutParams.height,mScreenWidth,mScreenHeight);
        }

    }

    /**
     * 计算px的值
     *
     * @param value        传入的当前view的值
     * @param widthPixels  屏幕宽
     * @param heightPixels 屏幕长
     * @return 计算好的px值
     */
    private int setValue(int value, int widthPixels, int heightPixels) {

        return value == 0 || value == 1 ? value : (int) (value * ((float) widthPixels / (float) mDesignWidth));
    }


    //解决MIUI更改框架导致的MIUI7+Android5.1.1上出现的失效问题(以及极少数基于这部分miui去掉art然后置入xposed的手机)
    private static DisplayMetrics getMetricsOnMiui(Resources resources) {
        if ("MiuiResources".equals(resources.getClass().getSimpleName()) || "XResources".equals(resources.getClass().getSimpleName())) {
            try {
                Field field = Resources.class.getDeclaredField("mTmpMetrics");
                field.setAccessible(true);
                return (DisplayMetrics) field.get(resources);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
