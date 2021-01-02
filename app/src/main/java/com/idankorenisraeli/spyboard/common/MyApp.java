package com.idankorenisraeli.spyboard.common;

import android.app.Application;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EncryptedSPManager.initHelper(this);
    }
}
