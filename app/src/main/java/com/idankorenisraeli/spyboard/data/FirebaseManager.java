package com.idankorenisraeli.spyboard.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idankorenisraeli.spyboard.common.SharedPrefsManager;

import java.util.ArrayList;
import java.util.UUID;

public class FirebaseManager {

    private static FirebaseManager instance = null;

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SharedPrefsManager sharedPrefs = SharedPrefsManager.getInstance();
    private final CollectionReference usersRef;

    private final ArrayList<DailyUsageLog> waitingList;


    interface KEYS {
        String USERS = "users";
        String DAILY_LOGS = "daily_logs";


        String UID = "my_uid";
    }

    private FirebaseManager() {
        usersRef = database.collection(KEYS.USERS);
        waitingList = new ArrayList<>();
    }

    public static FirebaseManager getInstance() {

        if (instance == null)
            instance = new FirebaseManager();
        return instance;
    }


    //This will override the current daily log that is saved in db/sp
    private void saveDailyLog(DailyUsageLog log) {
        assert log != null;

        getDailyLogDocRef(log.getDate()).set(log)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Could not save to firebase, so saving on sp
                        Log.e(getClass().getSimpleName(), e.toString());
                        sharedPrefs.putObject(getDailyLogSPKey(log.getDate()), log);
                    }
                })
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
                });
    }

    public void loadDailyLog(String date, OnDailyLogLoaded onLoaded) {

        Runnable checkOnSP = new Runnable() {
            @Override
            public void run() {
                //Check if date received is in sp
                String spKey = getDailyLogSPKey(date);
                if(onLoaded!=null) {
                    if (sharedPrefs.contain(spKey))
                        onLoaded.onDailyLogLoaded(sharedPrefs.getObject(spKey, DailyUsageLog.class));
                    else
                        onLoaded.onDailyLogLoaded(null); //not found on fb and sp
                }
            }
        };

        getDailyLogDocRef(date).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(onLoaded!=null) {
                    if (documentSnapshot.exists())
                        onLoaded.onDailyLogLoaded(documentSnapshot.toObject(DailyUsageLog.class));
                    else
                        checkOnSP.run();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                checkOnSP.run();
            }
        });




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

    private String getDailyLogSPKey(String date) {
        return KEYS.DAILY_LOGS + "_" + date;
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
