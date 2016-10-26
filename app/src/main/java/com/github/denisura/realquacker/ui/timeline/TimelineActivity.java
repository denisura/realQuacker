package com.github.denisura.realquacker.ui.timeline;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.account.AppAccount;

import timber.log.Timber;

import static com.github.denisura.realquacker.account.AppAccount.AUTHTOKEN_TYPE_FULL_ACCESS;

public class TimelineActivity extends AppCompatActivity {

    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        mAccountManager = AccountManager.get(this);

       // showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, false);

        getTokenForAccountCreateIfNeeded(AppAccount.TYPE, AUTHTOKEN_TYPE_FULL_ACCESS);

        //get user account


//        mCLient = RealQuackerApplication.getRestClient();
        //   populateTimeline();

//        initTwitter();
//
//
//        mWebView = (WebView) findViewById(R.id.webview);
//        mWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                if (url.startsWith("oauth")) {
//                    Uri uri = Uri.parse(url);
//                    onOAuthCallback(uri);
//                    return true;
//                }
//                return super.shouldOverrideUrlLoading(view, url);
//            }
//        });
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                            Timber.d("getTokenForAccountCreateIfNeeded %s", authtoken);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                , null);
    }

    private boolean mInvalidate;

    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
        mInvalidate = invalidate;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AppAccount.TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (invalidate) {
                        //  invalidateAuthToken(availableAccounts[which], authTokenType);
                    } else
                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                }
            }).create();
            mAlertDialog.show();
        }
    }

    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    Timber.d("getExistingAccountAuthToken %s", authtoken);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }

//    private void populateTimeline() {
//        mCLient.getHomeTimeline(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//                Timber.d("Success %s", response.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Timber.d("Failure %s", errorResponse.toString());
//            }
//        });
//    }
//
//
//    private void initTwitter() {
//
//        mConsumer = new DefaultOAuthConsumer(
//                BuildConfig.QUACKER_CONSUMER_KEY,
//                BuildConfig.QUACKER_CONSUMER_SECRET);
//
//        mProvider = new DefaultOAuthProvider(
//                "https://api.twitter.com/oauth/request_token",
//                "https://api.twitter.com/oauth/access_token",
//                "https://api.twitter.com/oauth/authorize");
//
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                try {
//                    mAuthUrl = mProvider.retrieveRequestToken(mConsumer, REST_CALLBACK_URL);
//                    Timber.d("mAuthUrl %s", mAuthUrl);
//                } catch (OAuthMessageSignerException e) {
//                    e.printStackTrace();
//                } catch (OAuthNotAuthorizedException e) {
//                    e.printStackTrace();
//                } catch (OAuthExpectationFailedException e) {
//                    e.printStackTrace();
//                } catch (OAuthCommunicationException e) {
//                    e.printStackTrace();
//                }
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void v) {
//
//                Timber.d("mAuthUrl starts with %s ", mAuthUrl);
//
//                if (!mAuthUrl.equals("")) {
//                    //mWebView.loadUrl(mAuthUrl);
//                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
//                            .addDefaultShareMenuItem()
//                            .build();
//
//                    CustomTabActivityHelper.openCustomTab(TimelineActivity.this, customTabsIntent, Uri.parse(mAuthUrl),
//                            (activity, uri) -> {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                activity.startActivity(intent);
//                            });
//                }
//
//            }
//        }.execute();
//    }


//    private void onOAuthCallback(final Uri uri) {
//
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//
//                String pinCode = uri.getQueryParameter("oauth_verifier");
//                try {
//                    mProvider.retrieveAccessToken(mConsumer, pinCode);
//
//                    String token = mConsumer.getToken();
//                    String tokenSecret = mConsumer.getTokenSecret();
//
//                    Timber.d("oauthtoken %s; secret: %s", token, tokenSecret);
////                    PrefUtil.setTwitterToken(getActivity(), token);
////                    PrefUtil.setTwitterTokenSecret(getActivity(), tokenSecret);
//
//                } catch (OAuthMessageSignerException e) {
//                    e.printStackTrace();
//                } catch (OAuthNotAuthorizedException e) {
//                    e.printStackTrace();
//                } catch (OAuthExpectationFailedException e) {
//                    e.printStackTrace();
//                } catch (OAuthCommunicationException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                finish();
//            }
//        }.execute();
//
//    }
}
