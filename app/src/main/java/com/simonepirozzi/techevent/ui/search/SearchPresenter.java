package com.simonepirozzi.techevent.ui.search;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.simonepirozzi.techevent.ui.home.HomeContract;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View view;
    private Activity activity;
    private TinyDB tinyDB;
    private FirebaseUser firebaseUser;
    private MainRepository repository;

    public SearchPresenter(Activity activity, SearchContract.View view) {
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
    public Task<QuerySnapshot> getEventsByParam(String key, String param) {
        return repository.getDocumentByParam(FirestoreManager.EVENT_COLLECTION,key,param);
    }

    @Override
    public Task<QuerySnapshot> getEventsByMultipleParam(String key, String param, String key1, String param1) {
        return repository.getDocumentByMultParam(FirestoreManager.EVENT_COLLECTION,key,param,key1,param1);
    }

    @Override
    public void setList(ArrayList<Object> lista){
        tinyDB.remove("lista");
        tinyDB.putListObject("lista", lista);
    }

    @Override
    public void getListEvents() {
        view.setLayoutEvents(tinyDB.getListObject("lista", Event.class));
    }

    @Override
    public void getListEventsSize() {
        if (tinyDB.getListObject("lista", Event.class).size() == 0) {
            view.cancelDialogo(null);
            view.startDialogo(activity, "Nessun evento trovato!", "", SweetAlertDialog.WARNING_TYPE);
        } else {
            view.cancelDialogo(null);
        }
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument() {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public User getUser() {
        final User[] user = {new User()};
        getUserDocument().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user[0] = task.getResult().toObject(User.class);
            }
        });
        return user[0];
    }

}
