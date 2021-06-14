package com.simonepirozzi.techevent.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface MainRepositoryInterface {
    Task<QuerySnapshot> getCollection(String collection);
    Task<DocumentSnapshot> getDocument(String collection, String document);

    Task<Void> setDocument(String collection, String document, Object object);

    Task<AuthResult> signIn(String username, String password);

    Task<Void> sendPasswordResetEmail(String mail);

    Task<AuthResult> createUser(String mail, String password);
}
