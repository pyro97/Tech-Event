package com.simonepirozzi.techevent.ui.home;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeContract {

    interface Presenter{
        Task<DocumentSnapshot> getUserDocument();

        Task<QuerySnapshot> getEventsByProvince(String collection, String province);

        Task<Void> deleteEvents(String document);

        void getEvents();
    }

    interface View {
        void cancelDialogo(SweetAlertDialog s);

        SweetAlertDialog startDialogo(Context context, String title, String message, int tipo);

        void setLayoutListEvent(List<Event> events);

        void setLayoutUser(User user);

    }
}
