package com.simonepirozzi.techevent.ui.login.signin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseUser;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.ui.login.LoginActivity;
import com.simonepirozzi.techevent.utils.Constants;
import com.simonepirozzi.techevent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SignInCityActivity extends Activity implements SignInContract.View {
    List<String> numberList = new ArrayList<>();
    TextView selectedCity;
    String name, surname, mail, password;
    EditText city;
    Button signin;
    ListView cityList;
    ArrayAdapter<String> adapter;
    SweetAlertDialog dialog;
    SignInPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_city);
        name = getIntent().getStringExtra(Constants.NAME_SIGN_IN);
        surname = getIntent().getStringExtra(Constants.SURNAME_SIGN_IN);
        mail = getIntent().getStringExtra(Constants.EMAIL_SIGN_IN);
        password = getIntent().getStringExtra(Constants.PASSWORD_SIGN_IN);
        city = findViewById(R.id.Reg_City);
        selectedCity = findViewById(R.id.selectedCity);
        cityList = findViewById(R.id.listViewCity);
        signin = findViewById(R.id.signinButtonCity);

        mPresenter = new SignInPresenter(this, this);

        numberList = Utility.getJson(this);
        adapter = new ArrayAdapter<>(this, R.layout.custom_list_item_view, R.id.text_view_list_item, numberList);
        cityList.setAdapter(adapter);

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity.setText(cityList.getItemAtPosition(position).toString());
            }
        });

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.signInWithCity(selectedCity.getText().toString(), mail, password, name, surname);
            }
        });


    }

    @Override
    public void updateUI(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            cancelDialog();
            Intent intent = new Intent(SignInCityActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase(getString(R.string.dialog_loading))) {
            if (message.equalsIgnoreCase(getString(R.string.verify_account))) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton(getString(R.string.dialog_ok), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        updateUI(mPresenter.firebaseUser);
                    }
                });
            } else {
                pDialog.setContentText(message);
                pDialog.setConfirmText(getString(R.string.dialog_ok));
            }
        }
        pDialog.setCancelable(false);
        pDialog.show();
        return pDialog;
    }

    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }


}
