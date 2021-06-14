package com.simonepirozzi.techevent.ui.account.admin.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.model.User;

import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomAdapterListaAdmin extends ArrayAdapter<User> {
    private int resource;
    private LayoutInflater inflater;
    private FirebaseFirestore db;

    User user;
    String giorno, mese;
    int numero;

    public CustomAdapterListaAdmin(Context context, int resourceId, List<User> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        if (v == null) {
            v = inflater.inflate(R.layout.list_admin, null);
        }

        user = getItem(position);


        final TextView mail, ruolo, luogo;
        final LinearLayout linearLayout;
        final AppCompatImageView imageView;

        linearLayout = v.findViewById(R.id.adminElement);
        mail = v.findViewById(R.id.mailElement);
        ruolo = v.findViewById(R.id.roleElement);


        mail.setText(user.getMail());
        ruolo.setText(user.getRole());

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", mail.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copiata!", Toast.LENGTH_SHORT).show();
            }
        });


        mail.setTag(position);

        ruolo.setTag(position);

        return v;
    }
}

