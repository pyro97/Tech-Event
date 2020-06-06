package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.View;
import android.widget.ListView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class RicercaActivity extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;
    AppCompatImageView img,back;
    TextView data,titolo,luogo,descrizione,organizz,partecip,cat,orario,costo,contatto,tit,fav;
    String id,chiamante;
    Evento e;
    TinyDB tinyDB;
    ListView listView;
    List<Evento> listaFinale;
    Utente utente;
    CustomAdapterRicerca customAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ricerca);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();

        tinyDB=new TinyDB(RicercaActivity.this);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(RicercaActivity.this,"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        customAdapter=new CustomAdapterRicerca(RicercaActivity.this,R.layout.list_element_ricerca,new ArrayList<Evento>());
        listView=findViewById(R.id.listViewRicerca);
        listView.setAdapter(customAdapter);



        listaFinale=new ArrayList<>();


        for(int i=0;i<tinyDB.getListObject("lista",Evento.class).size();i++){
            Evento e= (Evento) tinyDB.getListObject("lista",Evento.class).get(i);
            listaFinale.add(e);
        }

        db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                utente=task.getResult().toObject(Utente.class);
            }
        });

        Collections.sort(listaFinale, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                try {
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
                    Date data1= simpleDateFormat.parse(o1.getData());
                    Date data2= simpleDateFormat.parse(o2.getData());
                    return data1.compareTo(data2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        Collections.sort(listaFinale, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                try {

                    if(o1.getData().equalsIgnoreCase(o2.getData())){
                        if(o1.getPriorità()>o2.getPriorità()){
                            return -1;
                        }else if(o1.getPriorità()<o2.getPriorità()){
                            return 1;
                        }else return 0;
                    }else   return 0;



                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        Collections.sort(listaFinale, new Comparator<Evento>() {
            @Override
            public int compare(Evento o1, Evento o2) {
                try {

                    if(o1.getData().equalsIgnoreCase(o2.getData())
                            && o1.getPriorità()==o2.getPriorità()){
                        if(o1.getCitta().equalsIgnoreCase(utente.getCittà())
                                && o2.getCitta().equalsIgnoreCase(utente.getCittà())){
                            return 0;
                        }else if(!o1.getCitta().equalsIgnoreCase(utente.getCittà())
                                && !o2.getCitta().equalsIgnoreCase(utente.getCittà())){
                            return 0;
                        }else if(o1.getCitta().equalsIgnoreCase(utente.getCittà())
                                && !o2.getCitta().equalsIgnoreCase(utente.getCittà())){
                            return -1;
                        }else if(!o1.getCitta().equalsIgnoreCase(utente.getCittà())
                                && o2.getCitta().equalsIgnoreCase(utente.getCittà())){
                            return -1;
                        }

                        else return 1;


                    }else   return 0;



                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });





        for(int j=0;j<listaFinale.size();j++){
            customAdapter.add(listaFinale.get(j));
        }

        if(tinyDB.getListObject("lista",Evento.class).size()==0){
            if(dialogo!=null)   cancelDialogo(dialogo);
            dialogo=startDialogo(RicercaActivity.this,"Nessun evento trovato!","",SweetAlertDialog.WARNING_TYPE);

        }else {
            if(dialogo!=null)   cancelDialogo(dialogo);

        }






    }


    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if(!message.equalsIgnoreCase("caricamento")){

            if(title.equalsIgnoreCase("Nessun evento trovato!")){
                pDialog.setTitleText(title);

                new CountDownTimer(2000, 2000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        if(dialogo!=null)   cancelDialogo(dialogo);

                    }
                }.start();
            }
            else {
                pDialog.setTitleText(title);
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

}
