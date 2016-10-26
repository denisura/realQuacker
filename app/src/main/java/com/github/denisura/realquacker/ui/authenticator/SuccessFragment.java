package com.github.denisura.realquacker.ui.authenticator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.denisura.realquacker.R;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

public class SuccessFragment extends Fragment {

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;
    private String mAuthUrl;


    public static SuccessFragment newInstance() {
        return new SuccessFragment();
    }

    public SuccessFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_form, container, false);
        return rootView;
    }

}
