package com.simonepirozzi.techevent.ui.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class EventoActivity extends Activity {
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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_info);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        data=findViewById(R.id.dataEvento);
        titolo=findViewById(R.id.titoloEvento);
        luogo=findViewById(R.id.luogoEvento);
        img=findViewById(R.id.fotoEvento);
        descrizione=findViewById(R.id.infoEvento);
        organizz=findViewById(R.id.organizzatoreEvento);
        orario=findViewById(R.id.oraEvento);
        costo=findViewById(R.id.costoEvento);
        contatto=findViewById(R.id.contattoEvento);
        back=findViewById(R.id.backEvento);
        tit=findViewById(R.id.titEvento);
        fav=findViewById(R.id.favouriteEvent);
        id=getIntent().getStringExtra("id");
        chiamante=getIntent().getStringExtra("chiamante");

        tit.setText(getIntent().getStringExtra("titoloExtra"));

        descrizione.setText(getIntent().getStringExtra("descrizioneExtra"));
        titolo.setText(getIntent().getStringExtra("titoloExtra"));
        organizz.setText(getIntent().getStringExtra("organizzExtra"));
        data.setText(getIntent().getStringExtra("dataExtra"));
        orario.setText(getIntent().getStringExtra("orarioExtra"));
        luogo.setText(getIntent().getStringExtra("luogoExtra"));
        if(getIntent().getStringExtra("costoExtra").equalsIgnoreCase("0"))    costo.setText("Gratuito");
        else    costo.setText(getIntent().getStringExtra("costoExtra"));
        descrizione.setText(getIntent().getStringExtra("descrizioneExtra"));
        contatto.setText(getIntent().getStringExtra("contattoExtra"));
        tinyDB=new TinyDB(EventoActivity.this);




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        db.collection("/eventi").whereEqualTo("id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Event> eventi=new ArrayList<>();
                    eventi=task.getResult().toObjects(Event.class);
                    if(eventi.size()==1){
                        for(int i=0;i<eventi.size();i++){
                            e=eventi.get(i);
                        }
                    }

                    titolo.setText(e.getTitle());
                    if(e.getPhoto().length()>0){
                        try {
                            byte [] encodeByte= Base64.decode(e.getPhoto(),Base64.DEFAULT);
                            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                            img.setImageBitmap(bitmap);

                        } catch(Exception e) {
                            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_ph));

                            e.getMessage();
                        }
                    }else{
                        img.setImageDrawable(getResources().getDrawable(R.drawable.ic_ph));

                    }
                }
            }
        });

        db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User u=task.getResult().toObject(User.class);
               if(u.getPreferences()!=null && u.getPreferences().size()>0){
                   boolean trovato=false;
                   for(int i = 0; i<u.getPreferences().size(); i++){
                       if(u.getPreferences().get(i).getId().equalsIgnoreCase(id)){
                           trovato=true;
                           fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_favorite),null);
                       }
                   }
                   if(!trovato){
                       fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_add_favorite),null);

                   }
               }else{
                   fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_add_favorite),null);

               }

            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        User u=task.getResult().toObject(User.class);
                        if(u.getPreferences()!=null && u.getPreferences().size()>0){
                            boolean trovato=false;
                            for(int i = 0; i<u.getPreferences().size(); i++){
                                if(u.getPreferences().get(i).getId().equalsIgnoreCase(id)){
                                    trovato=true;
                                    ArrayList<Event> preferitiNew=new ArrayList<>();
                                    preferitiNew=u.getPreferences();
                                    preferitiNew.remove(i);
                                    u.setPreferences(preferitiNew);
                                    db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).set(u, SetOptions.merge());
                                    fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_add_favorite),null);
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    dialogo=startDialogo(EventoActivity.this,"Evento rimosso dai preferiti!","",SweetAlertDialog.SUCCESS_TYPE);
                                }
                            }
                            if(!trovato){
                                ArrayList<Event> preferitiNew=new ArrayList<>();
                                preferitiNew=u.getPreferences();

                                preferitiNew.add(e);
                                u.setPreferences(preferitiNew);
                                db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).set(u, SetOptions.merge());
                                fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_favorite),null);
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                dialogo=startDialogo(EventoActivity.this,"Evento aggiunto ai preferiti!","",SweetAlertDialog.SUCCESS_TYPE);
                            }
                        }else{
                            ArrayList<Event> preferiti=new ArrayList<>();
                            preferiti.add(e);
                            u.setPreferences(preferiti);
                            db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).set(u, SetOptions.merge());
                            fav.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_favorite),null);
                            if(dialogo!=null)   cancelDialogo(dialogo);
                            dialogo=startDialogo(EventoActivity.this,"Evento aggiunto ai preferiti!","",SweetAlertDialog.SUCCESS_TYPE);
                        }

                    }
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(chiamante.equalsIgnoreCase("preferiti")){
            tinyDB.putString("evento","preferiti");
            Intent intent=new Intent(EventoActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}
