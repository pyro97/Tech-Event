package com.simonepirozzi.techevent.ui.account;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.ui.event.publish.EventPublishFragment;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.ui.login.LoginActivity;
import com.simonepirozzi.techevent.utils.Utility;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountPresenter implements AccountContract.Presenter {

    private AccountContract.View view;
    private Activity activity;
    private FirebaseUser firebaseUser;
    private MainRepository repository;
    private TinyDB tinyDB;


    public AccountPresenter(Activity activity, AccountContract.View view) {
        this.repository = new MainRepository();
        this.activity = activity;
        this.view = view;
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
        this.tinyDB = TinyManager.getInstance(activity);
    }

    @Override
    public Task<QuerySnapshot> getEventById(String id) {
        return repository.getCollectionById(FirestoreManager.EVENT_COLLECTION,id);
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument() {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public Task<DocumentSnapshot> getUserDocumentCustom(String mail) {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, mail);
    }

    @Override
    public Task<QuerySnapshot> getUserCollection() {
        return repository.getCollection(FirestoreManager.USER_COLLECTION);
    }

    @Override
    public Task<Void> reauthenticate(AuthCredential credential) {
        return repository.reauthenticate(credential);
    }

    @Override
    public Task<Void> setUserDocument(User user) {
        return repository.setDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail(), user);
    }

    @Override
    public Task<Void> setEventDocument(String date, Event event) {
        return repository.setDocument(FirestoreManager.EVENT_COLLECTION, date, event);
    }

    @Override
    public void getAccount() {
        view.startDialog(activity.getString(R.string.loading_message), "", SweetAlertDialog.PROGRESS_TYPE);
        if (Utility.isNetwork(activity)) {
            getUserDocument().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    view.setAccountLayout(user);
                }
            });
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
        }
    }

    @Override
    public void banAccount(final String mail, boolean isBan) {
        if (isBan) {
            if (mail.length() > 0) {
                if (Utility.isNetwork(activity)) {
                    getUserDocumentCustom(mail).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (user.getRole().equalsIgnoreCase("bannato")) {
                                    view.startDialog("Attenzione", "L'utente è già stato bannato", SweetAlertDialog.ERROR_TYPE);
                                } else {
                                    if (user.getRole().equalsIgnoreCase("utente")) {
                                        user.setRole("bannato");
                                        getEventByUserCustom(mail).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<Event> eventList = queryDocumentSnapshots.toObjects(Event.class);
                                                if (eventList != null) {
                                                    for (Event e : eventList) {
                                                        e.setPriority(0);
                                                        e.setState("rifiutato");
                                                        setEventDocument(e.getPublishDate(),e);
                                                    }
                                                }
                                            }
                                        });
                                        setUserDocument(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                view.startDialog("", "Utente bannato", SweetAlertDialog.SUCCESS_TYPE);
                                            }
                                        });
                                    } else {
                                        view.startDialog("Attenzione", "Un'Admin non può essere bannato", SweetAlertDialog.ERROR_TYPE);
                                    }
                                }
                            } else {
                                view.startDialog("Attenzione", "Email non esistente", SweetAlertDialog.ERROR_TYPE);
                            }
                        }
                    });
                } else {
                    view.startDialog("Attenzione", "Problemi di connessione", SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                view.startDialog("Attenzione", "Completa il campo", SweetAlertDialog.WARNING_TYPE);
            }
        } else {
            if (mail.length() > 0) {
                if (Utility.isNetwork(activity)) {
                    getUserDocumentCustom(mail).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (!user.getRole().equalsIgnoreCase("bannato")) {
                                    view.startDialog("Attenzione", "L'utente non è bannato", SweetAlertDialog.ERROR_TYPE);
                                } else {
                                    user.setRole("utente");
                                    setUserDocument(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            view.startDialog("", "Utente reintegrato", SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    });
                                }
                            } else {
                                view.startDialog("Attenzione", "Email non esistente", SweetAlertDialog.ERROR_TYPE);
                            }
                        }
                    });
                } else {
                    view.startDialog("Attenzione", "Problemi di connessione", SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                view.startDialog("Attenzione", "Completa il campo", SweetAlertDialog.WARNING_TYPE);
            }
        }
    }

    @Override
    public void checkPublishEventFlow() {
        if (tinyDB.getString(TinyManager.PUBLISHING_EVENT) != null && tinyDB.getString(TinyManager.PUBLISHING_EVENT).length() > 0) {
            if (tinyDB.getString(TinyManager.PUBLISHING_EVENT).equalsIgnoreCase(TinyManager.YES)) {
                tinyDB.remove(TinyManager.PUBLISHING_EVENT);
                activity.getFragmentManager().beginTransaction().replace(R.id.frame_container, new EventPublishFragment()).commit();
            }
        }
    }

    @Override
    public void signOut() {
        repository.signOut();
    }

    @Override
    public Task<QuerySnapshot> getEventByUser() {
        return repository.getCollectionByUser(FirestoreManager.EVENT_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public Task<QuerySnapshot> getEventByUserCustom(String mail) {
        return repository.getCollectionByUser(FirestoreManager.EVENT_COLLECTION, mail);
    }

    @Override
    public Task<QuerySnapshot> getEventByState(String state) {
        return repository.getCollectionByState(FirestoreManager.EVENT_COLLECTION, state);
    }

    @Override
    public Task<Void> deleteEvents(String document) {
        return repository.deleteDocument(FirestoreManager.EVENT_COLLECTION, document);
    }

    @Override
    public Task<Void> deleteUser() {
        return repository.deleteDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }


    public void getProgressEvents() {
        getEventByState(FirestoreManager.STATE_PROGRESS).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Event> events = queryDocumentSnapshots.toObjects(Event.class);
                view.setEventLayout(events);
            }
        });
    }


    @Override
    public void saveEditCity(User user) {
        setUserDocument(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                view.startDialog(activity.getString(R.string.edit_save_title), activity.getString(R.string.edit_save_desc), SweetAlertDialog.SUCCESS_TYPE);
            }
        });
    }


    @Override
    public void saveEditAccount(final String name, final String surname) {
        if (Utility.isNetwork(activity)) {
            view.startDialog(activity.getString(R.string.loading_title), activity.getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
            if (name.length() != 0 && surname.length() != 0) {
                try {
                    getUserDocument().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                User user = task.getResult().toObject(User.class);
                                user.setName(name);
                                user.setSurname(surname);
                                if (Utility.isNetwork(activity)) {
                                    setUserDocument(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                view.startDialog(activity.getString(R.string.save_edit_title), activity.getString(R.string.save_edit_desc), SweetAlertDialog.SUCCESS_TYPE);
                                            } else {
                                                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.error_edit1), SweetAlertDialog.ERROR_TYPE);
                                            }
                                        }
                                    });
                                } else {
                                    view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.error_edit), SweetAlertDialog.ERROR_TYPE);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.error_edit), SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.empty_field), SweetAlertDialog.WARNING_TYPE);
            }
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
        }
    }

    @Override
    public void reauthenticate(String password) {
        if (password.length() > 0) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(firebaseUser.getEmail(), password);

            reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        getEventByUser().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<Event> deleteEvents = task.getResult().toObjects(Event.class);
                                    for (int i = 0; i < deleteEvents.size(); i++) {
                                        deleteEvents(deleteEvents.get(i).getPublishDate());
                                    }
                                    deleteUser().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Intent intent = new Intent(activity, LoginActivity.class);
                                                    activity.startActivity(intent);
                                                    activity.finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        view.cancelDialog();
                    }
                }
            });
        } else {
            view.cancelDialog();
        }
    }

    @Override
    public void getListAccount() {
        view.startDialog(activity.getString(R.string.loading_message), "", SweetAlertDialog.PROGRESS_TYPE);
        if (Utility.isNetwork(activity)) {
            getUserCollection().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    List<User> users = querySnapshot.toObjects(User.class);
                    for (User u : users) {
                        if (u.getRole().equalsIgnoreCase(FirestoreManager.BANNED_USER)) {
                            view.setAccountLayout(u);
                        }
                    }
                    view.cancelDialog();
                }
            });
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
        }
    }

    @Override
    public void getListAccountAdmin() {
        view.startDialog(activity.getString(R.string.loading_message), "", SweetAlertDialog.PROGRESS_TYPE);
        if (Utility.isNetwork(activity)) {
            getUserCollection().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    List<User> users = querySnapshot.toObjects(User.class);
                    for (User u : users) {
                        if (u.getRole().equalsIgnoreCase("admin") || u.getRole().equalsIgnoreCase("moderatore")) {
                            view.setAccountLayout(u);
                        }
                    }
                    view.cancelDialog();
                }
            });
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
        }
    }

    @Override
    public void setRole(String mail, final String role) {
        if(mail.length() > 0 && role.length() > 0){
            if (Utility.isNetwork(activity)) {
                getUserDocumentCustom(mail).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            if (user.getRole().equalsIgnoreCase(role)) {
                                view.startDialog("Attenzione", "Ruolo già associato", SweetAlertDialog.ERROR_TYPE);
                            } else {
                                user.setRole(role);
                                setUserDocument(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        view.startDialog( "", "Modifiche effettuate", SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                });
                            }
                        } else {
                            view.startDialog( "Attenzione", "Email non esistente", SweetAlertDialog.ERROR_TYPE);
                        }

                    }
                });
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
            }
        } else {
            view.startDialog( "Attenzione", "Completa il campo", SweetAlertDialog.WARNING_TYPE);
        }
    }

    @Override
    public void eventsById(String id){
        getEventById(id).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Event> events = task.getResult().toObjects(Event.class);
                    view.setEventLayout(events);
                }
            }
        });
    }

    @Override
    public void confirmEvent(Event e, final boolean isConfirm){
        setEventDocument(e.getPublishDate(),e).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (isConfirm){
                    view.startDialog("", "Evento pubblicato!", SweetAlertDialog.SUCCESS_TYPE);
                } else {
                    view.startDialog("", "Evento rifiutato!", SweetAlertDialog.SUCCESS_TYPE);
                }
            }
        });
    }

    @Override
    public void setFavouriteEvents(){
        tinyDB.putString("evento", "preferiti");
    }

}
