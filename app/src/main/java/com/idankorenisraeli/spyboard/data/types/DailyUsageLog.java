package com.idankorenisraeli.spyboard.data.types;


import com.google.firebase.firestore.Exclude;
import com.idankorenisraeli.spyboard.common.TimeManager;

import java.util.Map;

/**
 *
 * This class will sum all the usage of a single user in a single day.
 * When user finish a keyboard session, its data will be added to the daily log.
 *
 */
public class DailyUsageLog extends UsageLog{

    private String date = TimeManager.getInstance().getDateOfToday();



    public DailyUsageLog(){

    }

    public DailyUsageLog(String date){
        this.date = date;
    }



    @Exclude
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
