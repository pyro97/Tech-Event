package com.simonepirozzi.techevent.ui.login;

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

import com.google.firebase.auth.FirebaseUser;
import com.simonepirozzi.techevent.ui.main.MainActivity;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.ui.login.fogotten.PasswordRecoveryActivity;
import com.simonepirozzi.techevent.ui.login.signin.SignInActivity;

import androidx.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends Activity implements LoginContract.View {
    TextView signIn, forgotten;
    Button login;
    EditText username, pass;
    SweetAlertDialog dialog;
    LoginPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signIn = findViewById(R.id.link_rec_login);
        forgotten = findViewById(R.id.recover_link);
        login = findViewById(R.id.login);
        username = findViewById(R.id.Login_Username);
        pass = findViewById(R.id.Login_Password);
        mPresenter = new LoginPresenter(this,this);
        SpannableString content = new SpannableString(forgotten.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        forgotten.setText(content);

        mPresenter.checkVerifiedEmail();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = startDialog(getString(R.string.loading_title), getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                Intent intent = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
                cancelDialog();
            }
        });

        forgotten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) cancelDialog();
                dialog = startDialog(getString(R.string.loading_title), getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
                startActivity(intent);
                cancelDialog();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login(username.getText().toString(),pass.getText().toString());
            }
        });
    }

    @Override
    public void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            cancelDialog();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
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

