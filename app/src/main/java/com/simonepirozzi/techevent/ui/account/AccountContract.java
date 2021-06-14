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

        Task<DocumentSnapshot> getUserDocument();

        Task<QuerySnapshot> getUserCollection();

        Task<Void> reauthenticate(AuthCredential credential);

        Task<Void> setUserDocument(User user);

        void getAccount();

        void checkPublishEventFlow();

        void signOut();

        Task<QuerySnapshot> getEventByUser();

        Task<QuerySnapshot> getEventByState(String state);

        Task<Void> deleteEvents(String document);

        Task<Void> deleteUser();

        void saveEditCity(User user);

        void saveEditAccount(String name, String surname);

        void reauthenticate(String password);
    }

    public interface View{
        SweetAlertDialog startDialog(String title, String message, int type);
        void cancelDialog();

        void setAccountLayout(User user);

        void setEventLayout(List<Event> events);
    }
}
