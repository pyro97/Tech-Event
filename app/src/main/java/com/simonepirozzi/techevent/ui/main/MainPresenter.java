package com.simonepirozzi.techevent.ui.main;

import android.app.Activity;

import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.data.db.TinyDB;
import com.simonepirozzi.techevent.data.db.TinyManager;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private Activity activity;
    private TinyDB tinyDB;


    public MainPresenter(Activity activity, MainContract.View view) {
        this.activity = activity;
        this.view = view;
        this.tinyDB = TinyManager.getInstance(activity);
    }

    @Override
    public void checkSelectedNav() {
        if (tinyDB.getString(TinyManager.EVENT) != null && tinyDB.getString(TinyManager.EVENT).length() > 0) {
            if (tinyDB.getString(TinyManager.EVENT).equalsIgnoreCase(TinyManager.FAVOURITE)) {
                tinyDB.remove(TinyManager.EVENT);
                view.setSelectedNavigation(R.id.navigation_preferiti);
            } else if (tinyDB.getString(TinyManager.EVENT).equalsIgnoreCase(TinyManager.EVENT_MANAGEMENT)) {
                tinyDB.remove(TinyManager.EVENT);
                view.setSelectedNavigation(R.id.nuovo_evento);
            } else {
                view.setSelectedNavigation(R.id.navigation_home);
            }
        } else if (tinyDB.getString(TinyManager.EDIT_PROFILE) != null && tinyDB.getString(TinyManager.EDIT_PROFILE).length() > 0) {
            if (tinyDB.getString(TinyManager.EDIT_PROFILE).equalsIgnoreCase(TinyManager.YES)) {
                tinyDB.remove(TinyManager.EDIT_PROFILE);
                view.setSelectedNavigation(R.id.navigation_account);
            } else {
                view.setSelectedNavigation(R.id.navigation_home);
            }
        } else {
            view.setSelectedNavigation(R.id.navigation_home);
        }
    }
}
