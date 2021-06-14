package com.simonepirozzi.techevent;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PreferitiFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    SweetAlertDialog dialogo;
    ListView listView;
    CustomAdapterPreferiti customAdapter;
    ArrayList<Event> lista;
    List<Event> listaprova;
    List<Event> listaFinale;

    TextView titTEXT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_preferiti, container, false);
        db = FirebaseFirestore.getInstance();

        lista=new ArrayList<>();
        listaprova=new ArrayList<>();
        listaFinale=new ArrayList<>();

        customAdapter=new CustomAdapterPreferiti(view.getContext(),R.layout.list_element_pref,new ArrayList<Event>());

        listView=view.findViewById(R.id.listViewPreferiti);
        listView.setAdapter(customAdapter);
        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(view.getContext(),"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
        eventi(view);



        return view;
    }



    public void eventi(View view) {
        listaprova.clear();
        lista.clear();
        listaFinale.clear();


        //prendo la lista preferiti associata all utente
        //verifico che gli eventi preferiti siano ancora disponibili e pubblicati altrimenti li rimuovo

        db.collection("/utenti").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    user =task.getResult().toObject(User.class);
                    db.collection("/eventi").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<Event> evv=task.getResult().toObjects(Event.class);



                         /*
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd:MM:yyyy");
                            String adesso= simpleDateFormat.format(new Date());
                            Date d= null;
                            try {
                                d = simpleDateFormat.parse(adesso);
                                for(int c=0;c<utente.getPreferiti().size();c++){
                                    Date d1=simpleDateFormat.parse(utente.getPreferiti().get(c).getData());
                                    if(d.after(d1)){
                                        utente.getPreferiti().remove(c);
                                    }



                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

*/




                            for(int i=0;i<evv.size();i++){

                                for(int j = 0; j< user.getPreferences().size(); j++){
                                    if(evv.get(i).getId().equalsIgnoreCase(user.getPreferences().get(j).getId())){
                                        if(evv.get(i).getState().equalsIgnoreCase("pubblicato")){
                                            lista.add(evv.get(i));

                                        }
                                    }
                                }

                            }
                            user.setPreferences(lista);
                            db.collection("/utenti").document(currentUser.getEmail()).set(user,SetOptions.merge());
                            //nuovo
                            listaFinale.addAll(lista);

                            Collections.sort(listaFinale, new Comparator<Event>() {
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


                            Collections.sort(listaFinale, new Comparator<Event>() {
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

                            Collections.sort(listaFinale, new Comparator<Event>() {
                                @Override
                                public int compare(Event o1, Event o2) {
                                    try {

                                        if(o1.getDate().equalsIgnoreCase(o2.getDate())
                                                && o1.getPriority()==o2.getPriority()){
                                            if(o1.getCity().equalsIgnoreCase(user.getCity())
                                                    && o2.getCity().equalsIgnoreCase(user.getCity())){
                                                return 0;
                                            }else if(!o1.getCity().equalsIgnoreCase(user.getCity())
                                                    && !o2.getCity().equalsIgnoreCase(user.getCity())){
                                                return 0;
                                            }else if(o1.getCity().equalsIgnoreCase(user.getCity())
                                                    && !o2.getCity().equalsIgnoreCase(user.getCity())){
                                                return -1;
                                            }else return 1;


                                        }else   return 0;



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return 0;
                                }
                            });


                            for(int k=0;k<listaFinale.size();k++){

                                customAdapter.add(listaFinale.get(k));
                            }

                            if(listaFinale==null || listaFinale.size()==0){
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                dialogo=startDialogo(getView().getContext(),"Non hai aggiunto ancora nessun evento ai preferiti","",SweetAlertDialog.WARNING_TYPE);
                            }else{
                                if(dialogo!=null)   cancelDialogo(dialogo);

                            }


                        }
                    });
/*
                    db.collection("/utenti").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Utente u=task.getResult().toObject(Utente.class);
                            listaFinale.addAll(u.getPreferiti());

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
                                            }else return 1;


                                        }else   return 0;



                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return 0;
                                }
                            });


                            for(int k=0;k<listaFinale.size();k++){

                                customAdapter.add(listaFinale.get(k));
                            }

                            if(listaFinale==null || listaFinale.size()==0){
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                dialogo=startDialogo(getView().getContext(),"Non hai aggiunto ancora nessun evento ai preferiti","",SweetAlertDialog.WARNING_TYPE);
                            }else{
                                if(dialogo!=null)   cancelDialogo(dialogo);

                            }

                        }
                    });*/

                }
            }
        });




    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if(!message.equalsIgnoreCase("caricamento")){

            if(title.equalsIgnoreCase("Non hai aggiunto ancora nessun evento ai preferiti")){
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
