package com.simonepirozzi.techevent.ui.login.fogotten;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.repository.FirestoreManager;
import com.simonepirozzi.techevent.data.repository.MainRepository;
import com.simonepirozzi.techevent.utils.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecoveryPasswordPresenter implements RecoveryPasswordContract.Presenter {

    private RecoveryPasswordContract.View view;
    private Activity activity;
    private MainRepository repository;


    public RecoveryPasswordPresenter(Activity activity, RecoveryPasswordContract.View view) {
        FirebaseApp.initializeApp(activity.getApplicationContext());
        this.repository = new MainRepository();
        this.activity = activity;
        this.view = view;
    }

    @Override
    public Task<DocumentSnapshot> getUserDocument(String mail) {
        return repository.getDocument(FirestoreManager.USER_COLLECTION, mail);
    }

    @Override
    public Task<Void> sendPasswordResetEmail(String mail) {
        return repository.sendPasswordResetEmail(mail);
    }

    @Override
    public void sendMail(final String mail) {

        if (Utility.isNetwork(activity)) {
            if (mail.length() != 0) {
                view.startDialog(activity.getString(R.string.loading_title), activity.getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                getUserDocument(mail).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        view.startDialog(activity.getString(R.string.success_send_mail), activity.getString(R.string.success_send_mail_desc), SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        view.startDialog(activity.getString(R.string.send_mail_error), activity.getString(R.string.send_mail_error_desc), SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                });
                            } else {
                                view.startDialog(activity.getString(R.string.warning_title), activity.getString(R.string.no_exist_mail), SweetAlertDialog.ERROR_TYPE);
                            }
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
}
