package com.simonepirozzi.techevent.ui.favourite;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.simonepirozzi.techevent.ui.favourite.adapter.CustomAdapterFavourite;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FavouriteFragment extends Fragment implements FavouriteContract.View {
    private User user;
    SweetAlertDialog dialogo;
    ListView listView;
    CustomAdapterFavourite customAdapter;
    ArrayList<Event> lista;
    List<Event> listaprova;
    List<Event> listaFinale;
    FavouritePresenter favouritePresenter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        lista = new ArrayList<>();
        listaprova = new ArrayList<>();
        listaFinale = new ArrayList<>();
        favouritePresenter = new FavouritePresenter(getActivity(), this);

        customAdapter = new CustomAdapterFavourite(view.getContext(), R.layout.list_element_pref, new ArrayList<Event>());

        listView = view.findViewById(R.id.listViewPreferiti);
        listView.setAdapter(customAdapter);
        if (dialogo != null) cancelDialogo(dialogo);
        dialogo = startDialogo(view.getContext(), "", "caricamento", SweetAlertDialog.PROGRESS_TYPE);
        eventi(view);
        return view;
    }


    public void eventi(View view) {
        listaprova.clear();
        lista.clear();
        listaFinale.clear();


        //prendo la lista preferiti associata all utente
        //verifico che gli eventi preferiti siano ancora disponibili e pubblicati altrimenti li rimuovo

        favouritePresenter.getFavEvents();
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo) {


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        if (!message.equalsIgnoreCase("caricamento")) {

            if (title.equalsIgnoreCase("Non hai aggiunto ancora nessun evento ai preferiti")) {
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
    public void setLayout(List<Event> events, User user1) {
        this.user = user1;
        List<Event> evv = events;
        for (int i = 0; i < evv.size(); i++) {

            for (int j = 0; j < user.getPreferences().size(); j++) {
                if (evv.get(i).getId().equalsIgnoreCase(user.getPreferences().get(j).getId())) {
                    if (evv.get(i).getState().equalsIgnoreCase("pubblicato")) {
                        lista.add(evv.get(i));

                    }
                }
            }

        }
        user.setPreferences(lista);
        favouritePresenter.setUserDocument(user);
        //nuovo
        listaFinale.addAll(lista);

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
                        } else return 1;


                    } else return 0;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        for (int k = 0; k < listaFinale.size(); k++) {

            customAdapter.add(listaFinale.get(k));
        }

        if (listaFinale == null || listaFinale.size() == 0) {
            if (dialogo != null) cancelDialogo(dialogo);
            dialogo = startDialogo(getView().getContext(), "Non hai aggiunto ancora nessun evento ai preferiti", "", SweetAlertDialog.WARNING_TYPE);
        } else {
            if (dialogo != null) cancelDialogo(dialogo);

        }


    }

}
