package com.idankorenisraeli.spyboard.data;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;

public class UsageLog {

    protected HashMap<String, Integer> wordFreq;
    protected HashMap<String, Integer> charFreq;
    //Firestore's keys are strings, therefor we will have here single character strings as key

    public UsageLog(){
        this.wordFreq = new HashMap<>();
        this.charFreq = new HashMap<>();
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
