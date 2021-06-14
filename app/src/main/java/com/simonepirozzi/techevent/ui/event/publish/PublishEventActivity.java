package com.simonepirozzi.techevent.ui.event.publish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ListView;
import android.widget.TextView;

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.event.EventContract;
import com.simonepirozzi.techevent.ui.event.EventPresenter;
import com.simonepirozzi.techevent.ui.event.adapter.CustomAdapterPublish;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PublishEventActivity extends Activity implements EventContract.View {
    private User user;
    SweetAlertDialog dialogo;
    ListView listView;
    CustomAdapterPublish customAdapter;
    List<Event> lista;
    List<Event> listaprova;
    List<Event> listaFinale;
    EventPresenter eventPresenter;

    TextView titTEXT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pubblish);
        eventPresenter = new EventPresenter(this, this);
        eventPresenter.checkManageEvent();
        lista = new ArrayList<>();
        listaprova = new ArrayList<>();
        listaFinale = new ArrayList<>();

        customAdapter = new CustomAdapterPublish(PublishEventActivity.this, R.layout.list_element_pubblicati, new ArrayList<Event>());
        listView = findViewById(R.id.listViewPubblicati);
        listView.setAdapter(customAdapter);
        if (dialogo != null) cancelDialogo(dialogo);
        dialogo = startDialogo("", "caricamento", SweetAlertDialog.PROGRESS_TYPE);
        eventi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventPresenter.checkManageEvent();
    }

    public void eventi() {
        listaprova.clear();
        lista.clear();
        listaFinale.clear();
        eventPresenter.getEventsByMail();
    }

    public SweetAlertDialog startDialogo(String title, String message, int tipo) {


        SweetAlertDialog pDialog = new SweetAlertDialog(PublishEventActivity.this, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if (!message.equalsIgnoreCase("caricamento")) {

            if (title.equalsIgnoreCase("Non hai pubblicato ancora nessun evento!")) {
                pDialog.setTitleText(title);

                new CountDownTimer(2000, 2000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        if (dialogo != null) cancelDialogo(dialogo);

                    }
                }.start();
            } else {
                pDialog.setTitleText(title);
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }

        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    public void cancelDialogo(SweetAlertDialog s) {

        s.cancel();
    }

    @Override
    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo) {
        return null;
    }

    @Override
    public void setLayoutUser(User user1) {

    }

    @Override
    public void setLayoutEvents(List<Event> events) {
        List<Event> eventArrayList = events;
        for (int i = 0; i < eventArrayList.size(); i++) {
            customAdapter.add(eventArrayList.get(i));
        }

        if (eventArrayList.size() == 0) {
            if (dialogo != null) cancelDialogo(dialogo);
            dialogo = startDialogo("Non hai pubblicato ancora nessun evento!", "", SweetAlertDialog.WARNING_TYPE);
        } else {
            if (dialogo != null) cancelDialogo(dialogo);

        }
    }

}
