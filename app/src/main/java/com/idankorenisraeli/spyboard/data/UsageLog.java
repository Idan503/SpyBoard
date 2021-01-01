package com.idankorenisraeli.spyboard.data;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class UsageLog {

    protected HashMap<String, Integer> wordFreq = new HashMap<>();
    protected HashMap<String, Integer> charFreq = new HashMap<>();
    //Firestore's keys are strings, therefor we will have here single character strings as key

    public UsageLog(){
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
        Log.i("pttt" , "Added word "  + word + " | " + wordFreq.get(word) + " is the value");
        Log.i("pttt" , "KEYSET: " + wordFreq.keySet().toString());
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


}
