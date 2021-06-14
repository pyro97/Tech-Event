package com.simonepirozzi.techevent.data.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.utils.Constants;

public class MainRepository implements MainRepositoryInterface{

    private FirebaseFirestore instance;
    private FirebaseAuth authInstance;


    public MainRepository(){
        this.instance = FirestoreManager.getInstance();
        this.authInstance = FirestoreManager.getAuthInstance();
    }


    @Override
    public Task<QuerySnapshot> getCollection(String collection) {
        return instance.collection(collection).get();
    }

    @Override
    public Task<QuerySnapshot> getCollectionByUser(String collection, String mail) {
        return instance.collection(collection).whereEqualTo(Constants.TAG_EMAIL,mail).get();
    }

    @Override
    public Task<DocumentSnapshot> getDocument(String collection, String document) {
        return instance.collection(collection).document(document).get();
    }

    @Override
    public Task<QuerySnapshot> getCollectionByState(String collection, String state) {
        return instance.collection(collection).whereEqualTo(Constants.TAG_STATE,state).get();
    }

    @Override
    public Task<Void> setDocument(String collection, String document, Object object) {
        return instance.collection(collection).document(document).set(object, SetOptions.merge());
    }

    @Override
    public Task<Void> deleteDocument(String collection, String document) {
        return instance.collection(collection).document(document).delete();
    }

    @Override
    public Task<AuthResult> signIn(String username, String password){
        return authInstance.signInWithEmailAndPassword(username,password);
    }

    @Override
    public Task<Void> reauthenticate(AuthCredential credential){
        return authInstance.getCurrentUser().reauthenticate(credential);
    }

    @Override
    public void signOut(){
        authInstance.signOut();
    }

    @Override
    public Task<Void> sendPasswordResetEmail(String mail) {
        return authInstance.sendPasswordResetEmail(mail);
    }

    @Override
    public Task<AuthResult> createUser(String mail, String password) {
        return authInstance.createUserWithEmailAndPassword(mail,password);
    }
}
