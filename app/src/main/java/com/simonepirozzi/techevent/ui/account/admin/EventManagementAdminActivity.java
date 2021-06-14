package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.ui.account.admin.adapter.CustomAdapterEventiAdmin;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class EventManagementAdminActivity extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    Button agg;
    ListView listView;
    CustomAdapterEventiAdmin customAdapterEventAdmin;
    AccountPresenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_admin_events);
        agg = findViewById(R.id.progressUpdate);
        listView = findViewById(R.id.listViewAdminEvent);
        mPresenter = new AccountPresenter(this, this);
        customAdapterEventAdmin = new CustomAdapterEventiAdmin(EventManagementAdminActivity.this, R.layout.list_admin, new ArrayList<Event>());
        listView.setAdapter(customAdapterEventAdmin);
        startDialog("", getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
        mPresenter.getProgressEvents();

        agg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });
    }

    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        SweetAlertDialog pDialog = new SweetAlertDialog(this, type);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        if (!message.equalsIgnoreCase(getString(R.string.loading_message))) {
            if (message.equalsIgnoreCase(getString(R.string.edit_saved_dialog))) {
                pDialog.setContentText(message);
                pDialog.setConfirmButton(getString(R.string.dialog_ok), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        startActivity(getIntent());
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

    @Override
    public void cancelDialog() {
        if (dialog != null) {
            dialog.cancel();
        }
    }

    @Override
    public void setAccountLayout(User user) {
    }

    @Override
    public void setEventLayout(List<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MM:yyyy");
                    Date data1 = simpleDateFormat.parse(o1.getDate());
                    Date data2 = simpleDateFormat.parse(o2.getDate());
                    return data1.compareTo(data2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                try {

                    if (o1.getDate().equalsIgnoreCase(o2.getDate())) {
                        if (o1.getPriority() > o2.getPriority()) {
                            return -1;
                        } else if (o1.getPriority() < o2.getPriority()) {
                            return 1;
                        } else return 0;
                    } else return 0;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        for (Event u : events) {
            customAdapterEventAdmin.add(u);
        }
        cancelDialog();
    }

}
