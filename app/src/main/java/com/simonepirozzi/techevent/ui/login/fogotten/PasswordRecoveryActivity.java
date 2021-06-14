package com.simonepirozzi.techevent.ui.login.fogotten;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.ui.login.LoginActivity;
import com.simonepirozzi.techevent.ui.main.MainActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PasswordRecoveryActivity extends Activity implements RecoveryPasswordContract.View {
    TextView login;
    Button sendButton;
    EditText mail;
    SweetAlertDialog dialog;
    RecoveryPasswordPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);
        login = findViewById(R.id.link_rec_login);
        sendButton = findViewById(R.id.send_rec);
        mail = findViewById(R.id.mail_rec);
        mPresenter = new RecoveryPasswordPresenter(this, this);

        SpannableString content = new SpannableString(login.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        login.setText(content);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.sendMail(mail.getText().toString());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        dialog = new SweetAlertDialog(this, type);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText(title);
        if (!message.equalsIgnoreCase(getString(R.string.dialog_loading))) {
            dialog.setContentText(message);
            dialog.setConfirmText(getString(R.string.dialog_ok));
        }
        dialog.setCancelable(false);
        dialog.show();
        return dialog;

    }

    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }
}

