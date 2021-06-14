package com.simonepirozzi.techevent;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import android.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PrivacyFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User user;
    EditText nome,cognome,mail;
    AutoCompleteTextView citta;
    LinearLayout profilo,categorie;
    Button salva;
    SweetAlertDialog dialogo;
    List<String> numberList=new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view=inflater.inflate(R.layout.fragment_privacy, container, false);
        db = FirebaseFirestore.getInstance();

        WebView webView=view.findViewById(R.id.webView);
        webView.loadUrl("https://tech-event-campani.flycricket.io/privacy.html");
        return view;
    }



}
