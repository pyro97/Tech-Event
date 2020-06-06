package com.simonepirozzi.techevent;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;

public class MainActivity extends Activity {
    private FirebaseAuth mAuth;
    TinyDB tinyDB;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment=null;
            String tag="no";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment=new HomeFragment();
                    break;
                case R.id.navigation_search:
                    fragment=new RicercaFragment();
                    break;
                case R.id.navigation_preferiti:
                    fragment=new PreferitiFragment();
                    break;
                case R.id.nuovo_evento:
                    fragment=new EventiFragment();
                    break;
                case R.id.navigation_account:
                    fragment=new AccountFragment();
                    break;
                }
                getFragmentManager().beginTransaction().replace(R.id.contenitore,fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user= mAuth.getCurrentUser();
        tinyDB=new TinyDB(MainActivity.this);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(tinyDB.getString("evento")!=null && tinyDB.getString("evento").length()>0){
            if(tinyDB.getString("evento").equalsIgnoreCase("preferiti")){
                tinyDB.remove("evento");
                navigation.setSelectedItemId(R.id.navigation_preferiti);
                getFragmentManager().beginTransaction().replace(R.id.contenitore,new PreferitiFragment()).commit();
            }else if(tinyDB.getString("evento").equalsIgnoreCase("gestioneEvento")){
                tinyDB.remove("evento");
                navigation.setSelectedItemId(R.id.nuovo_evento);
                getFragmentManager().beginTransaction().replace(R.id.contenitore,new EventiFragment()).commit();
                Intent intent=new Intent(MainActivity.this,EventiPubblicatiActivity.class);
                startActivity(intent);

            }

            else{
                getFragmentManager().beginTransaction().replace(R.id.contenitore,new HomeFragment()).commit();

            }

        }else if(tinyDB.getString("modProfilo")!=null && tinyDB.getString("modProfilo").length()>0){
            if(tinyDB.getString("modProfilo").equalsIgnoreCase("si")){
                tinyDB.remove("modProfilo");
                navigation.setSelectedItemId(R.id.navigation_account);
                getFragmentManager().beginTransaction().replace(R.id.contenitore,new AccountFragment(),"account").commit();
            }else{
                getFragmentManager().beginTransaction().replace(R.id.contenitore,new HomeFragment()).commit();

            }
        }
        else{
            getFragmentManager().beginTransaction().replace(R.id.contenitore,new HomeFragment()).commit();

        }



    }

}
