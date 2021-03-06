package com.simonepirozzi.techevent.ui.main;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simonepirozzi.techevent.ui.account.AccountFragment;
import com.simonepirozzi.techevent.ui.event.EventsFragment;
import com.simonepirozzi.techevent.ui.event.publish.PublishEventActivity;
import com.simonepirozzi.techevent.ui.home.HomeFragment;
import com.simonepirozzi.techevent.ui.favourite.FavouriteFragment;
import com.simonepirozzi.techevent.R;
import com.simonepirozzi.techevent.ui.search.RicercaFragment;
import com.simonepirozzi.techevent.utils.Constants;

public class MainActivity extends Activity implements MainContract.View {
    BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_search:
                    fragment = new RicercaFragment();
                    break;
                case R.id.navigation_preferiti:
                    fragment = new FavouriteFragment();
                    break;
                case R.id.nuovo_evento:
                    fragment = new EventsFragment();
                    break;
                case R.id.navigation_account:
                    fragment = new AccountFragment();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainPresenter mPresenter = new MainPresenter(this, this);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mPresenter.checkSelectedNav();
    }

    @Override
    public void setSelectedNavigation(int id) {
        switch (id) {
            case R.id.navigation_preferiti:
                navigation.setSelectedItemId(R.id.navigation_preferiti);
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new FavouriteFragment()).commit();
                break;
            case R.id.nuovo_evento:
                navigation.setSelectedItemId(R.id.nuovo_evento);
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new EventsFragment()).commit();
                Intent intent = new Intent(MainActivity.this, PublishEventActivity.class);
                startActivity(intent);
                break;
            case R.id.navigation_home:
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new HomeFragment()).commit();
                break;
            case R.id.navigation_account:
                navigation.setSelectedItemId(R.id.navigation_account);
                getFragmentManager().beginTransaction().replace(R.id.frame_container, new AccountFragment(), Constants.TAG_ACCOUNT).commit();
                break;
        }
    }

}
