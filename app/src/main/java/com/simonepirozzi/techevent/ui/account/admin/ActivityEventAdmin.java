package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;
import com.simonepirozzi.techevent.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ActivityEventAdmin extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    AppCompatImageView img;
    TextView data, organizz, orario, costo, contatto;
    EditText titolo, luogo, descrizione, cittaET;
    String id, chiamante;
    Event e;
    Spinner prio;
    Button accetta, rifiuta;
    AccountPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info_admin);
        data = findViewById(R.id.dataEventoA);
        titolo = findViewById(R.id.titoloEventoA);
        luogo = findViewById(R.id.luogoEventoA);
        img = findViewById(R.id.fotoEventoA);
        descrizione = findViewById(R.id.infoEventoA);
        organizz = findViewById(R.id.organizzatoreEventoA);
        orario = findViewById(R.id.oraEventoA);
        costo = findViewById(R.id.costoEventoA);
        contatto = findViewById(R.id.contattoEventoA);
        cittaET = findViewById(R.id.cittaEventoA);
        id = getIntent().getStringExtra("id");
        chiamante = getIntent().getStringExtra("chiamante");
        cittaET.setText(getIntent().getStringExtra("cittaExtra"));
        descrizione.setText(getIntent().getStringExtra("descrizioneExtra"));
        titolo.setText(getIntent().getStringExtra("titoloExtra"));
        organizz.setText(getIntent().getStringExtra("organizzExtra"));
        data.setText(getIntent().getStringExtra("dataExtra"));
        orario.setText(getIntent().getStringExtra("orarioExtra"));
        luogo.setText(getIntent().getStringExtra("luogoExtra"));
        if (getIntent().getStringExtra("costoExtra").equalsIgnoreCase("0")) {
            costo.setText("Gratuito");
        } else {
            costo.setText(getIntent().getStringExtra("costoExtra"));
        }
        descrizione.setText(getIntent().getStringExtra("descrizioneExtra"));
        contatto.setText(getIntent().getStringExtra("contattoExtra"));
        prio = findViewById(R.id.prioEv);
        rifiuta = findViewById(R.id.rifiuta);
        accetta = findViewById(R.id.pubblica);
        mPresenter = new AccountPresenter(this, this);
        ArrayList<String> priorità = new ArrayList<>();
        priorità.add("Evento normale");
        priorità.add("Evento importante");
        // priorità.add("Evento sponsorizzato");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ActivityEventAdmin.this, android.R.layout.simple_spinner_dropdown_item, priorità);
        prio.setAdapter(arrayAdapter);

        mPresenter.getEventById(id);


        accetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (descrizione.getText().toString().length() > 0 && titolo.getText().toString().length() > 0
                        && luogo.getText().toString().length() > 0 && cittaET.getText().toString().length() > 0) {

                    e.setState("pubblicato");
                    e.setDescription(descrizione.getText().toString());
                    e.setTitle(titolo.getText().toString());
                    e.setCity(cittaET.getText().toString().substring(0, cittaET.getText().toString().indexOf(",")));
                    e.setPosition(luogo.getText().toString());
                    e.setProvince(cittaET.getText().toString().substring(cittaET.getText().toString().indexOf(",") + 1));

                    if (prio.getSelectedItem().toString().equalsIgnoreCase("Evento normale")) {
                        e.setPriority(1);
                    } else if (prio.getSelectedItem().toString().equalsIgnoreCase("Evento importante")) {
                        e.setPriority(2);

                    } else {
                        e.setPriority(0);
                    }
                    mPresenter.confirmEvent(e, true);

                } else {
                    startDialog("", "Alcuni campi sono vuoti", SweetAlertDialog.WARNING_TYPE);
                }

            }
        });

        rifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                e.setState("rifiutato");
                e.setPriority(0);
                mPresenter.confirmEvent(e, false);
            }
        });


    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase("caricamento")) {

            if (message.equalsIgnoreCase("Evento pubblicato!")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String mailto = "mailto:" + e.getEmail() +
                                "?cc=" + e.getEmail() +
                                "&subject=" + "Esito dell'evento inserito" +
                                "&body=" + "L'evento inserito è stato pubblicato con successo";

                        finish();
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(mailto));
                        startActivity(intent);
                    }
                });

            } else if (message.equalsIgnoreCase("Evento rifiutato!")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        String mailto = "mailto:" + e.getEmail() +
                                "?cc=" + e.getEmail() +
                                "&subject=" + "Esito dell'evento inserito" +
                                "&body=" + "L'evento inserito è stato rifiutato.";
                        finish();
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse(mailto));
                        startActivity(intent);
                    }
                });

            } else {
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }


        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    @Override
    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void setAccountLayout(User user) {

    }

    @Override
    public void setEventLayout(List<Event> events) {
        if (events.size() == 1) {
            for (int i = 0; i < events.size(); i++) {
                e = events.get(i);
            }
        }

        titolo.setText(e.getTitle());
        if (e.getPhoto().length() > 0) {
            try {
                byte[] encodeByte = Base64.decode(e.getPhoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                img.setImageBitmap(bitmap);

            } catch (Exception e) {
                img.setImageDrawable(getResources().getDrawable(R.drawable.ic_ph));

                e.getMessage();
            }
        } else {
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_ph));

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chiamante.equalsIgnoreCase("preferiti")) {
            mPresenter.setFavouriteEvents();
            Intent intent = new Intent(ActivityEventAdmin.this, MainActivity.class);
            startActivity(intent);
        }

    }
}
