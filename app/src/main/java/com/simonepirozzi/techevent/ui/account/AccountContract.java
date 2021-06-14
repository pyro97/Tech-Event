package com.simonepirozzi.techevent.ui.account;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountContract {

    interface Presenter{

        Task<QuerySnapshot> getEventById(String id);

        Task<DocumentSnapshot> getUserDocument();

        Task<DocumentSnapshot> getUserDocumentCustom(String mail);

        Task<QuerySnapshot> getUserCollection();

        Task<Void> reauthenticate(AuthCredential credential);

        Task<Void> setUserDocument(User user);

        Task<Void> setEventDocument(String date, Event event);

        void getAccount();

        void banAccount(String mail, boolean isBan);

        void checkPublishEventFlow();

        void signOut();

        Task<QuerySnapshot> getEventByUser();

        Task<QuerySnapshot> getEventByUserCustom(String mail);

        Task<QuerySnapshot> getEventByState(String state);

        Task<Void> deleteEvents(String document);

        Task<Void> deleteUser();

        void saveEditCity(User user);

        void saveEditAccount(String name, String surname);

        void reauthenticate(String password);

        void getListAccount();

        void getListAccountAdmin();

        void setRole(String mail, String role);

        void eventsById(String id);

        void confirmEvent(Event e, boolean isConfirm);

        void setFavouriteEvents();
    }

    public interface View{
        SweetAlertDialog startDialog(String title, String message, int type);
        void cancelDialog();

        void setAccountLayout(User user);

        void setEventLayout(List<Event> events);
    }
}
