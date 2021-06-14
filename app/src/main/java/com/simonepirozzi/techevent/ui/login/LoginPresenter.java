package com.simonepirozzi.techevent.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.utils.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginPresenter implements LoginContract.Presenter {

    private LoginContract.View view;
    private Activity activity;
    private FirebaseUser firebaseUser;
    private MainRepository repository;


    public LoginPresenter(Activity activity, LoginContract.View view) {
        FirebaseApp.initializeApp(activity.getApplicationContext());
        this.repository = new MainRepository();
        this.activity = activity;
        this.view = view;
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
    }

    @Override
    public void checkVerifiedEmail() {
        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
            if (Utility.isNetwork(activity)) {
                view.startDialog(activity.getString(R.string.loading_profile), activity.getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                checkBanUser();
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
            }
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.verify_account), SweetAlertDialog.WARNING_TYPE);
        }
    }

    @Override
    public void checkBanUser() {
        getUserDocument().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (!user.getRole().equalsIgnoreCase(FirestoreManager.BANNED_USER)) {
                    view.updateUI(firebaseUser);
                } else {
                    view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.ban_warning), SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }

    @Override
    public void login(String username, String password) {
        if (Utility.isNetwork(activity)) {
            if (username.length() != 0 && password.length() != 0) {
                view.startDialog(activity.getString(R.string.loading_access), activity.getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                signIn(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
                            if (firebaseUser.isEmailVerified()) {
                                getUserDocument().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        if (!user.getRole().equalsIgnoreCase(FirestoreManager.BANNED_USER)) {
                                            view.updateUI(firebaseUser);

                                        } else {
                                            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.ban_warning), SweetAlertDialog.ERROR_TYPE);
                                        }
                                    }
                                });
                            } else {
                                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.verify_account), SweetAlertDialog.WARNING_TYPE);
                            }
                        } else {
                            view.startDialog(activity.getString(R.string.no_correct_login_title), activity.getString(R.string.no_correct_login_message), SweetAlertDialog.ERROR_TYPE);
                            view.updateUI(null);
                        }
                    }
                });
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.empty_field), SweetAlertDialog.WARNING_TYPE);
            }
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);

        }

    }

    @Override
    public Task<DocumentSnapshot> getUserDocument() {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, firebaseUser.getEmail());
    }

    @Override
    public Task<AuthResult> signIn(String username, String password) {
        return repository.signIn(username, password);
    }
}
