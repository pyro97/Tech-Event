package com.simonepirozzi.techevent.ui.account;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.ui.account.admin.AdminActivity;
import com.simonepirozzi.techevent.ui.account.editAccount.EditAccountFragment;
import com.simonepirozzi.techevent.ui.account.privacy.PrivacyFragment;
import com.simonepirozzi.techevent.ui.account.editAccount.FavouriteCityActivity;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.login.LoginActivity;
import com.simonepirozzi.techevent.utils.Constants;
import com.simonepirozzi.techevent.utils.Utility;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccountFragment extends Fragment implements AccountContract.View {
    TextView name, mailAccount;
    LinearLayout profile, privacy, favCity, adminLayout, linearExtend;
    Button logout;
    SweetAlertDialog dialog;
    AccountPresenter mPresenter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        logout = view.findViewById(R.id.logout);
        name = view.findViewById(R.id.names);
        mailAccount = view.findViewById(R.id.mailAccount);
        profile = view.findViewById(R.id.setProfilo);
        privacy = view.findViewById(R.id.privacy);
        favCity = view.findViewById(R.id.setFavCity);
        adminLayout = view.findViewById(R.id.adminLayout);
        linearExtend = view.findViewById(R.id.linearExtend);
        mPresenter = new AccountPresenter(getActivity(), this);

        mPresenter.getAccount();
        mPresenter.checkPublishEventFlow();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetwork(getActivity())) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_container, new EditAccountFragment(), Constants.TAG_EDIT_PROFILE).addToBackStack(Constants.TAG_ACCOUNT).commit();
                } else {
                    startDialog(getString(R.string.warning_title), getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
                }
            }
        });


        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetwork(getActivity())) {
                    startDialog("", getString(R.string.loading_message), SweetAlertDialog.PROGRESS_TYPE);
                    getFragmentManager().beginTransaction().replace(R.id.frame_container, new PrivacyFragment(), Constants.TAG_PRIVACY).addToBackStack(Constants.TAG_ACCOUNT).commit();
                    cancelDialog();
                } else {
                    startDialog(getString(R.string.warning_title), getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
                }
            }
        });

        favCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.isNetwork(getActivity())) {
                    Intent intent = new Intent(getActivity(), FavouriteCityActivity.class);
                    startActivity(intent);
                } else {
                    startDialog(getString(R.string.warning_title), getString(R.string.connection_error), SweetAlertDialog.ERROR_TYPE);
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog(getString(R.string.warning_title), getString(R.string.sign_out_dialog), SweetAlertDialog.WARNING_TYPE);
            }
        });

    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        dialog = new SweetAlertDialog(getActivity(), type);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText(title);
        if (message.equalsIgnoreCase(getString(R.string.sign_out_dialog))) {
            dialog.setContentText(message);
            dialog.setConfirmButton(getString(R.string.yes_dialog), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    mPresenter.signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    cancelDialog();
                }
            });
            dialog.setCancelButton(getString(R.string.dialog_cancel), new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    cancelDialog();
                }
            });
        } else {
            if (!message.equalsIgnoreCase(getString(R.string.loading_message))) {
                dialog.setConfirmText(getString(R.string.dialog_ok));
                dialog.setContentText(message);
            }
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
        name.setText(user.getName() + " " + user.getSurname());
        mailAccount.setText(user.getMail());

        //creo menu admin
        if (user.getRole().equalsIgnoreCase(Constants.TAG_ADMIN)
                || user.getRole().equalsIgnoreCase(Constants.TAG_MOD)) {

            TextView textView = new TextView(getActivity());
            textView.setText(R.string.admin_text);
            textView.setGravity(Gravity.LEFT);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            textView.setLayoutParams(param);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(14);

            TextView textView1 = new TextView(getActivity());
            textView1.setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
            textView1.setLayoutParams(param1);
            textView1.setTextColor(Color.GRAY);
            textView1.setTextSize(18);
            textView1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_arrow, 0);

            View viewTW = new View(getActivity());
            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            viewTW.setLayoutParams(param2);
            viewTW.setBackground(getResources().getDrawable(R.color.gray_btn_bg_pressed_color));

            adminLayout.addView(textView);
            adminLayout.addView(textView1);

            adminLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AdminActivity.class);
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
