package com.simonepirozzi.techevent.ui.event;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simonepirozzi.techevent.ui.event.add.AddEventoFragment;
import com.simonepirozzi.techevent.ui.event.publish.PublishEventActivity;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventsFragment extends Fragment implements EventContract.View {
    List<String> cittaList = new ArrayList<>();
    SweetAlertDialog dialogo;
    TextView nuovo, pubblicati;
    EventPresenter eventPresenter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_eventi, container, false);
        nuovo = view.findViewById(R.id.linkNuovoEvento);
        pubblicati = view.findViewById(R.id.linkEventiPubblicati);
        eventPresenter = new EventPresenter(getActivity(), this);
        eventPresenter.checkPublishEvent();

        pubblicati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), PublishEventActivity.class);
                startActivity(intent);
            }
        });

        nuovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new AddEventoFragment(), "aggiungiEvento").addToBackStack("addEvent").commit();

            }
        });


        return view;
    }

    public SweetAlertDialog startDialogo(Context context, String title, String message, int tipo) {


        SweetAlertDialog pDialog = new SweetAlertDialog(context, tipo);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);

        if (!message.equalsIgnoreCase("caricamento")) {
            if (message.equalsIgnoreCase("L'evento è stato aggiunto con successo")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (dialogo != null) cancelDialogo(dialogo);
                        getFragmentManager().beginTransaction().replace(R.id.frame_container, new EventsFragment(), "aggiungiEvento").addToBackStack("addEvento").commit();

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
    public void setLayoutUser(User user1) {

    }

    public void cancelDialogo(SweetAlertDialog s) {
        if (dialogo != null) {
            dialogo.cancel();
        }
    }

}
