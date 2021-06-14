package com.simonepirozzi.techevent.ui.event;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.ui.event.publish.PublishEventActivity;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventPresenter implements EventContract.Presenter {

    private EventContract.View view;
    private Activity activity;
    private TinyDB tinyDB;
    private FirebaseUser firebaseUser;
    private MainRepository repository;

    public EventPresenter(Activity activity, EventContract.View view) {
        this.activity = activity;
        this.view = view;
        this.tinyDB = TinyManager.getInstance(activity);
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
        this.repository = new MainRepository();
    }

    @Override
    public Task<Void> setEventDocument(String date, Event event) {
        return repository.setDocument(FirestoreManager.EVENT_COLLECTION, date, event);
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

    @Override
    public Task<QuerySnapshot> getEventDocumentByEmail() {
        return repository.getCollectionByEmail(FirestoreManager.EVENT_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public void checkPublishEvent(){
        if (tinyDB.getString("mainTogestione") != null && tinyDB.getString("mainTogestione").length() > 0) {
            if (tinyDB.getString("mainTogestione").equalsIgnoreCase("si")) {
                tinyDB.remove("mainTogestione");
                view.cancelDialogo(null);
                Intent intent = new Intent(activity, PublishEventActivity.class);
                activity.startActivity(intent);
            }
        }
    }

    @Override
    public void publishEvent(){
        getUserDocument().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                view.setLayoutUser(documentSnapshot.toObject(User.class));
            }
        });
    }

    @Override
    public void publishEventStep2(String date, Event e){
        setEventDocument(date,e).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                view.cancelDialogo(null);
                if (task.isSuccessful()) {
                    view.startDialogo(activity, "Evento in fase di controllo", "L'evento è in attesa di essere confermato. Verrai avvisato attraverso una mail", SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    view.startDialogo(activity, "Attenzione", "L'evento non è stato aggiunto. Riprova", SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }

    @Override
    public void checkManageEvent(){
        if(tinyDB.getString("evento")!=null){
            if(tinyDB.getString("evento").equalsIgnoreCase("gestioneEvento")){
                tinyDB.remove("evento");
                activity.finish();
                activity.startActivity(activity.getIntent());
            }
        }
    }

    public void getEventsByMail() {
        getEventDocumentByEmail().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    view.setLayoutEvents(task.getResult().toObjects(Event.class));
                }
            }
        });
    }

}
