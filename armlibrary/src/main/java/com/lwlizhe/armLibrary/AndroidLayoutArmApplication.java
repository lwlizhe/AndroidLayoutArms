package com.lwlizhe.armLibrary;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/5/25 0025.
 */

public class AndroidLayoutArmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        init(AndroidLayoutArmApplication.this);

    }

    public void init(Context context) {
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context
                    .getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int designWidth = applicationInfo.metaData.getInt("design_width");
        String design_unit = applicationInfo.metaData.getString("design_unit");

    }
}
