package com.simonepirozzi.techevent.ui.account.editAccount;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.Event;
import com.simonepirozzi.techevent.data.db.model.User;
import com.simonepirozzi.techevent.ui.account.AccountContract;
import com.simonepirozzi.techevent.ui.account.AccountPresenter;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditAccountFragment extends Fragment implements AccountContract.View {
    EditText name, surname;
    Button save, delete;
    SweetAlertDialog dialog;
    AccountPresenter mPresenter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        name = view.findViewById(R.id.Mod_Name);
        surname = view.findViewById(R.id.Mod_Sur);
        save = view.findViewById(R.id.save_button);
        delete = view.findViewById(R.id.delete_button);
        mPresenter = new AccountPresenter(getActivity(), this);
        mPresenter.getAccount();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveEditAccount(name.getText().toString(), surname.getText().toString());
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog( getString(R.string.warning_title), getString(R.string.delete_account_warn), SweetAlertDialog.WARNING_TYPE);
            }
        });

    }

    @Override
    public SweetAlertDialog startDialog(String title, String message, int type) {
        cancelDialog();
        dialog = new SweetAlertDialog(getActivity(), type);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText(title);
        if (!message.equalsIgnoreCase(getString(R.string.loading_message))) {

            if (message.equalsIgnoreCase(getString(R.string.delete_account_warn))) {
                dialog.setContentText(message);
                dialog.setCancelButton(getString(R.string.dialog_cancel), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        cancelDialog();
                    }
                });
                dialog.setConfirmButton(getString(R.string.yes_dialog), new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.insert_pass);
                        final EditText input = new EditText(getActivity());
                        input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        builder.setView(input);
                        builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.cancel();
                                cancelDialog();
                                mPresenter.reauthenticate(input.getText().toString());
                            }
                        });
                        builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                cancelDialog();
                            }
                        });

                        builder.show();

                    }
                });
            } else {
                if (!message.equalsIgnoreCase(getString(R.string.loading_message))) {
                    dialog.setConfirmText(getString(R.string.dialog_ok));
                    dialog.setContentText(message);
                }

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
        name.setText(user.getName());
        surname.setText(user.getSurname());
        cancelDialog();
    }

    @Override
    public void setEventLayout(List<Event> events) {

    }
}
