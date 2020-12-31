package com.idankorenisraeli.spyboard.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class will represent one single day of spyboard usage.
 * With data of what words did the user typed and which characters
 */
public class DailyUsageLog implements Serializable {
    private String date;
    private HashMap<String, Integer> wordFreq;
    private HashMap<Character, Integer> charFreq;
    private ArrayList<String> passwordSuspicious;

    public DailyUsageLog(){

    }

    public DailyUsageLog(String date){
        this.date = date;
    }

    public void saveChar(Character c){
        Integer count = charFreq.getOrDefault(c, 0);
        assert count!=null;
        count++;
        charFreq.put(c, count);
    }

    public void saveWord(String word){
        Integer count = wordFreq.getOrDefault(word, 0);
        assert count!=null;
        count++;
        wordFreq.put(word, count);
    }


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

    public HashMap<Character, Integer> getCharFreq() {
        return charFreq;
    }

    public void setCharFreq(HashMap<Character, Integer> charFreq) {
        this.charFreq = charFreq;
    }

    public ArrayList<String> getPasswordSuspicious() {
        return passwordSuspicious;
    }

    public void setPasswordSuspicious(ArrayList<String> passwordSuspicious) {
        this.passwordSuspicious = passwordSuspicious;
    }
}
