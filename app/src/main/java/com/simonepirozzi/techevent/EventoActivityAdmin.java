package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class EventoActivityAdmin extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser user;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;
    AppCompatImageView img,back;
    TextView data,organizz,orario,costo,contatto;
    EditText titolo,luogo,descrizione,cittaET;
    String id,chiamante;
    Evento e;
    TinyDB tinyDB;
    Spinner prio;
    Button accetta,rifiuta;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento_info_admin);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        data=findViewById(R.id.dataEventoA);
        titolo=findViewById(R.id.titoloEventoA);
        luogo=findViewById(R.id.luogoEventoA);
        img=findViewById(R.id.fotoEventoA);
        descrizione=findViewById(R.id.infoEventoA);
        organizz=findViewById(R.id.organizzatoreEventoA);
        orario=findViewById(R.id.oraEventoA);
        costo=findViewById(R.id.costoEventoA);
        contatto=findViewById(R.id.contattoEventoA);
        cittaET=findViewById(R.id.cittaEventoA);
        id=getIntent().getStringExtra("id");
        chiamante=getIntent().getStringExtra("chiamante");

//        tit.setText(getIntent().getStringExtra("titoloExtra"));

        cittaET.setText(getIntent().getStringExtra("cittaExtra"));
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
        tinyDB=new TinyDB(EventoActivityAdmin.this);
        prio=findViewById(R.id.prioEv);
        rifiuta=findViewById(R.id.rifiuta);
        accetta=findViewById(R.id.pubblica);

        ArrayList<String> priorità = new ArrayList<>();
        priorità.add("Evento normale");
        priorità.add("Evento importante");
       // priorità.add("Evento sponsorizzato");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(EventoActivityAdmin.this, android.R.layout.simple_spinner_dropdown_item, priorità);
        prio.setAdapter(arrayAdapter);


        db.collection("/eventi").whereEqualTo("id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Evento> eventi=new ArrayList<>();
                    eventi=task.getResult().toObjects(Evento.class);
                    if(eventi.size()==1){
                        for(int i=0;i<eventi.size();i++){
                            e=eventi.get(i);
                        }
                    }

                    titolo.setText(e.getTitolo());
                    if(e.getFoto().length()>0){
                        try {
                            byte [] encodeByte= Base64.decode(e.getFoto(),Base64.DEFAULT);
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


        accetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descrizione.getText().toString().length()>0 && titolo.getText().toString().length()>0
                    && luogo.getText().toString().length()>0 && cittaET.getText().toString().length()>0){

                    e.setStato("pubblicato");
                    e.setDescrizione(descrizione.getText().toString());
                    e.setTitolo(titolo.getText().toString());
                    e.setCitta(cittaET.getText().toString().substring(0,cittaET.getText().toString().indexOf(",")));
                    e.setPosizione(luogo.getText().toString());
                    e.setProvincia(cittaET.getText().toString().substring(cittaET.getText().toString().indexOf(",")+1));

                    if(prio.getSelectedItem().toString().equalsIgnoreCase("Evento normale")){
                        e.setPriorità(1);
                    }else if(prio.getSelectedItem().toString().equalsIgnoreCase("Evento importante")){
                        e.setPriorità(2);

                    }
              /*  else if(prio.getSelectedItem().toString().equalsIgnoreCase("Evento sponsorizzato")){
                    e.setPriorità(3);

                }*/
                    else{
                        e.setPriorità(0);
                    }

                    db.collection("/eventi").document(e.getDataPubb()).set(e, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(dialogo!=null)   cancelDialogo(dialogo);
                            dialogo=startDialogo(EventoActivityAdmin.this,"","Evento pubblicato!",SweetAlertDialog.SUCCESS_TYPE);


                        }
                    });



                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(EventoActivityAdmin.this,"","Alcuni campi sono vuoti",SweetAlertDialog.WARNING_TYPE);
                }

            }
        });

        rifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setStato("rifiutato");
                e.setPriorità(0);

                db.collection("/eventi").document(e.getDataPubb()).set(e, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(EventoActivityAdmin.this,"","Evento rifiutato!",SweetAlertDialog.SUCCESS_TYPE);



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

            if(message.equalsIgnoreCase("Evento pubblicato!")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String mailto = "mailto:"+e.getEmail() +
                                "?cc=" + e.getEmail() +
                                "&subject=" + "Esito dell'evento inserito" +
                                "&body=" + "L'evento inserito è stato pubblicato con successo";

                        finish();
                        Intent intent=new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(mailto));
                        startActivity(intent);
                    }
                });

            }else if(message.equalsIgnoreCase("Evento rifiutato!")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String mailto = "mailto:"+e.getEmail() +
                                "?cc=" + e.getEmail() +
                                "&subject=" + "Esito dell'evento inserito" +
                                "&body=" + "L'evento inserito è stato rifiutato.";
                        finish();
                        Intent intent=new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(mailto));
                        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(chiamante.equalsIgnoreCase("preferiti")){
            tinyDB.putString("evento","preferiti");
            Intent intent=new Intent(EventoActivityAdmin.this,MainActivity.class);
            startActivity(intent);
        }

    }
}
