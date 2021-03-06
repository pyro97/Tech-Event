package com.simonepirozzi.techevent.ui.account.privacy;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.simonepirozzi.techevent.R;

public class PrivacyFragment extends Fragment {


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_privacy, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        WebView webView=view.findViewById(R.id.webView);
        webView.loadUrl("https://tech-event-campani.flycricket.io/privacy.html");
    }
}
