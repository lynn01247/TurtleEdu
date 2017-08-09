package com.tatait.turtleedu.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tatait.turtleedu.BuildConfig;
import com.tatait.turtleedu.http.HttpInterceptor;
import com.tatait.turtleedu.utils.Preferences;
import com.tencent.bugly.Bugly;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.tatait.turtleedu.api.KeyStore.BUGLY_APP_ID_;

/**
 * 自定义Application
 * Created by Lynn on 2015/11/27.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //开发时，方便调试
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this); //在Appliction里面设置我们的异常处理器为UncaughtExceptionHandler处理器

        AppCache.init(this);
        AppCache.updateNightMode(Preferences.isNightMode());
        initOkHttpUtils();
        initImageLoader();
        initBugly();

        Fresco.initialize(this);
    }

    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new HttpInterceptor())
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheSize(2 * 1024 * 1024) // 2MB
                .diskCacheSize(50 * 1024 * 1024) // 50MB
                .build();
        ImageLoader.getInstance().init(configuration);
    }

    private void initBugly() {
        if (!BuildConfig.DEBUG) {
            Bugly.init(this, BUGLY_APP_ID_, false);
        }
    }
}