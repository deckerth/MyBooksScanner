package com.example.thomas.mybooksscanner.service;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.thomas.mybooksscanner.BasicApp;
import com.example.thomas.mybooksscanner.ui.views.SettingsActivity;

public class Settings {

    private static Settings mSettings = null;
    private SharedPreferences mSharedPref = PreferenceManager.getDefaultSharedPreferences(BasicApp.getContext());

    public static Settings getInstance(){
        if (mSettings == null) mSettings = new Settings();
        return mSettings;
    }


    public Boolean getLookupTitles() {
        return mSharedPref.getBoolean(SettingsActivity.KEY_LOOKUP_TITLES,true);
    }

    public Boolean getPermanentScan() {
        return mSharedPref.getBoolean(SettingsActivity.KEY_PERMANENT_SCAN,true);
    }

    public Boolean getAutomaticScan() {
        return mSharedPref.getBoolean(SettingsActivity.KEY_AUTOMATIC_SCAN,true);
    }

    public Boolean getUseFlash() {
        return mSharedPref.getBoolean(SettingsActivity.KEY_USE_FLASH,false);
    }

    public Boolean getAutoFocus() {
        return mSharedPref.getBoolean(SettingsActivity.KEY_AUTO_FOCUS,true);
    }

}
