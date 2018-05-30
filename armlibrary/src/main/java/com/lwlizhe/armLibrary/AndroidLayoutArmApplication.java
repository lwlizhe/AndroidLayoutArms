package com.lwlizhe.armLibrary;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;


/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class AndroidLayoutArmApplication extends Application {

    protected LayoutArms arms;


    @Override
    public void onCreate() {
        super.onCreate();

        arms = LayoutArms.getInstance(this);

        init(AndroidLayoutArmApplication.this);

    }


    /**
     * 初始化，获取清单文件中的配置参数
     *
     * @param context 环境变量
     */
    public void init(Context context) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context
                    .getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (applicationInfo == null) {
            return;
        }

        int designWidth = applicationInfo.metaData.getInt("design_width");
        int designHeight = applicationInfo.metaData.getInt("design_height");
        String designUnit = applicationInfo.metaData.getString("design_unit");
        boolean designHeightEnable = applicationInfo.metaData.getBoolean("design_height_enable");


        arms.setParams(designWidth, designHeight, designUnit, designHeightEnable);
    }


}
