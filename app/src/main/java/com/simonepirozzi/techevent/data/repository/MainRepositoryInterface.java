package com.simonepirozzi.techevent.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface MainRepositoryInterface {
    Task<QuerySnapshot> getCollection(String collection);

    Task<QuerySnapshot> getCollectionByUser(String collection, String mail);

    Task<DocumentSnapshot> getDocument(String collection, String document);

    Task<QuerySnapshot> getCollectionByState(String collection, String state);

    Task<QuerySnapshot> getCollectionById(String collection, String id);

    Task<QuerySnapshot> getCollectionByEmail(String collection, String email);

    Task<QuerySnapshot> getCollectionByProvince(String collection, String province);

    Task<Void> setDocument(String collection, String document, Object object);

    Task<Void> deleteDocument(String collection, String document);

    Task<AuthResult> signIn(String username, String password);

    Task<Void> reauthenticate (AuthCredential credential);

    void signOut();

    Task<Void> sendPasswordResetEmail(String mail);

    Task<AuthResult> createUser(String mail, String password);

    Task<QuerySnapshot> getDocumentByParam(String eventCollection, String key, String param);

    Task<QuerySnapshot> getDocumentByMultParam(String eventCollection, String key, String param, String key1, String param1);
}
