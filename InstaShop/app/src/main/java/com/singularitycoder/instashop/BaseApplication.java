package com.singularitycoder.instashop;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.squareup.leakcanary.LeakCanary;

public class BaseApplication extends Application {

    private static final String TAG = "BaseApplication";

    private static BaseApplication _instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if(_instance == null) {
            _instance = this;
        }

        // Initializing Leaky Canary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized BaseApplication getInstance() {
        return _instance;
    }
}
