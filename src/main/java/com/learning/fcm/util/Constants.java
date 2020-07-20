package com.learning.fcm.util;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.connection.PersistentConnection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Constants {
    public static Map<String, FirebaseApp> FIREBASE_APP_MAP =new ConcurrentHashMap<>();
}
