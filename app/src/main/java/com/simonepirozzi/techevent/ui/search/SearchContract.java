package com.simonepirozzi.techevent.ui.search;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchContract {

    interface Presenter{
        Task<QuerySnapshot> getAllEvents();

        Task<QuerySnapshot> getEventsByParam(String key, String param);

        Task<QuerySnapshot> getEventsByMultipleParam(String key, String param, String key1, String param1);

        void setList(ArrayList<Object> lista);

        void getListEvents();

        void getListEventsSize();

        Task<DocumentSnapshot> getUserDocument();

        User getUser();
    }

    interface View {
        void cancelDialogo(SweetAlertDialog s);

        SweetAlertDialog startDialogo(Context context, String title, String message, int tipo);

        void setLayoutEvents(ArrayList<Object> lista);

    }
}
