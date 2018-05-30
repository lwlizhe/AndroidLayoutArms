package com.lwlizhe.armLibrary;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import com.lwlizhe.armLibrary.helper.ScreenHelper;

import java.util.ArrayList;

/**
 * Todo：实现适配（dp完成，px正在做）
 * Todo：实现纵向
 * Todo：实现指定页面适配
 * Created by Administrator on 2018/5/30 0030.
 */

public class LayoutArms {

    private ScreenHelper mHelper;

    private Context mContext;

    private boolean isActive = false;

    private ArrayList<Activity> mTargetActivityList;

    private static LayoutArms mInstance;


    public static LayoutArms getInstance(Context mContext) {
        if (mInstance == null) {
            synchronized (LayoutArms.class) {
                if (mInstance == null) {
                    mInstance = new LayoutArms(mContext);
                }
            }
        }

        return mInstance;
    }

    public void setParams(int designWidth, int designHeight, String designUnit, boolean designHeightEnable){
        mHelper = ScreenHelper.getInstance(designWidth, designHeight, designUnit, designHeightEnable);

        init();
    }


    private LayoutArms(Context mContext) {

        this.mContext = mContext;


    }

    private void init() {

        mTargetActivityList = new ArrayList<>();

    }

    private ComponentCallbacks mComponentCallback = new ComponentCallbacks() {
        @Override
        public void onConfigurationChanged(Configuration newConfig) {

//            if(newConfig!=null&&newConfig.fontScale>0){
//
//            }

        }

        @Override
        public void onLowMemory() {

        }
    };

    private Application.ActivityLifecycleCallbacks mCallback = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (isActive) {
                mHelper.reset(activity);
            } else {

                for (Activity targetActivity : mTargetActivityList) {
                    if (targetActivity==activity) {
                        mHelper.resetTarget(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

            if (isActive) {
                mHelper.reset(activity);
            } else {
                for (Activity targetActivity : mTargetActivityList) {
                    if (activity==targetActivity) {
                        mHelper.resetTarget(activity);
                    }
                }
            }

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };


    /**
     * 针对某个特定页面
     *
     * @param targetActivity
     */
    public void activeTargetActivity(Activity targetActivity) {

        isActive = false;

        mTargetActivityList.add(targetActivity);

    }

    public void activeAll() {

        if (mContext == null) {
            return;
        }

        isActive = true;
        Application application = (Application) (mContext.getApplicationContext());

        application.registerActivityLifecycleCallbacks(mCallback);
        /**
         * 当用户更改系统的字体字号之类的，应该重新计算
         */
        application.registerComponentCallbacks(mComponentCallback);
    }

    public void inActiveAll() {
        if (mContext == null) {
            return;
        }

        isActive = false;

        ((Application) (mContext.getApplicationContext())).registerActivityLifecycleCallbacks(mCallback);
        /**
         * 当用户更改系统的字体字号之类的，应该重新计算
         */
        mContext.getApplicationContext().registerComponentCallbacks(mComponentCallback);
    }

}
