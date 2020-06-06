package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class RegistrazioneCity extends Activity {
    List<String> numberList=new ArrayList<>();
    List<String> newlist=new ArrayList<>();

    TextView cittaScelta;
    String nome,cognome,mail,password,city,provincia;
    EditText citta;
    String result;
    Button registrati;
    ListView listaCitta;
    ArrayAdapter<String> adapter;
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
        setContentView(R.layout.activity_reg_city);

        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

        nome=getIntent().getStringExtra("nome");
        cognome=getIntent().getStringExtra("cognome");
        mail=getIntent().getStringExtra("mail");
        password=getIntent().getStringExtra("password");

        citta=findViewById(R.id.Reg_City);
        cittaScelta=findViewById(R.id.cityScelta);
        listaCitta=findViewById(R.id.listViewCittaReg);
        registrati=findViewById(R.id.registrati_button_città);


        getJson();
        adapter=new ArrayAdapter<>(this,R.layout.custom_list_item_view,R.id.text_view_list_item,numberList);
        listaCitta.setAdapter(adapter);

        listaCitta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cittaScelta.setText(listaCitta.getItemAtPosition(position).toString());
            }
        });

        citta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cittaScelta.getText().toString().length()!=0){

                    if(!cittaScelta.getText().toString().equalsIgnoreCase("Nessuna")){
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(RegistrazioneCity.this,"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);

                        if(isNetwork(RegistrazioneCity.this)){
                            provincia=cittaScelta.getText().toString().substring(cittaScelta.getText().toString().indexOf(",")+1);
                            city=cittaScelta.getText().toString().substring(0,cittaScelta.getText().toString().indexOf(","));

                            mAuth.createUserWithEmailAndPassword(mail,password)
                                    .addOnCompleteListener(RegistrazioneCity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                user = mAuth.getCurrentUser();
                                                Log.d("creazione", "createUserWithEmail:success");
                                                user.sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("mail", "Email sent.");
                                                                    Utente ut=new Utente(nome,cognome,city,mail,"utente",provincia);
                                                                    db.collection("/utenti").document(mail)
                                                                            .set(ut, SetOptions.merge())
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Log.d("ok", "DocumentSnapshot successfully written!");
                                                                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                                                                    dialogo=startDialogo(RegistrazioneCity.this,"Registrazione avvenuta con successo","Per accedere verifica il tuo account tramite l'email che ti abbiamo inviato",SweetAlertDialog.SUCCESS_TYPE);
                                                                                   //  updateUI(user);
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Log.w("ok", "Error writing document", e);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            } else {
                                                if(dialogo!=null)   cancelDialogo(dialogo);
                                                dialogo=startDialogo(RegistrazioneCity.this,"Attenzione","Email già esistente. Utilizza un'altra mail",SweetAlertDialog.WARNING_TYPE);
                                                updateUI(null);

                                            }
                                        }
                                    });
                        }else{
                            if(dialogo!=null)   cancelDialogo(dialogo);
                            dialogo= startDialogo(RegistrazioneCity.this,"Attenzione","Problemi di connessione!",SweetAlertDialog.ERROR_TYPE);
                        }






                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(RegistrazioneCity.this,"Attenzione","Devi selezionare una città per poter procedere",SweetAlertDialog.WARNING_TYPE);
                    }


                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(RegistrazioneCity.this,"Attenzione","Devi selezionare una città per poter procedere",SweetAlertDialog.WARNING_TYPE);
                }
            }
        });









    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            if(dialogo!=null) cancelDialogo(dialogo);
            Intent intent=new Intent(RegistrazioneCity.this,Login.class);
            startActivity(intent);
            finish();
        }

    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("Per accedere verifica il tuo account tramite l'email che ti abbiamo inviato")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        updateUI(user);
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

    public void getJson(){
        String json;
        try{
            InputStream is=getAssets().open("com.json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            json=new String(buffer,"UTF-8");
            JSONArray jsonArray=new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                numberList.add(jsonObject
                        .getString("comune")+","+jsonObject.getString("provincia"));
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public boolean isNetwork(Context context){
        ConnectivityManager manager =(ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null && activeNetwork.getState()== NetworkInfo.State.CONNECTED)   return true;
        else    return false;
    }

}
