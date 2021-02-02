package com.idankorenisraeli.spyboard.common;


// Need also import gradle:         implementation 'com.google.code.gson:gson:2.8.2'

/*
   <<<<    Initial:    >>>>
        Put in Application class:
        PreferencesHelper.initHelper(this);
        PreferencesHelper.initHelper(this, "CustomName");
        *Will use app name as of Preferences file name PreferencesHelper.initHelper(this);
        *Will use CustomName as the name of SharePreferences file PreferencesHelper.initHelper(this, "CustomName");

   <<<<    Using:    >>>>
       SET VALUE:
       PreferencesHelper.getInstance().setValue(KEY_BOOLEAN, true);
       PreferencesHelper.getInstance().setValue(KEY_DOUBLE, 123.123);
       PreferencesHelper.getInstance().setValue(KEY_FLOAT, 234.234f);
       PreferencesHelper.getInstance().setValue(KEY_INT, 345);
       PreferencesHelper.getInstance().setValue(KEY_LONG, Long.MAX_VALUE);
       PreferencesHelper.getInstance().setValue(KEY_STRING, "Khang");
       UserModel userModel = new UserModel("KhangTran", 27);
       PreferencesHelper.getInstance().setValue(KEY_OBJECT, userModel);

       GET VALUE:
       boolean booleanValue = PreferencesHelper.getInstance().getBooleanValue(KEY_BOOLEAN, false);
       double doubleValue = PreferencesHelper.getInstance().getDoubleValue(KEY_DOUBLE, Double.MIN_VALUE);
       float floatValue = PreferencesHelper.getInstance().getFloatValue(KEY_FLOAT, Float.MIN_VALUE);
       int intValue = PreferencesHelper.getInstance().getIntValue(KEY_INT, Integer.MIN_VALUE);
       long longValue = PreferencesHelper.getInstance().getLongValue(KEY_LONG, Long.MIN_VALUE);
       String stringValue = PreferencesHelper.getInstance().getStringValue(KEY_STRING, "Empty");
       UserModel userModel = PreferencesHelper.getInstance().getObjectValue(KEY_OBJECT, UserModel.class);
 */

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

public class EncryptedSPManager {


    public interface KEYS {
        String SP_NAME = "SPYBOARD_SP_DB";

        String SP_INIT_ACTIVITY_SHOWN = "INIT_ACTIVITY";
        String SP_KEY_PREFIX = "SP_SPYBOARD_";
    }



    private static EncryptedSPManager instance;
    private SharedPreferences prefs;

    public static EncryptedSPManager getInstance() {
        return instance;
    }

    private EncryptedSPManager(Context context) {

        String masterKeyAlias = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            prefs = EncryptedSharedPreferences.create(
                    KEYS.SP_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


    }

    private EncryptedSPManager(Context context, String sharePreferencesName) {
        prefs = context.getApplicationContext().getSharedPreferences(sharePreferencesName, Context.MODE_PRIVATE);
    }

    public static EncryptedSPManager initHelper(Context context) {
        if (instance == null)
            instance = new EncryptedSPManager(context);
        return instance;
    }

    public static EncryptedSPManager initHelper(Context context, String sharePreferencesName) {
        if (instance == null)
            instance = new EncryptedSPManager(context, sharePreferencesName);
        return instance;
    }

    public void putBoolean(String KEY, boolean value) {
        prefs.edit().putBoolean(KEY, value).apply();
    }

    public void putString(String KEY, String value) {
        prefs.edit().putString(KEY, value).apply();
    }

    public void putObject(String KEY, Object value) {
        prefs.edit().putString(KEY, new Gson().toJson(value)).apply();
    }

    public void putInt(String KEY, int value) {
        prefs.edit().putInt(KEY, value).apply();
    }

    public void putLong(String KEY, long value) {
        prefs.edit().putLong(KEY, value).apply();
    }

    public void putFloat(String KEY, float value) {
        prefs.edit().putFloat(KEY, value).apply();
    }

    public void putDouble(String KEY, double defValue) {

        putString(KEY, String.valueOf(defValue));
    }

    public boolean getBoolean(String KEY, boolean defvalue) {
        return prefs.getBoolean(KEY, defvalue);
    }

    public String getString(String KEY, String defvalue) {
        return prefs.getString(KEY, defvalue);
    }

    public <T> T getObject(String KEY, Class<T> mModelClass) {
        Object object = null;
        try {
            object = new Gson().fromJson(prefs.getString(KEY, ""), mModelClass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Primitives.wrap(mModelClass).cast(object);
    }

    public int getInt(String KEY, int defValue) {
        return prefs.getInt(KEY, defValue);
    }

    public long getLong(String KEY, long defValue) {
        return prefs.getLong(KEY, defValue);
    }

    public float getFloat(String KEY, float defValue) {
        return prefs.getFloat(KEY, defValue);
    }

    public double getDouble(String KEY, double defValue) {
        return Double.parseDouble(getString(KEY, String.valueOf(defValue)));
    }

    public void removeKey(String KEY) {
        prefs.edit().remove(KEY).apply();
    }

    public boolean contain(String KEY) {
        return prefs.contains(KEY);
    }

    public void registerChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public <T> void putArray(String KEY, ArrayList<T> array) {
        String json = new Gson().toJson(array);
        prefs.edit().putString(KEY, json).apply();
    }

    public <T> ArrayList<T> getArray(String KEY, TypeToken typeToken, ArrayList<T> defaultValue) {
        // type token == new TypeToken<ArrayList<YOUR_CLASS>>() {}
        ArrayList<T> arr = null;
        try {
            arr = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return arr==null ? defaultValue : arr;
    }

    public <S, T> void putMap(String KEY, HashMap<S, T> map) {
        String json = new Gson().toJson(map);
        prefs.edit().putString(KEY, json).apply();
    }

    public <S, T> HashMap<S, T> getMap(String KEY, TypeToken typeToken) {
        // getMap(MySharedPreferencesV4.KEYS.SP_PLAYLISTS, new TypeToken<HashMap<String, Playlist>>() {});
        // type token == new TypeToken<ArrayList<YOUR_CLASS>>() {}
        HashMap<S, T> map = null;
        try {
            map = new Gson().fromJson(prefs.getString(KEY, ""), typeToken.getType());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}