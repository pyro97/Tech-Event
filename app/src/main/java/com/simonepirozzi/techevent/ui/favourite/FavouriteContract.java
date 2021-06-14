package com.simonepirozzi.techevent.ui.favourite;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FavouriteContract {

    interface Presenter{
        Task<QuerySnapshot> getAllEvents();

        Task<DocumentSnapshot> getUserDocument();

        Task<Void> setUserDocument(User user);
    }

    interface View {
        void cancelDialogo(SweetAlertDialog s);

        SweetAlertDialog startDialogo(Context context, String title, String message, int tipo);

        void setLayout(List<Event> events, User user1);
    }
}
