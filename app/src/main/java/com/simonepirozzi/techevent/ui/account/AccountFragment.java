package com.simonepirozzi.techevent.ui.account;

import android.app.Fragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.simonepirozzi.techevent.AdminActivity;
import com.simonepirozzi.techevent.EventiPubblicatiFragment;
import com.simonepirozzi.techevent.ModificaAccountFragment;
import com.simonepirozzi.techevent.PrivacyFragment;
import com.simonepirozzi.techevent.ProfiloCittaPreferitaActivity;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.login.LoginActivity;

import androidx.annotation.Nullable;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    TextView nome,mailAccount;
    LinearLayout profilo,priv,cittPref,adminLayout,padre;
    Button logout;
    SweetAlertDialog dialogo;
    TinyDB tinyDB;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_account, container, false);
        db = FirebaseFirestore.getInstance();
        logout=view.findViewById(R.id.logout);
        nome=view.findViewById(R.id.nomi);
        mailAccount=view.findViewById(R.id.mailAccount);
        profilo=view.findViewById(R.id.setProfilo);
        priv=view.findViewById(R.id.privacy);
        cittPref=view.findViewById(R.id.setCittaPreferita);
        adminLayout=view.findViewById(R.id.adminLayout);
        padre=view.findViewById(R.id.padrelinear);
        tinyDB=new TinyDB(view.getContext());
        user =new User();

        if(dialogo!=null)   cancelDialogo(dialogo);
        dialogo=startDialogo(view.getContext(),"caricamento","",SweetAlertDialog.PROGRESS_TYPE);


        if(currentUser!=null){
            DocumentReference docRef = db.collection("/utenti").document(currentUser.getEmail());

            if(isNetwork(view.getContext())){
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user =documentSnapshot.toObject(User.class);
                        nome.setText(user.getName()+" "+ user.getSurname());
                        mailAccount.setText(user.getMail());

                        //creo menu admin
                        if(user.getRole().equalsIgnoreCase("admin")
                                || user.getRole().equalsIgnoreCase("moderatore")){

                            TextView textView=new TextView(view.getContext());
                            textView.setText("Admin");
                            textView.setGravity(Gravity.LEFT);
                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            );
                            textView.setLayoutParams(param);
                            textView.setTextColor(Color.BLACK);
                            textView.setTextSize(14);

                            TextView textView1=new TextView(view.getContext());
                            textView1.setGravity(Gravity.RIGHT);
                            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    1.0f
                            );
                            textView1.setLayoutParams(param1);
                            textView1.setTextColor(Color.GRAY);
                            textView1.setTextSize(18);
                            textView1.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_action_arrow, 0);

                            View viewTW=new View(view.getContext());
                            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    1
                            );
                            viewTW.setLayoutParams(param2);
                            viewTW.setBackground(getResources().getDrawable(R.color.gray_btn_bg_pressed_color));

                            adminLayout.addView(textView);
                            adminLayout.addView(textView1);

                            adminLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(view.getContext(), AdminActivity.class);
                                    startActivity(intent);

                                }
                            });
                        }




                        if(dialogo!=null)   cancelDialogo(dialogo);

                    }
                });
            }else{
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);


            }



        }

        if(tinyDB.getString("mainTogestione")!=null && tinyDB.getString("mainTogestione").length()>0){
            if(tinyDB.getString("mainTogestione").equalsIgnoreCase("si")){
                tinyDB.remove("mainTogestione");
                getFragmentManager().beginTransaction().replace(R.id.frame_container,new EventiPubblicatiFragment()).commit();
            }
        }



            profilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(view.getContext())){
                    getFragmentManager().beginTransaction().replace(R.id.frame_container,new ModificaAccountFragment(),"modificaProfilo").addToBackStack("account").commit();

                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                }
            }
        });





        priv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(view.getContext())){
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"","caricamento",SweetAlertDialog.PROGRESS_TYPE);
                    getFragmentManager().beginTransaction().replace(R.id.frame_container,new PrivacyFragment(),"privacy").addToBackStack("account").commit();
                    if(dialogo!=null)   cancelDialogo(dialogo);

                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                }
            }
        });

        cittPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetwork(view.getContext())){
                    Intent  intent=new Intent(view.getContext(), ProfiloCittaPreferitaActivity.class);
                    startActivity(intent);

                }else{
                    if(dialogo!=null)   cancelDialogo(dialogo);
                    dialogo=startDialogo(view.getContext(),"Attenzione","Problemi di connessione",SweetAlertDialog.ERROR_TYPE);
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialogo!=null)   cancelDialogo(dialogo);
                dialogo=startDialogo(view.getContext(),"Attenzione","Stai per essere disconnesso. Sei sicuro/a ?",SweetAlertDialog.WARNING_TYPE);
            }
        });



        return view;
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo){


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if(message.equalsIgnoreCase("Stai per essere disconnesso. Sei sicuro/a ?")){
            pDialog.setContentText(message);
            pDialog.setConfirmButton("Si", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mAuth.signOut();
                    Intent intent =new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    if(dialogo!=null)   cancelDialogo(dialogo);
                }
            });
            pDialog.setCancelButton("Annulla", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    if(dialogo!=null)   cancelDialogo(dialogo);
                }
            });
        }else{

            if(!message.equalsIgnoreCase("caricamento")){
                pDialog.setConfirmText("Ok");
                pDialog.setContentText(message);
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
