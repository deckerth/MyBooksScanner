package com.example.thomas.mybooksscanner;

import android.app.Application;
import android.content.Context;

/**
 * Android Application class. Used for accessing singletons.
 */
public class BasicApp extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getContext() { return mContext; }

    public DataRepository getRepository() {
        return DataRepository.getInstance();
    }
}
