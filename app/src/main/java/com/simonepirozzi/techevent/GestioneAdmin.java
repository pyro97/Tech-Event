package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class GestioneAdmin extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;
    AppCompatImageView img,back;
    TextView data,titolo,luogo,descrizione,organizz,partecip,orario,costo,contatto,tit,fav;
    String id,chiamante;
    Evento e;
    TinyDB tinyDB;
    EditText email;
    Spinner spinner;
    Button mod;
    Utente utente;
    LinearLayout gestione,banna,aggiungi;
    ListView listView;
    CustomAdapterListaAdmin customAdapterListaAdmin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestioneadmin_activity);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

        email=findViewById(R.id.mail_gest_admin);
        spinner=findViewById(R.id.spinner_gest_admin);
        mod=findViewById(R.id.modifica_ruolo);
        listView=findViewById(R.id.listViewAdmin);
        customAdapterListaAdmin=new CustomAdapterListaAdmin(GestioneAdmin.this,R.layout.list_element,new ArrayList<Utente>());
        listView.setAdapter(customAdapterListaAdmin);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(GestioneAdmin.this,"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        db.collection("/utenti").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Utente> utenti=queryDocumentSnapshots.toObjects(Utente.class);
                for(Utente u:utenti){
                    if(u.getRuolo().equalsIgnoreCase("admin") || u.getRuolo().equalsIgnoreCase("moderatore")){
                        customAdapterListaAdmin.add(u);
                    }
                }
                if(dialogo!=null)   cancelDialogo(dialogo);

            }
        });

        ArrayList<String> categorie = new ArrayList<>();
        categorie.add("admin");
        categorie.add("moderatore");
        categorie.add("utente");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(GestioneAdmin.this, android.R.layout.simple_spinner_dropdown_item, categorie);
        spinner.setAdapter(arrayAdapter);

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().length()>0 && spinner.getSelectedItem().toString().length()>0){
                    final DocumentReference docRef = db.collection("/utenti").document(email.getText().toString());
                    if(isNetwork(GestioneAdmin.this)){
                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                utente=documentSnapshot.toObject(Utente.class);
                                if(utente!=null){
                                    if(utente.getRuolo().equalsIgnoreCase(spinner.getSelectedItem().toString())){
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(GestioneAdmin.this,"Attenzione","Ruolo già associato",SweetAlertDialog.ERROR_TYPE);
                                    }else{
                                        utente.setRuolo(spinner.getSelectedItem().toString());
                                        docRef.set(utente, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(dialogo!=null)   cancelDialogo(dialogo);
                                                dialogo=startDialogo(GestioneAdmin.this,"","Modifiche effettuate",SweetAlertDialog.SUCCESS_TYPE);

                                            }
                                        });
                                    }


                                }else{
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    dialogo=startDialogo(GestioneAdmin.this,"Attenzione","Email non esistente",SweetAlertDialog.ERROR_TYPE);
                                }



                            }
                        });
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(GestioneAdmin.this,"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(GestioneAdmin.this,"Attenzione","Completa il campo",SweetAlertDialog.WARNING_TYPE);
                }

            }
        });





    }


    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("Modifiche effettuate")){
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
