package com.deckerth.thomas.mybooksscanner;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() { return mContext; }

// --Commented out by Inspection START (09.12.2019 19:57):
//    public DataRepository getRepository() {
//        return DataRepository.getInstance();
//    }
// --Commented out by Inspection STOP (09.12.2019 19:57)
}
