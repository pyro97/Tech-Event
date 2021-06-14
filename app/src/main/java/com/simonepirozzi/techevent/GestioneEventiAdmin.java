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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class GestioneEventiAdmin extends Activity {
    private FirebaseAuth mAuth;
   private FirebaseUser user;
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
    Button mod,agg;
    User user;
    LinearLayout gestione,banna,aggiungi;
    ListView listView;
    CustomAdapterEventiAdmin customAdapterEventiAdmin;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestione_eventi_admin);
        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
        agg=findViewById(R.id.aggiorna_attesa);
        listView=findViewById(R.id.listViewAdminEventi);
        customAdapterEventiAdmin=new CustomAdapterEventiAdmin(GestioneEventiAdmin.this,R.layout.list_admin,new ArrayList<Event>());
        listView.setAdapter(customAdapterEventiAdmin);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(GestioneEventiAdmin.this,"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        db.collection("/eventi").whereEqualTo("stato","attesa").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Event> eventi=queryDocumentSnapshots.toObjects(Event.class);
                Collections.sort(eventi, new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        try {
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
                            Date data1= simpleDateFormat.parse(o1.getDate());
                            Date data2= simpleDateFormat.parse(o2.getDate());
                            return data1.compareTo(data2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });


                Collections.sort(eventi, new Comparator<Event>() {
                    @Override
                    public int compare(Event o1, Event o2) {
                        try {

                            if(o1.getDate().equalsIgnoreCase(o2.getDate())){
                                if(o1.getPriority()>o2.getPriority()){
                                    return -1;
                                }else if(o1.getPriority()<o2.getPriority()){
                                    return 1;
                                }else return 0;
                            }else   return 0;



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });


                for(Event u:eventi){
                   customAdapterEventiAdmin.add(u);
                }
                if(dialogo!=null)   cancelDialogo(dialogo);

            }
        });

        agg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
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
