package com.simonepirozzi.techevent.ui.login.signin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.ui.login.LoginActivity;

import androidx.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SignInActivity extends Activity implements SignInContract.View {
    TextView login;
    EditText name, surname, mail, password;
    Button signIn;
    LinearLayout lin;
    SweetAlertDialog dialog;
    SignInPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login = findViewById(R.id.link_login);
        signIn = findViewById(R.id.signinButton);
        name = findViewById(R.id.Reg_Name);
        surname = findViewById(R.id.Reg_Sur);
        mail = findViewById(R.id.Reg_Mail);
        password = findViewById(R.id.Reg_Pass);
        lin = findViewById(R.id.lreg);
        mPresenter = new SignInPresenter(this, this);
        SpannableString content = new SpannableString(login.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        login.setText(content);


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.signInWithRegistry(name.getText().toString(), surname.getText().toString(), mail.getText().toString(), password.getText().toString());
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
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

    @Override
    public void updateUI(FirebaseUser firebaseUser) { }


}
