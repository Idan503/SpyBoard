package com.idankorenisraeli.spyboard.data;

import android.util.Log;


import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class will represent one single day of spyboard usage.
 * With data of what words did the user typed and which characters
 */
public class DailyUsageLog implements Serializable {

    private String date;

    private HashMap<String, Integer> wordFreq;
    private HashMap<String, Integer> charFreq;

    //private ArrayList<String> passwordSuspicious; TBD

    public DailyUsageLog(){
        this.date = getDateOfToday();
    }

    public DailyUsageLog(HashMap<String, Integer> words, HashMap<String, Integer> chars){
        this.date = getDateOfToday();
    }

    public DailyUsageLog(String date){
        this.date = date;
    }

    @Exclude
    public void addChar(String c){
        Integer count = charFreq.getOrDefault(c, 0);
        assert count!=null;
        count++;
        charFreq.put(c, count);
    }

    @Exclude
    public void addWord(String word){
        Integer count = wordFreq.getOrDefault(word, 0);
        assert count!=null;
        count++;
        wordFreq.put(word, count);
    }


    @Exclude
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String, Integer> getWordFreq() {
        return wordFreq;
    }

    public void setWordFreq(HashMap<String, Integer> wordFreq) {
        this.wordFreq = wordFreq;
    }

    public HashMap<String, Integer> getCharFreq() {
        return charFreq;
    }

    public void setCharFreq(HashMap<String, Integer> charFreq) {
        this.charFreq = charFreq;
    }

    /*
    public ArrayList<String> getPasswordSuspicious() {
        return passwordSuspicious;
    }

    public void setPasswordSuspicious(ArrayList<String> passwordSuspicious) {
        this.passwordSuspicious = passwordSuspicious;
    }
*/

    @Exclude
    private String getDateOfToday(){
        Date date = Calendar.getInstance().getTime();
        Log.i("pttt", "Current time => " + date);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        return df.format(date);
    }
}
