package com.idankorenisraeli.spyboard.data;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

public class FirebaseAuthExecute implements Executor {

    @Override
    public void execute(@NonNull Runnable r) {
        r.run();
    }

}
