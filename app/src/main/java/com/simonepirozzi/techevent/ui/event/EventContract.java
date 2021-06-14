package com.simonepirozzi.techevent.ui.event;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EventContract {

    interface Presenter{
        Task<Void> setEventDocument(String date, Event event);

        Task<QuerySnapshot> getAllEvents();

        Task<Void> setUserDocument(User user);

        Task<DocumentSnapshot> getUserDocument();

        Task<QuerySnapshot> getEventDocumentByEmail();

        void checkPublishEvent();

        void publishEvent();

        void publishEventStep2(String date, Event e);

        void checkManageEvent();
    }

   public interface View {
        void cancelDialogo(SweetAlertDialog s);

        SweetAlertDialog startDialogo(Context context, String title, String message, int tipo);

        void setLayoutUser(User user1);
       void setLayoutEvents(List<Event> events);

   }
}
