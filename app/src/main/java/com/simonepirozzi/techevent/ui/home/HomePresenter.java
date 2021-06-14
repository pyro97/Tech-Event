package com.simonepirozzi.techevent.ui.home;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.ui.main.MainContract;

public class HomePresenter implements HomeContract.Presenter {

    private HomeContract.View view;
    private Activity activity;
    private TinyDB tinyDB;
    private FirebaseUser firebaseUser;
    private MainRepository repository;

    public HomePresenter(Activity activity, HomeContract.View view) {
        this.activity = activity;
        this.view = view;
        this.tinyDB = TinyManager.getInstance(activity);
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
        this.repository = new MainRepository();
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument() {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public Task<QuerySnapshot> getEventsByProvince(String collection, String province) {
        return repository.getCollectionByProvince(collection,province);
    }

    @Override
    public Task<Void> deleteEvents(String document) {
        return repository.deleteDocument(FirestoreManager.EVENT_COLLECTION, document);
    }

    @Override
    public void getEvents(){
        getUserDocument().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                view.setLayoutUser(user);
                getEventsByProvince(FirestoreManager.EVENT_COLLECTION, user.getProvince()).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                           view.setLayoutListEvent(task.getResult().toObjects(Event.class));
                        } else {
                            view.cancelDialogo(null);
                        }
                    }
                });
            }
        });
    }
}
