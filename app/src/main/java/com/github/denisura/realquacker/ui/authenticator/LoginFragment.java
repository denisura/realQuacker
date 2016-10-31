package com.github.denisura.realquacker.ui.authenticator;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.RealQuackerApplication;
import com.github.denisura.realquacker.customtabsclient.CustomTabActivityHelper;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import timber.log.Timber;

import static com.github.denisura.realquacker.data.network.TwitterApi.REST_CALLBACK_URL;


public class LoginFragment extends Fragment {

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;
    private String mAuthUrl;
    public Button mConnectBtn;

    private void initTwitter() {

        mConnectBtn.setEnabled(false);

        mConsumer = RealQuackerApplication.getOAuthConsumer();
        mProvider = RealQuackerApplication.getOAuthProvider();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, REST_CALLBACK_URL);
                } catch (OAuthMessageSignerException
                        | OAuthNotAuthorizedException
                        | OAuthExpectationFailedException
                        | OAuthCommunicationException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void v) {
                Timber.d("mAuthUrl starts with %s ", mAuthUrl);
                if (!mAuthUrl.equals("")) {
                    mConnectBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_form, container, false);
        mConnectBtn = (Button) rootView.findViewById(R.id.btn_sign_in);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("Clicked on mConnectBtn %s", mAuthUrl);
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                        .setCloseButtonIcon(BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_arrow_back_white_24dp))
                        .setStartAnimations(getContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                        .setExitAnimations(getContext(), R.anim.slide_in_left, R.anim.slide_out_right)
                        .build();

                CustomTabActivityHelper.openCustomTab(getActivity(), customTabsIntent, Uri.parse(mAuthUrl),
                        (activity, uri) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        });
            }
        });
        initTwitter();
        return rootView;
    }
}
