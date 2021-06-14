package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;
import com.simonepirozzi.techevent.ui.account.admin.adapter.CustomAdapterListaAdmin;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AdminManagementActivity extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    EditText email;
    Spinner spinner;
    Button mod;
    ListView listView;
    CustomAdapterListaAdmin customAdapterListaAdmin;
    AccountPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_managment);
        email = findViewById(R.id.mail_gest_admin);
        spinner = findViewById(R.id.spinner_gest_admin);
        mod = findViewById(R.id.modifica_ruolo);
        listView = findViewById(R.id.listViewAdmin);
        mPresenter = new AccountPresenter(this, this);
        customAdapterListaAdmin = new CustomAdapterListaAdmin(AdminManagementActivity.this, R.layout.list_element, new ArrayList<User>());
        listView.setAdapter(customAdapterListaAdmin);

        mPresenter.getAccount();

        ArrayList<String> categories = createCategories();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminManagementActivity.this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinner.setAdapter(arrayAdapter);

        mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setRole(email.getText().toString(), spinner.getSelectedItem().toString());
            }
        });


    }

    private ArrayList<String> createCategories() {
        ArrayList<String> categorie = new ArrayList<>();
        categorie.add("admin");
        categorie.add("moderatore");
        categorie.add("utente");
        return categorie;
    }


    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase("caricamento")) {
            if (message.equalsIgnoreCase("Modifiche effettuate")) {
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
