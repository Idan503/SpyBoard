package com.idankorenisraeli.spyboard.common;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeManager {
    private static TimeManager instance = null;

    private TimeManager(){

    }

    public static TimeManager getInstance() {
        if(instance==null)
            instance = new TimeManager();
        return instance;
    }

    public String getDateOfToday(){
        Date date = Calendar.getInstance().getTime();
        Log.i("pttt", "Current time => " + date);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return df.format(date);
    }
}
