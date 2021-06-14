package com.simonepirozzi.techevent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.simonepirozzi.techevent.data.db.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfiloCittaPreferitaActivity extends Activity {
    List<String> numberList=new ArrayList<>();
    List<String> newlist=new ArrayList<>();

    TextView cittaScelta;
    String provincia;
    String nome,cognome,mail,password,city;
    EditText citta;
    String result;
    Button registrati;
    ListView listaCitta;
    ArrayAdapter<String> adapter;
    String cittaNew;
LinearLayout lin;
    private FirebaseAuth mAuth;
   private FirebaseUser firebaseUser;
    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    SweetAlertDialog dialogo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_citta);

        mAuth=FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();


        citta=findViewById(R.id.Prof_City);
        cittaScelta=findViewById(R.id.citySceltaProf);
        listaCitta=findViewById(R.id.listViewCittaProf);
        registrati=findViewById(R.id.prof_button_città);

        db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String cittaEsistente=task.getResult().toObject(User.class).getCity();
                    cittaScelta.setText(cittaEsistente);
                }
            }
        });
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
                        if(cittaScelta.getText().toString().contains(",")){
                            cittaNew=cittaScelta.getText().toString().substring(0,cittaScelta.getText().toString().indexOf(","));
                            provincia=cittaScelta.getText().toString().substring(cittaScelta.getText().toString().indexOf(",")+1);

                        }


                        db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    User user =task.getResult().toObject(User.class);
                                    user.setCity(cittaNew);
                                    user.setProvince(provincia);
                                    db.collection("/utenti").document(mAuth.getCurrentUser().getEmail()).set(user, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(dialogo!=null)   cancelDialogo(dialogo);

                                            dialogo=startDialogo(ProfiloCittaPreferitaActivity.this,"Modifiche salvate","Le modifiche sono state salvate con successo",SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    });
                                }
                            }
                        });
                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(ProfiloCittaPreferitaActivity.this,"Attenzione","Devi selezionare una città per poter procedere",SweetAlertDialog.WARNING_TYPE);
                    }


                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(ProfiloCittaPreferitaActivity.this,"Attenzione","Devi selezionare una città per poter procedere",SweetAlertDialog.WARNING_TYPE);
                }
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
                numberList.add(jsonObject.getString("comune")+","+jsonObject.getString("provincia"));
            }

        }catch (Exception e){
            Log.d("cazz",e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
