package com.example.myapplication.util;

import android.content.Context;

import com.google.firebase.FirebaseApp;

public final class FirebaseSetup {
    private FirebaseSetup() {
    }

    public static boolean isReady(Context context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context);
            }
            return !FirebaseApp.getApps(context).isEmpty();
        } catch (IllegalStateException ignored) {
            return false;
        }
    }
}
