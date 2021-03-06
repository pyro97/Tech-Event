package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.ui.account.admin.adapter.CustomAdapterListaAdmin;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class UserBanActivity extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    EditText email;
    Button mod, rei;
    CustomAdapterListaAdmin customAdapterListaAdmin;
    ListView bannedList;
    AccountPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banna_activity);
        email = findViewById(R.id.mail_banna);
        mod = findViewById(R.id.banna);
        rei = findViewById(R.id.rein);
        mPresenter = new AccountPresenter(this, this);
        bannedList = findViewById(R.id.listViewBannati);
        customAdapterListaAdmin = new CustomAdapterListaAdmin(UserBanActivity.this, R.layout.list_element, new ArrayList<User>());
        bannedList.setAdapter(customAdapterListaAdmin);

        mPresenter.getListAccount();

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.banAccount(email.getText().toString(), true);
            }
        });

        rei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.banAccount(email.getText().toString(),false);
            }
        });


    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase("caricamento")) {
            if (message.equalsIgnoreCase("Utente bannato")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            } else if (message.equalsIgnoreCase("Utente reintegrato")) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
                    }
                });

            } else {
                pDialog.setContentText(message);
                pDialog.setConfirmText("Ok");
            }


        }

        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;

    }

    @Override
    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void setAccountLayout(User user) {
        customAdapterListaAdmin.add(user);
    }

    @Override
    public void setEventLayout(List<Event> events) {

    }

}
