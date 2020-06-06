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
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecuperaPassword extends Activity {
    TextView login,dimenticata;
    Button invia;
    EditText mail;
    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_pass);
        login=findViewById(R.id.link_rec_login);
        invia=findViewById(R.id.invia_rec);
        mail=findViewById(R.id.mail_rec);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



        SpannableString content = new SpannableString(login.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        login.setText(content);

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(RecuperaPassword.this)){
                    if(mail.getText().toString().length()!=0){
                        DocumentReference documentReference=db.collection("/utenti")
                                .document(mail.getText().toString());
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(RecuperaPassword.this,"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(RecuperaPassword.this,"Email inviata con successo","Controlla la tua posta e segui le istruzioni presenti nell'email che ti abbiamo inviato",SweetAlertDialog.SUCCESS_TYPE);
                                        mAuth.sendPasswordResetEmail(mail.getText().toString());
                                    } else {
                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                        dialogo=startDialogo(RecuperaPassword.this,"Attenzione","Email non esistente",SweetAlertDialog.ERROR_TYPE);

                                    }
                                } else {
                                    Log.d("ok", "get failed with ", task.getException());
                                }
                            }
                        });

                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(RecuperaPassword.this,"Attenzione","Tutti i campi devono essere riempiti",SweetAlertDialog.WARNING_TYPE);
                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo= startDialogo(RecuperaPassword.this,"Attenzione","Problemi di connessione!",SweetAlertDialog.ERROR_TYPE);
                }

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RecuperaPassword.this,Login.class);
                startActivity(intent);
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if(dialogo!=null) cancelDialogo(dialogo);
            Intent intent = new Intent(RecuperaPassword.this, MainActivity.class);
            startActivity(intent);
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

