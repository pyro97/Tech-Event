package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
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


public class AdminActivity extends Activity {
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
    Utente utente;
    LinearLayout gestione,banna,aggiungi;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
         gestione=findViewById(R.id.linkAdminEventi);
         banna=findViewById(R.id.linkAdminBan);
         aggiungi=findViewById(R.id.linkAdminMod);

         gestione.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(AdminActivity.this,GestioneEventiAdmin.class);
                 startActivity(intent);
             }
         });


         banna.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(AdminActivity.this,BannaUtente.class);
                 startActivity(intent);
             }
         });




        if(mAuth.getCurrentUser()!=null){
            DocumentReference docRef = db.collection("/utenti").document(mAuth.getCurrentUser().getEmail());

            if(isNetwork(AdminActivity.this)){
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        utente=documentSnapshot.toObject(Utente.class);

                        if(utente.getRuolo().equalsIgnoreCase("admin")){
                            TextView textView=new TextView(AdminActivity.this);
                            textView.setText("Gestione dei ruoli di amministrazione");
                            textView.setGravity(Gravity.LEFT);
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            );
                            textView.setLayoutParams(param);
                            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                            textView.setTextSize(18);
                            textView.setTypeface(null, Typeface.BOLD);

                            TextView textView1=new TextView(AdminActivity.this);
                            textView1.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            );
                            textView1.setLayoutParams(param1);
                            textView1.setTextColor(Color.GRAY);
                            textView1.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_action_arrow, 0);

                            aggiungi.addView(textView);
                            aggiungi.addView(textView1);
                            aggiungi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(AdminActivity.this,GestioneAdmin.class);
                                    startActivity(intent);
                                }
                            });



                        }




                        if(dialogo!=null)   cancelDialogo(dialogo);

                    }
                });
            }else{
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(AdminActivity.this,"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


            }



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
