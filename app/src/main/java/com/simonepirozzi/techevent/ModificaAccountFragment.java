package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ModificaAccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Utente utente;
    EditText nome,cognome,mail;
    AutoCompleteTextView citta;
    LinearLayout profilo,categorie;
    Button salva,elimina;
    SweetAlertDialog dialogo;
    List<String> numberList=new ArrayList<>();
    TinyDB tinyDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_mod_profilo, container, false);
        db = FirebaseFirestore.getInstance();
        nome=view.findViewById(R.id.Mod_Name);
        cognome=view.findViewById(R.id.Mod_Sur);
        salva=view.findViewById(R.id.salva_button);
        elimina=view.findViewById(R.id.elimina_button);
        utente=new Utente();
        tinyDB=new TinyDB(view.getContext());


        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(view.getContext(),"","caricamento",SweetAlertDialog.PROGRESS_TYPE);

        if(currentUser!=null){
            DocumentReference docRef = db.collection("/utenti").document(currentUser.getEmail());

            if(isNetwork(view.getContext())){
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        utente=documentSnapshot.toObject(Utente.class);
                        nome.setText(utente.getNome());
                        cognome.setText(utente.getCognome());
                        if(dialogo!=null)   cancelDialogo(dialogo);


                    }
                });
            }else{
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);

            }



        }


        salva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(view.getContext())){
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Caricamento","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                    if(nome.getText().toString().length()!=0 && cognome.getText().toString().length()!=0){
                        try{
                            db.collection("/utenti").document(currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Utente utente1= task.getResult().toObject(Utente.class);
                                        utente1.setNome(nome.getText().toString());
                                        utente1.setCognome(cognome.getText().toString());
                                        if(isNetwork(view.getContext())){
                                            db.collection("/utenti").document(currentUser.getEmail()).set(utente1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        if(dialogo!=null)   cancelDialogo(dialogo);
                                                        // progressBar.setVisibility(View.GONE);
                                                        startDialogo(view.getContext(),"Modifiche salvate","Le modifiche sono state salvate con successo",SweetAlertDialog.SUCCESS_TYPE);
                                                    }else{
                                                        startDialogo(view.getContext(),"Attenzione","Le modifiche non sono state salvate! Riprova.",SweetAlertDialog.ERROR_TYPE);
                                                    }
                                                }
                                            });
                                        }else{
                                            if(dialogo!=null)   cancelDialogo(dialogo);
                                            //progressBar.setVisibility(View.GONE);
                                            startDialogo(view.getContext(),"Attenzione","Le modifiche non sono state salvate a causa di problemi di connessione! Riprova.",SweetAlertDialog.ERROR_TYPE);
                                        }
                                    }
                                }
                            });
                        }catch (Exception e){
                            if(dialogo!=null)   cancelDialogo(dialogo);

                            //   progressBar.setVisibility(View.GONE);
                            startDialogo(view.getContext(),"Attenzione","Le modifiche non sono state salvate a causa di problemi di connessione! Riprova.",SweetAlertDialog.ERROR_TYPE);
                        }


                    }else{
                        if(dialogo!=null)   cancelDialogo(dialogo);
                        dialogo=startDialogo(view.getContext(),"Attenzione","Tutti i campi devono essere riempiti",SweetAlertDialog.WARNING_TYPE);
                    }
                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                }
            }
        });


        elimina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(view.getContext(),"Attenzione","Stai per eliminare il tuo account. Sei sicuro?",SweetAlertDialog.WARNING_TYPE);
            }
        });



        return view;


    }





    public SweetAlertDialog startDialogo(final Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(!message.equalsIgnoreCase("caricamento")){

            if(message.equalsIgnoreCase("Stai per eliminare il tuo account. Sei sicuro?")){
                pDialog.setContentText(message);
                pDialog.setCancelButton("Annulla", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if(dialogo!=null)   cancelDialogo(dialogo);
                    }
                });
                pDialog.setConfirmButton("Si", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Per confermare inserisci la password");
                        final EditText input = new EditText(context);
                        input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        builder.setView(input);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.cancel();
                                if(dialogo!=null)   cancelDialogo(dialogo);
                                if(input.getText().toString().length()>0){
                                    String password=input.getText().toString();
                                    AuthCredential credential = EmailAuthProvider
                                            .getCredential(currentUser.getEmail(), password);
                                    currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                db.collection("/eventi").whereEqualTo("email",currentUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            List<Evento> eliminare=task.getResult().toObjects(Evento.class);

                                                            for(int i=0;i<eliminare.size();i++){
                                                                db.collection("/eventi").document(eliminare.get(i).getDataPubb()).delete();
                                                            }

                                                            db.collection("/utenti").document(currentUser.getEmail()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Intent intent=new Intent(context,Login.class);
                                                                            startActivity(intent);
                                                                            getActivity().finish();
                                                                        }
                                                                    });
                                                                }
                                                            });


                                                        }
                                                    }
                                                });





                                            }else{
                                                dialog.cancel();
                                                if(dialogo!=null)   cancelDialogo(dialogo);
                                                Toast.makeText(context,"Password errata!",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else{
                                    dialog.cancel();
                                    if(dialogo!=null)   cancelDialogo(dialogo);
                                    Toast.makeText(context,"Campo della password vuoto! Riprova",Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if(dialogo!=null)   cancelDialogo(dialogo);
                            }
                        });

                        builder.show();

                    }
                });
            }else{
                if(!message.equalsIgnoreCase("caricamento")){
                    pDialog.setConfirmText("Ok");
                    pDialog.setContentText(message);
                }

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
