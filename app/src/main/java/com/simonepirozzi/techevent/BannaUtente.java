package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class BannaUtente extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;
    AppCompatImageView img,back;
    TextView data,titolo,luogo,descrizione,organizz,partecip,orario,costo,contatto,tit,fav;
    String id,chiamante;
    Event e;
    TinyDB tinyDB;
    EditText email;
    Spinner spinner;
    Button mod,rei;
    User user;
    LinearLayout gestione,banna,aggiungi;
    ListView listView;
    CustomAdapterListaAdmin customAdapterListaAdmin;
    ListView listaBannati;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banna_activity);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

        email=findViewById(R.id.mail_banna);
        mod=findViewById(R.id.banna);
        rei=findViewById(R.id.rein);
        listaBannati=findViewById(R.id.listViewBannati);
        customAdapterListaAdmin=new CustomAdapterListaAdmin(BannaUtente.this,R.layout.list_element,new ArrayList<User>());
        listaBannati.setAdapter(customAdapterListaAdmin);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(BannaUtente.this,"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        db.collection("/utenti").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> utenti=queryDocumentSnapshots.toObjects(User.class);
                for(User u:utenti){
                    if(u.getRole().equalsIgnoreCase("bannato")){
                        customAdapterListaAdmin.add(u);
                    }
                }
                if(dialogo!=null)   cancelDialogo(dialogo);

            }
        });

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().length()>0){
                    final DocumentReference docRef = db.collection("/utenti").document(email.getText().toString());
                    if(isNetwork(BannaUtente.this)){
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user =documentSnapshot.toObject(User.class);
                                if(user !=null){
                                    if(user.getRole().equalsIgnoreCase("bannato")){
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(BannaUtente.this,"Attenzione","L'utente è già stato bannato",SweetAlertDialog.ERROR_TYPE);
                                    }else{

                                        if(user.getRole().equalsIgnoreCase("utente")){
                                            user.setRole("bannato");
                                            db.collection("/eventi").whereEqualTo("email", user.getMail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    List<Event> eventList =queryDocumentSnapshots.toObjects(Event.class);
                                                    if(eventList !=null){
                                                        for(Event e: eventList){
                                                            e.setPriority(0);
                                                            e.setState("rifiutato");
                                                            db.collection("/eventi").document(e.getPublishDate()).set(e,SetOptions.merge());
                                                        }
                                                    }
                                                }
                                            });
                                            docRef.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                                    dialogo=startDialogo(BannaUtente.this,"","Utente bannato",SweetAlertDialog.SUCCESS_TYPE);

                                                }
                                            });
                                        }else{
                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                            dialogo=startDialogo(BannaUtente.this,"Attenzione","Un'Admin non può essere bannato",SweetAlertDialog.ERROR_TYPE);
                                        }

                                    }


                                }else{
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    dialogo=startDialogo(BannaUtente.this,"Attenzione","Email non esistente",SweetAlertDialog.ERROR_TYPE);
                                }



                            }
                        });
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(BannaUtente.this,"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(BannaUtente.this,"Attenzione","Completa il campo",SweetAlertDialog.WARNING_TYPE);
                }

            }
        });

        rei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().length()>0){
                    final DocumentReference docRef = db.collection("/utenti").document(email.getText().toString());
                    if(isNetwork(BannaUtente.this)){
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                user =documentSnapshot.toObject(User.class);
                                if(user !=null){
                                    if(!user.getRole().equalsIgnoreCase("bannato")){
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(BannaUtente.this,"Attenzione","L'utente non è bannato",SweetAlertDialog.ERROR_TYPE);
                                    }else{
                                        user.setRole("utente");
                                        docRef.set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(dialogo!=null)   cancelDialogo(dialogo);
                                                dialogo=startDialogo(BannaUtente.this,"","Utente reintegrato",SweetAlertDialog.SUCCESS_TYPE);

                                            }
                                        });
                                    }


                                }else{
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    dialogo=startDialogo(BannaUtente.this,"Attenzione","Email non esistente",SweetAlertDialog.ERROR_TYPE);
                                }



                            }
                        });
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(BannaUtente.this,"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(BannaUtente.this,"Attenzione","Completa il campo",SweetAlertDialog.WARNING_TYPE);
                }

            }
        });



    }


    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("Utente bannato")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            }else if(message.equalsIgnoreCase("Utente reintegrato")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            }else{
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }



        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    public void cancelDialogo(SweetAlertDialog s){

        s.cancel();
    }



    public boolean isNetwork(Context context){
        ConnectivityManager manager =(ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null && activeNetwork.getState()== NetworkInfo.State.CONNECTED)   return true;
        else    return false;
    }

}
