package com.idankorenisraeli.spyboard.data;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.idankorenisraeli.spyboard.common.SharedPrefsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class FirebaseManager {

    private static FirebaseManager instance = null;

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final SharedPrefsManager sharedPrefs = SharedPrefsManager.getInstance();
    private CollectionReference usersRef;


    interface KEYS{
        String USERS = "users";
        String DAILY_LOGS = "daily_logs";


        String UID = "my_uid";
    }

    private FirebaseManager(){
        usersRef = database.collection(KEYS.USERS);
    }

    public static FirebaseManager getInstance() {
        if(instance == null)
            instance = new FirebaseManager();
        return instance;
    }

    public void saveDailyLog(DailyUsageLog log){
        usersRef.document(getUID().concat("/").concat(KEYS.DAILY_LOGS).concat("/").concat(log.getDate())).set(log);
    }


    /**
     * Each user gets its own unique id for the database
     * this unique id will be saved on shared prefs
     * @return Current user's unique ID
     */
    private String getUID(){
        String uuid;
        if(sharedPrefs.contain(KEYS.UID)){
            uuid = sharedPrefs.getString(KEYS.UID, UUID.randomUUID().toString());
        }
        else {
            uuid = UUID.randomUUID().toString();
            sharedPrefs.putString(KEYS.UID, uuid);

        }
        return uuid;
    }
}
