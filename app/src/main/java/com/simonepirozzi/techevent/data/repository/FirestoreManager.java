package com.simonepirozzi.techevent.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreManager {
    public static final String USER_COLLECTION = "/utenti";
    public static final String EVENT_COLLECTION = "/eventi";

    public static final String BANNED_USER = "bannato";
    public static final String STATE_PROGRESS = "attesa";


    public static FirebaseFirestore getInstance() {
        return FirebaseFirestore.getInstance();
    }
    public static FirebaseAuth getAuthInstance() { return FirebaseAuth.getInstance(); }
}
