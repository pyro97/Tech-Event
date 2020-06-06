package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends Activity {
    TextView registrati,dimenticata;
    Button accedi;
    EditText username,pass;
    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    SweetAlertDialog dialogo;
    Utente utente;
    private FirebaseFirestore db;
    FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        registrati=findViewById(R.id.link_rec_login);
        dimenticata=findViewById(R.id.link_recupera);
        accedi=findViewById(R.id.accedi);
        username=findViewById(R.id.Login_Username);
        pass=findViewById(R.id.Login_Password);
        db = FirebaseFirestore.getInstance();

        FirebaseApp.initializeApp(getApplicationContext());
        mAuth= FirebaseAuth.getInstance();
        SpannableString content = new SpannableString(dimenticata.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        dimenticata.setText(content);

        if(mAuth.getCurrentUser()!=null){
            if(mAuth.getCurrentUser().isEmailVerified()){
                if(isNetwork(Login.this)){
                    dialogo= startDialogo(Login.this,"Carico Profilo","caricamento",SweetAlertDialog.PROGRESS_TYPE);

                    db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            utente=documentSnapshot.toObject(Utente.class);
                            if(!utente.getRuolo().equalsIgnoreCase("bannato")){
                                updateUI(mAuth.getCurrentUser());

                            }else{
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                dialogo= startDialogo(Login.this,"Attenzione","Sei bannato. Attualmente non puoi accedere",SweetAlertDialog.ERROR_TYPE);
                            }
                        }
                    });
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo= startDialogo(Login.this,"Attenzione","Problemi di connessione!",SweetAlertDialog.ERROR_TYPE);
                }
            }else{
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(Login.this,"Attenzione","Per accedere, verifica prima il tuo account attraverso la mail che hai ricevuto",SweetAlertDialog.WARNING_TYPE);

            }
        }

        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(Login.this,"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                Intent intent=new Intent(Login.this,Registrazione.class);
                startActivity(intent);

                if(dialogo!=null)   cancelDialogo(dialogo);

            }
        });

        dimenticata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(Login.this,"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                Intent intent=new Intent(Login.this,RecuperaPassword.class);
                startActivity(intent);
                if(dialogo!=null)   cancelDialogo(dialogo);

            }
        });
        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(Login.this)){
                    if(username.getText().toString().length()!=0 && pass.getText().toString().length()!=0){
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(Login.this,"Accesso...","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                        mAuth.signInWithEmailAndPassword(username.getText().toString(),pass.getText().toString())
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("LOG", "signInWithEmail:success");
                                             user = mAuth.getCurrentUser();
                                            if(user.isEmailVerified()){
                                                db.collection("/utenti").document(mAuth.getCurrentUser().getEmail())
                                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        utente=documentSnapshot.toObject(Utente.class);
                                                        if(!utente.getRuolo().equalsIgnoreCase("bannato")){
                                                            updateUI(user);

                                                        }else{
                                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                                            dialogo= startDialogo(Login.this,"Attenzione","Sei bannato. Attualmente non puoi accedere",SweetAlertDialog.ERROR_TYPE);
                                                        }
                                                    }
                                                });

                                            }else{
                                                if(dialogo!=null)   cancelDialogo(dialogo);
                                                dialogo=startDialogo(Login.this,"Attenzione","Per poter effettuare l'accesso devi verificare il tuo account attraverso la mail che hai ricevuto",SweetAlertDialog.WARNING_TYPE);
                                            }
                                        } else {
                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                            dialogo=startDialogo(Login.this,"Dati non corretti","Password e/o e-mail non corretti. Riprova",SweetAlertDialog.ERROR_TYPE);
                                            updateUI(null);
                                        }
                                    }
                                });
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(Login.this,"Attenzione","Tutti i campi devono essere riempiti",SweetAlertDialog.WARNING_TYPE);
                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo= startDialogo(Login.this,"Attenzione","Problemi di connessione!",SweetAlertDialog.ERROR_TYPE);

                }



                }



        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
           if(dialogo!=null) cancelDialogo(dialogo);
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(!message.equalsIgnoreCase("caricamento")){
            pDialog.setContentText(message);
            pDialog.setConfirmText("Ok");
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

