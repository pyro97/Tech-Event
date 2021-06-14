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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventiPubblicatiFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    SweetAlertDialog dialogo;
    ListView listView;
    CustomAdapterPubblicati customAdapter;
    List<Event> lista;
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

        final View view=inflater.inflate(R.layout.fragment_pubblicati, container, false);
        db = FirebaseFirestore.getInstance();

        lista=new ArrayList<>();
        listaprova=new ArrayList<>();
        listaFinale=new ArrayList<>();

        customAdapter=new CustomAdapterPubblicati(view.getContext(),R.layout.list_element_pubblicati,new ArrayList<Event>());

        listView=view.findViewById(R.id.listViewPubblicati);
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
        db.collection("/eventi").whereEqualTo("email",currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<Event> eventArrayList =task.getResult().toObjects(Event.class);



                    for(int i = 0; i< eventArrayList.size(); i++){
                        customAdapter.add(eventArrayList.get(i));
                    }

                    if(eventArrayList.size()==0){
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(getView().getContext(),"Non hai pubblicato ancora nessun evento!","",SweetAlertDialog.WARNING_TYPE);
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);

                    }
                }
            }
        });
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if(!message.equalsIgnoreCase("caricamento")){

            if(title.equalsIgnoreCase("Non hai pubblicato ancora nessun evento!")){
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
