package com.simonepirozzi.techevent.ui.favourite;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.ui.search.SearchContract;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FavouritePresenter implements FavouriteContract.Presenter {

    private FavouriteContract.View view;
    private Activity activity;
    private TinyDB tinyDB;
    private FirebaseUser firebaseUser;
    private MainRepository repository;

    public FavouritePresenter(Activity activity, FavouriteContract.View view) {
        this.activity = activity;
        this.view = view;
        this.tinyDB = TinyManager.getInstance(activity);
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
        this.repository = new MainRepository();
    }


    @Override
    public Task<QuerySnapshot> getAllEvents() {
        return repository.getCollection(FirestoreManager.EVENT_COLLECTION);
    }

    @Override
    public Task<Void> setUserDocument(User user) {
        return repository.setDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail(), user);
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument() {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }

    public void getFavEvents(){
        getUserDocument().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                final User user = task.getResult().toObject(User.class);
                getAllEvents().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Event> evv = task.getResult().toObjects(Event.class);
                        view.setLayout(evv,user);
                    }
                });
            }
        });
    }

}
