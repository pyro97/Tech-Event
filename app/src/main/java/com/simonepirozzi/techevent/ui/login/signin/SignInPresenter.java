package com.simonepirozzi.techevent.ui.login.signin;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.utils.Constants;
import com.simonepirozzi.techevent.utils.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View view;
    private Activity activity;
    private MainRepository repository;
    FirebaseUser firebaseUser;


    public SignInPresenter(Activity activity, SignInContract.View view) {
        this.repository = new MainRepository();
        this.activity = activity;
        this.view = view;
        this.firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument(String mail) {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, mail);
    }

    @Override
    public Task<Void> setUserDocument(String mail, User user) {
        return repository.setDocument(FirestoreManager.USER_COLLECTION, mail, user);
    }

    @Override
    public Task<AuthResult> createUser(String mail, String password) {
        return repository.createUser(mail, password);
    }

    @Override
    public void signInWithRegistry(String name, String surname, String mail, String password) {
        if (name.length() != 0 && surname.length() != 0 && mail.length() != 0 && password.length() != 0) {
            if (password.length() > 5) {
                Intent intent = new Intent(activity, SignInCityActivity.class);
                intent.putExtra(Constants.NAME_SIGN_IN, name);
                intent.putExtra(Constants.SURNAME_SIGN_IN, surname);
                intent.putExtra(Constants.EMAIL_SIGN_IN, mail);
                intent.putExtra(Constants.PASSWORD_SIGN_IN, password);
                activity.startActivity(intent);
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.warning_desc_signin), SweetAlertDialog.WARNING_TYPE);
            }
        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.empty_field), SweetAlertDialog.WARNING_TYPE);
        }
    }

    @Override
    public void signInWithCity(final String city, final String mail, String password, final String name, final String surname) {
        if (city.length() != 0) {
            if (!city.equalsIgnoreCase(Constants.NO_CITY)) {
                view.startDialog(activity.getString(R.string.loading_title), activity.getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                if (Utility.isNetwork(activity)) {
                    final String selectedProv = city.substring(city.indexOf(",") + 1);
                    final String selectedCity = city.substring(0, city.indexOf(","));
                    createUser(mail, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseUser = FirestoreManager.getAuthInstance().getCurrentUser();
                                firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            User user = new User(name, surname, selectedCity, mail, Constants.USER_ROLE, selectedProv);
                                            setUserDocument(mail, user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    view.startDialog(activity.getString(R.string.signin_success), activity.getString(R.string.verify_account), SweetAlertDialog.SUCCESS_TYPE);
                                                }
                                            });
                                        }
                                    }
                                });

                            } else {
                                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.exist_mail), SweetAlertDialog.WARNING_TYPE);
                                view.updateUI(null);
                            }
                        }
                    });
                } else {
                    view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
                }
            } else {
                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.select_city_error), SweetAlertDialog.WARNING_TYPE);
            }


        } else {
            view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.select_city_error), SweetAlertDialog.WARNING_TYPE);
        }
    }
}
