package com.simonepirozzi.techevent.ui.account.admin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.GestioneAdmin;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;
import com.simonepirozzi.techevent.utils.Constants;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AdminActivity extends Activity implements AccountContract.View {
    SweetAlertDialog dialog;
    LinearLayout manage, ban, add;
    AccountPresenter mPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        manage = findViewById(R.id.linkAdminEvents);
        ban = findViewById(R.id.linkAdminBan);
        add = findViewById(R.id.linkAdminMod);
        mPresenter = new AccountPresenter(this, this);
        mPresenter.getAccount();

        manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, EventManagementAdminActivity.class);
                startActivity(intent);
            }
        });


        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, UserBanActivity.class);
                startActivity(intent);
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
        if (user.getRole().equalsIgnoreCase(Constants.ADMIN_ROLE)) {
            TextView textView = new TextView(AdminActivity.this);
            textView.setText(R.string.admin_roles);
            textView.setGravity(Gravity.LEFT);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            textView.setLayoutParams(param);
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setTextSize(18);
            textView.setTypeface(null, Typeface.BOLD);

            TextView textView1 = new TextView(AdminActivity.this);
            textView1.setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            textView1.setLayoutParams(param1);
            textView1.setTextColor(Color.GRAY);
            textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_arrow, 0);

            add.addView(textView);
            add.addView(textView1);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminActivity.this, GestioneAdmin.class);
                    startActivity(intent);
                }
            });
        }
        cancelDialog();
    }

    @Override
    public void setEventLayout(List<Event> events) {

    }
}
