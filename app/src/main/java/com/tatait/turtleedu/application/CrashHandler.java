package com.tatait.turtleedu.application;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tatait.turtleedu.activity.MainActivity;

import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

    private static CrashHandler instance; // 单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例
    private Context mContext;

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() { // 同步方法，以免单例多线程环境下出现异常
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) { // 初始化，把当前对象设置成UncaughtExceptionHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.mContext = ctx;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) { // 当有未处理的异常发生时，就会来到这里。。
        Log.d("Sandy", "uncaughtException, thread: " + thread + " ,name: "
                + thread.getName() + " ,id: " + thread.getId() + ",exception: "
                + ex);
        if (ex.toString().contains("MainActivity")) {
            // 退出程序,注释下面的重启启动程序代码
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            // 重新启动程序，注释上面的退出程序
            Intent intent = new Intent();
            intent.setClass(mContext, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}