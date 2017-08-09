package com.tatait.turtleedu.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.tatait.turtleedu.activity.BaseActivity;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ScreenUtils;
import com.tatait.turtleedu.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lynn on 2016/11/23.
 */
public class AppCache {
    private Context mContext;
    private final List<BaseActivity> mActivityStack = new ArrayList<>();

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache sAppCache = new AppCache();
    }

    private static AppCache getInstance() {
        return SingletonHolder.sAppCache;
    }

    public static void init(Context context) {
        getInstance().onInit(context);
    }

    private void onInit(Context context) {
        mContext = context.getApplicationContext();
        ToastUtils.init(mContext);
        Preferences.init(mContext);
        ScreenUtils.init(mContext);
        CrashHandler.getInstance().init(mContext);
    }

    public static Context getContext() {
        return getInstance().mContext;
    }


    public static void updateNightMode(boolean on) {
        Resources resources = getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }


    public static void addToStack(BaseActivity activity) {
        getInstance().mActivityStack.add(activity);
    }

    public static void removeFromStack(BaseActivity activity) {
        getInstance().mActivityStack.remove(activity);
    }

    public static void clearStack() {
        List<BaseActivity> activityStack = getInstance().mActivityStack;
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            BaseActivity activity = activityStack.get(i);
            activityStack.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}