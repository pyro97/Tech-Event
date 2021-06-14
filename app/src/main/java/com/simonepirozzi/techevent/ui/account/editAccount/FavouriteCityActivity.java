package com.simonepirozzi.techevent.ui.account.editAccount;

import android.app.Activity;
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

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;
import com.simonepirozzi.techevent.utils.Constants;
import com.simonepirozzi.techevent.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class FavouriteCityActivity extends Activity implements AccountContract.View {
    List<String> numberList = new ArrayList<>();
    TextView selectedCity;
    String province;
    EditText city;
    Button signin;
    ListView cityList;
    ArrayAdapter<String> adapter;
    String newCity;
    SweetAlertDialog dialog;
    AccountPresenter mPresenter;
    boolean isEdit = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_city);
        city = findViewById(R.id.Prof_City);
        selectedCity = findViewById(R.id.selectedCityProf);
        cityList = findViewById(R.id.listViewCittaProf);
        signin = findViewById(R.id.prof_button_city);
        mPresenter = new AccountPresenter(this, this);
        isEdit = false;
        mPresenter.getAccount();

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
                if (selectedCity.getText().toString().length() != 0) {

                    if (!selectedCity.getText().toString().equalsIgnoreCase(Constants.NO_CITY)) {
                        if (selectedCity.getText().toString().contains(",")) {
                            newCity = selectedCity.getText().toString().substring(0, selectedCity.getText().toString().indexOf(","));
                            province = selectedCity.getText().toString().substring(selectedCity.getText().toString().indexOf(",") + 1);
                        }
                        isEdit = true;
                        mPresenter.getAccount();
                    } else {
                        startDialog(getString(R.string.warning_title), getString(R.string.select_city_error), SweetAlertDialog.WARNING_TYPE);
                    }
                } else {
                    startDialog(getString(R.string.warning_title), getString(R.string.select_city_error), SweetAlertDialog.WARNING_TYPE);
                }
            }
        });
    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        dialog = new SweetAlertDialog(this, type);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText(title);
        if (!message.equalsIgnoreCase(getString(R.string.loading_message))) {
            dialog.setContentText(message);
            dialog.setConfirmText(getString(R.string.dialog_ok));
        }
        dialog.setCancelable(false);
        dialog.show();
        return dialog;

    }

    @Override
    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void setAccountLayout(User user) {
        if (isEdit) {
            isEdit = false;
            User userEdit = user;
            userEdit.setCity(newCity);
            userEdit.setProvince(province);
            mPresenter.saveEditCity(userEdit);
        } else {
            selectedCity.setText(user.getCity());
        }
    }

    @Override
    public void setEventLayout(List<Event> events) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
