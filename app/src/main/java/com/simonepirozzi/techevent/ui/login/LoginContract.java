package com.simonepirozzi.techevent.ui.login;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginContract {

    interface Presenter{

        void checkVerifiedEmail();

        void checkBanUser();

        void login(String username, String password);

        Task<DocumentSnapshot> getUserDocument();

        Task<AuthResult> signIn(String username, String password);
    }

    interface View{
        void updateUI(FirebaseUser firebaseUser);

        SweetAlertDialog startDialog(String title, String message, int type);
        void cancelDialog();
    }
}
