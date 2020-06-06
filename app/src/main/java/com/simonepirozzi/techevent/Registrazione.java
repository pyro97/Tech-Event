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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.flags.Singletons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class Registrazione extends Activity {
    List<String> numberList=new ArrayList<>();
    TextView logga;
    EditText nome,cognome,mail,password,password1;
    AutoCompleteTextView citta;
    String result;
    Button registrati;
    String cittaNew;
LinearLayout lin;
    private FirebaseAuth mAuth;
   private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        logga=findViewById(R.id.link_logga);
        registrati=findViewById(R.id.registrati_button);
        nome=findViewById(R.id.Reg_Name);
        cognome=findViewById(R.id.Reg_Sur);
        mail=findViewById(R.id.Reg_Mail);
        password=findViewById(R.id.Reg_Pass);
        lin=findViewById(R.id.lreg);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();




        SpannableString content = new SpannableString(logga.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        logga.setText(content);


        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nome.getText().toString().length()!=0 && cognome.getText().toString().length()!=0
                         && mail.getText().toString().length()!=0
                        && password.getText().toString().length()!=0){

                    if(password.getText().toString().length()>5){

                            Intent intent=new Intent(Registrazione.this,RegistrazioneCity.class);
                            intent.putExtra("nome",nome.getText().toString());
                            intent.putExtra("cognome",cognome.getText().toString());
                            intent.putExtra("mail",mail.getText().toString());
                            intent.putExtra("password",password.getText().toString());

                            startActivity(intent);
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(Registrazione.this,"Attenzione","La password deve contenere almeno 6 caratteri",SweetAlertDialog.WARNING_TYPE);
                    }


                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(Registrazione.this,"Attenzione","Tutti i campi devono essere riempiti",SweetAlertDialog.WARNING_TYPE);
                }

            }
        });

        logga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Registrazione.this,Login.class);
                startActivity(intent);
            }
        });


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



}
