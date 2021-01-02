package com.idankorenisraeli.spyboard.data;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class UsageLog {

    protected HashMap<String, Integer> wordFreq = new HashMap<>();
    protected HashMap<String, Integer> charFreq = new HashMap<>();
    //Firestore's keys are strings, therefor we will have here single character strings as key

    protected String allData;

    @Exclude
    private StringBuilder allDataBuilder = new StringBuilder();

    public UsageLog(){
    }

    @Exclude
    public void addChar(String c){
        Integer count = charFreq.getOrDefault(c, 0);
        assert count!=null;
        count++;
        charFreq.put(c, count);
        allDataBuilder.append(c);
    }

    @Exclude
    public void addWord(String word){
        Log.i("pttt", "Adding word: " + word);
        Integer count = wordFreq.getOrDefault(word, 0);
        assert count!=null;
        count++;
        wordFreq.put(word, count);
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


    public String getAllData() {
        allData = allDataBuilder.toString();
        return allData;
    }

    public void setAllData(String allData) {
        this.allData = allData;
    }

}
