package com.simonepirozzi.techevent;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Fragment;

import androidx.appcompat.widget.AppCompatImageView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Utente utente;
    SweetAlertDialog dialogo;
    ListView listView;
    CustomAdapter customAdapter;
    List<Evento> lista;
    List<Evento> listaprova;
    List<Evento> listaFinale;

    TextView titTEXT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_home, container, false);
        db = FirebaseFirestore.getInstance();

        lista=new ArrayList<>();
        listaprova=new ArrayList<>();
        listaFinale=new ArrayList<>();

        customAdapter=new CustomAdapter(view.getContext(),R.layout.list_element,new ArrayList<Evento>());
        listView=view.findViewById(R.id.listView);
        titTEXT=view.findViewById(R.id.eventiinteressanti);
        listView.setAdapter(customAdapter);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(view.getContext(),"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        eventi(view);

        return view;
    }

    public void eventi(final View view){
        if(currentUser!=null){

            DocumentReference docRef = db.collection("/utenti").document(currentUser.getEmail());

            if(isNetwork(view.getContext())){
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        utente=documentSnapshot.toObject(Utente.class);
                        titTEXT.setText("Eventi a "+utente.getCittà()+" e provincia");
                        db.collection("/eventi").whereEqualTo("provincia",utente.getProvincia()).whereEqualTo("stato","pubblicato").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    listaprova.clear();
                                    lista.clear();
                                    listaprova=task.getResult().toObjects(Evento.class);
                                    //  Log.d("cazz",listaprova.toString());
                                    if(listaprova!=null && listaprova.size()>0){
                                        for(int i=0;i<listaprova.size();i++){
                                            lista.add(listaprova.get(i));

                                        }

                                        listaFinale.clear();
                                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
                                        String adesso= simpleDateFormat.format(new Date());
                                        try {
                                            Date d= simpleDateFormat.parse(adesso);
                                            for(int k=0;k<lista.size();k++){
                                                Date d1=simpleDateFormat.parse(lista.get(k).getData());
                                                if(!d.after(d1)){
                                                    listaFinale.add(lista.get(k));

                                                }else{
                                                    db.collection("/eventi").document(lista.get(k).getDataPubb()).delete();

                                                }

                                            }

                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }


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

                                        if(listaFinale.size()==0){
                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                            dialogo=startDialogo(getView().getContext(),"Nessun evento trovato!","",SweetAlertDialog.WARNING_TYPE);
                                        }else{
                                            if(dialogo!=null)   cancelDialogo(dialogo);

                                        }



                                    }else{
                                        if(dialogo!=null)   cancelDialogo(dialogo);

                                    }

                                }else{
                                    if(dialogo!=null)   cancelDialogo(dialogo);

                                }
                            }
                        });


                    }
                });
            }else{
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(getView().getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


            }



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

    public boolean isNetwork(Context context){
        ConnectivityManager manager =(ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork!=null && activeNetwork.getState()== NetworkInfo.State.CONNECTED)   return true;
        else    return false;
    }


}
