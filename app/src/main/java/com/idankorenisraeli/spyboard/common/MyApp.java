package com.idankorenisraeli.spyboard.common;

import android.app.Application;

import com.idankorenisraeli.spyboard.data.DatabaseManager;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EncryptedSPManager.initHelper(this);
        CommonUtils.initHelper(this);
        DatabaseManager.initHelper();
    }
}
