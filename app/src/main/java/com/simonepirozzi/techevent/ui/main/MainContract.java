package com.simonepirozzi.techevent.ui.main;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainContract {

    interface Presenter{
        void checkSelectedNav();
    }

    interface View{
        void setSelectedNavigation(int id);
    }
}
