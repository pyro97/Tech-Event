package com.simonepirozzi.techevent.ui.login.fogotten;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecoveryPasswordContract {

    interface Presenter{

        Task<DocumentSnapshot> getUserDocument(String mail);

        Task<Void> sendPasswordResetEmail(String mail);

        void sendMail(String mail);
    }

    interface View{
        SweetAlertDialog startDialog(String title, String message, int type);
        void cancelDialog();
    }
}
