package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.CustomAdapterListaAdmin;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserBanActivity extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    EditText email;
    Button mod, rei;
    User user;
    CustomAdapterListaAdmin customAdapterListaAdmin;
    ListView listaBannati;
    AccountPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banna_activity);
        email = findViewById(R.id.mail_banna);
        mod = findViewById(R.id.banna);
        rei = findViewById(R.id.rein);
        mPresenter = new AccountPresenter(this,this);
        listaBannati = findViewById(R.id.listViewBannati);
        customAdapterListaAdmin = new CustomAdapterListaAdmin(UserBanActivity.this, R.layout.list_element, new ArrayList<User>());
        listaBannati.setAdapter(customAdapterListaAdmin);

        mPresenter.getListAccount();

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().length() > 0) {
                    final DocumentReference docRef = db.collection("/utenti").document(email.getText().toString());
                    if (isNetwork(UserBanActivity.this)) {
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    if (user.getRole().equalsIgnoreCase("bannato")) {
                                        if (dialog != null) cancelDialogo(dialog);
                                        dialog = startDialogo(UserBanActivity.this, "Attenzione", "L'utente è già stato bannato", SweetAlertDialog.ERROR_TYPE);
                                    } else {

                                        if (user.getRole().equalsIgnoreCase("utente")) {
                                            user.setRole("bannato");
                                            db.collection("/eventi").whereEqualTo("email", user.getMail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<Event> eventList = queryDocumentSnapshots.toObjects(Event.class);
                                                    if (eventList != null) {
                                                        for (Event e : eventList) {
                                                            e.setPriority(0);
                                                            e.setState("rifiutato");
                                                            db.collection("/eventi").document(e.getPublishDate()).set(e, SetOptions.merge());
                                                        }
                                                    }
                                                }
                                            });
                                            docRef.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (dialog != null) cancelDialogo(dialog);
                                                    dialog = startDialogo(UserBanActivity.this, "", "Utente bannato", SweetAlertDialog.SUCCESS_TYPE);

                                                }
                                            });
                                        } else {
                                            if (dialog != null) cancelDialogo(dialog);
                                            dialog = startDialogo(UserBanActivity.this, "Attenzione", "Un'Admin non può essere bannato", SweetAlertDialog.ERROR_TYPE);
                                        }

                                    }


                                } else {
                                    if (dialog != null) cancelDialogo(dialog);
                                    dialog = startDialogo(UserBanActivity.this, "Attenzione", "Email non esistente", SweetAlertDialog.ERROR_TYPE);
                                }


                            }
                        });
                    } else {
                        if (dialog != null) cancelDialogo(dialog);
                        dialog = startDialogo(UserBanActivity.this, "Attenzione", "Problemi di connessione", SweetAlertDialog.ERROR_TYPE);


                    }
                } else {
                    if (dialog != null) cancelDialogo(dialog);
                    dialog = startDialogo(UserBanActivity.this, "Attenzione", "Completa il campo", SweetAlertDialog.WARNING_TYPE);
                }

            }
        });

        rei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().length() > 0) {
                    final DocumentReference docRef = db.collection("/utenti").document(email.getText().toString());
                    if (isNetwork(UserBanActivity.this)) {
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user = documentSnapshot.toObject(User.class);
                                if (user != null) {
                                    if (!user.getRole().equalsIgnoreCase("bannato")) {
                                        if (dialog != null) cancelDialogo(dialog);
                                        dialog = startDialogo(UserBanActivity.this, "Attenzione", "L'utente non è bannato", SweetAlertDialog.ERROR_TYPE);
                                    } else {
                                        user.setRole("utente");
                                        docRef.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (dialog != null) cancelDialogo(dialog);
                                                dialog = startDialogo(UserBanActivity.this, "", "Utente reintegrato", SweetAlertDialog.SUCCESS_TYPE);

                                            }
                                        });
                                    }


                                } else {
                                    if (dialog != null) cancelDialogo(dialog);
                                    dialog = startDialogo(UserBanActivity.this, "Attenzione", "Email non esistente", SweetAlertDialog.ERROR_TYPE);
                                }


                            }
                        });
                    } else {
                        if (dialog != null) cancelDialogo(dialog);
                        dialog = startDialogo(UserBanActivity.this, "Attenzione", "Problemi di connessione", SweetAlertDialog.ERROR_TYPE);


                    }
                } else {
                    if (dialog != null) cancelDialogo(dialog);
                    dialog = startDialogo(UserBanActivity.this, "Attenzione", "Completa il campo", SweetAlertDialog.WARNING_TYPE);
                }

            }
        });


    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase("caricamento")) {
            if (message.equalsIgnoreCase("Utente bannato")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            } else if (message.equalsIgnoreCase("Utente reintegrato")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            } else {
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }


        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    @Override
    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void setAccountLayout(User user) {
        customAdapterListaAdmin.add(user);
    }

    @Override
    public void setEventLayout(List<Event> events) {

    }

}
