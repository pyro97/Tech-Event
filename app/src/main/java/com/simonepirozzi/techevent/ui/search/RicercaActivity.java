package com.simonepirozzi.techevent.ui.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ListView;

import com.simonepirozzi.techevent.ui.search.adapter.CustomAdapterSearch;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class RicercaActivity extends Activity implements SearchContract.View {
    SweetAlertDialog dialogo;
    ListView listView;
    List<Event> listaFinale;
    User user;
    CustomAdapterSearch customAdapter;
    SearchPresenter searchPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (dialogo != null) cancelDialogo(dialogo);
        dialogo = startDialogo(RicercaActivity.this, "", "caricamento", SweetAlertDialog.PROGRESS_TYPE);
        customAdapter = new CustomAdapterSearch(RicercaActivity.this, R.layout.list_element_ricerca, new ArrayList<Event>());
        listView = findViewById(R.id.listViewRicerca);
        searchPresenter = new SearchPresenter(this, this);
        listView.setAdapter(customAdapter);
        listaFinale = new ArrayList<>();

        searchPresenter.getListEvents();
        user = searchPresenter.getUser();


        Collections.sort(listaFinale, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy");
                    Date data1 = simpleDateFormat.parse(o1.getDate());
                    Date data2 = simpleDateFormat.parse(o2.getDate());
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

                    if (o1.getDate().equalsIgnoreCase(o2.getDate())) {
                        if (o1.getPriority() > o2.getPriority()) {
                            return -1;
                        } else if (o1.getPriority() < o2.getPriority()) {
                            return 1;
                        } else return 0;
                    } else return 0;


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

                    if (o1.getDate().equalsIgnoreCase(o2.getDate())
                            && o1.getPriority() == o2.getPriority()) {
                        if (o1.getCity().equalsIgnoreCase(user.getCity())
                                && o2.getCity().equalsIgnoreCase(user.getCity())) {
                            return 0;
                        } else if (!o1.getCity().equalsIgnoreCase(user.getCity())
                                && !o2.getCity().equalsIgnoreCase(user.getCity())) {
                            return 0;
                        } else if (o1.getCity().equalsIgnoreCase(user.getCity())
                                && !o2.getCity().equalsIgnoreCase(user.getCity())) {
                            return -1;
                        } else if (!o1.getCity().equalsIgnoreCase(user.getCity())
                                && o2.getCity().equalsIgnoreCase(user.getCity())) {
                            return -1;
                        } else return 1;


                    } else return 0;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        for (int j = 0; j < listaFinale.size(); j++) {
            customAdapter.add(listaFinale.get(j));
        }

        searchPresenter.getListEventsSize();

    }

    @Override
    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo) {


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if (!message.equalsIgnoreCase("caricamento")) {

            if (title.equalsIgnoreCase("Nessun evento trovato!")) {
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

    @Override
    public void setLayoutEvents(ArrayList<Object> lista) {
        for (int i = 0; i < lista.size(); i++) {
            Event e = (Event) lista.get(i);
            listaFinale.add(e);
        }
    }

    @Override
    public void cancelDialogo(SweetAlertDialog s) {
        if (dialogo != null) {
            dialogo.cancel();
        }
    }

}
