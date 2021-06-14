package com.simonepirozzi.techevent;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.simonepirozzi.techevent.data.db.TinyDB;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventiFragment extends Fragment {
    List<String> cittaList=new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    SweetAlertDialog dialogo;
    TextView nuovo,pubblicati;
    TinyDB tinyDB;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_eventi, container, false);
        db = FirebaseFirestore.getInstance();
        tinyDB=new TinyDB(view.getContext());
        nuovo=view.findViewById(R.id.linkNuovoEvento);
        pubblicati=view.findViewById(R.id.linkEventiPubblicati);
        if(tinyDB.getString("mainTogestione")!=null && tinyDB.getString("mainTogestione").length()>0){
            if(tinyDB.getString("mainTogestione").equalsIgnoreCase("si")){

                tinyDB.remove("mainTogestione");
                if(dialogo!=null)   cancelDialogo(dialogo);
                Intent intent=new Intent(view.getContext(),EventiPubblicatiActivity.class);
                startActivity(intent);
            }
        }

        pubblicati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(view.getContext(),EventiPubblicatiActivity.class);
                startActivity(intent);
            }
        });

        nuovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame_container,new AggiungiEventoFragment(),"aggiungiEvento").addToBackStack("addEvent").commit();

            }
        });



        return view;
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);

        if(!message.equalsIgnoreCase("caricamento")){
            if(message.equalsIgnoreCase("L'evento è stato aggiunto con successo")){
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container,new EventiFragment(),"aggiungiEvento").addToBackStack("addEvento").commit();

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

    public void getJson(View view){
        String json;
        try{
            InputStream is=view.getContext().getAssets().open("com.json");
            int size=is.available();
            byte[] buffer=new byte[size];

            is.read(buffer);
            is.close();
            json=new String(buffer,"UTF-8");

            JSONArray jsonArray=new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                cittaList.add(jsonObject.getString("comune")+","+jsonObject.getString("provincia"));
            }

        }catch (Exception e){
            Log.d("cazz",e.getMessage());
        }
    }


}
