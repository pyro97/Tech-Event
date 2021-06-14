package com.simonepirozzi.techevent.ui.login.signin;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.simonepirozzi.techevent.data.db.model.User;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInContract {

    interface Presenter{

        Task<DocumentSnapshot> getUserDocument(String mail);

        Task<Void> setUserDocument(String mail, User user);

        Task<AuthResult> createUser(String mail, String password);

        void signInWithRegistry(String name, String surname, String mail, String password);

        void signInWithCity(String city, String mail, String password, String name,String surname);
    }

    interface View{
        SweetAlertDialog startDialog(String title, String message, int type);
        void cancelDialog();

        void updateUI(FirebaseUser firebaseUser);
    }
}
