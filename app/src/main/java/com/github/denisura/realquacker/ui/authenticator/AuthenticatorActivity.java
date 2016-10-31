package com.github.denisura.realquacker.ui.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.RealQuackerApplication;
import com.github.denisura.realquacker.SingleFragmentActivity;
import com.github.denisura.realquacker.account.AppAccount;
import com.github.denisura.realquacker.data.model.AccountModel;
import com.github.denisura.realquacker.data.network.TwitterApi;
import com.github.denisura.realquacker.data.network.TwitterService;
import com.github.denisura.realquacker.data.sync.QuackerSyncAdapter;

import java.io.IOException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import retrofit2.Call;
import timber.log.Timber;

import static com.github.denisura.realquacker.account.AppAccount.AUTHTOKEN_TYPE_FULL_ACCESS;

public class AuthenticatorActivity extends SingleFragmentActivity {

    private OAuthProvider mProvider;
    private OAuthConsumer mConsumer;
    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    public static final String EXTRA_TOKEN_TYPE = "com.github.denisura.realquacker.EXTRA_TOKEN_TYPE";

    public static Intent newIntent(Context packageContext, String accountType) {
        final Intent intent = new Intent(packageContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.EXTRA_TOKEN_TYPE, accountType);
        return intent;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        if (uri != null) {
            Timber.d("new intent url %s", uri.toString());
            if (uri.getQueryParameter("oauth_verifier") != null) {
                onOAuthCallback(uri);
            }
            if (uri.getQueryParameter("denied") != null) {
                Timber.d("Boo denied");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, createFragment())
                        .commit();
            }
        }
    }

    private void onOAuthCallback(final Uri uri) {
        mConsumer = RealQuackerApplication.getOAuthConsumer();
        mProvider = RealQuackerApplication.getOAuthProvider();
        new AsyncTask<Void, Void, Bundle>() {

            @Override
            protected Bundle doInBackground(Void... voids) {
                String pinCode = uri.getQueryParameter("oauth_verifier");
                try {
                    mProvider.retrieveAccessToken(mConsumer, pinCode);

                    String token = mConsumer.getToken();
                    String tokenSecret = mConsumer.getTokenSecret();

                    TwitterApi _apiService = TwitterService.newService(token, tokenSecret);
                    Call<AccountModel> call = _apiService.getAccountDetails();
                    AccountModel accountModel = call.execute().body();

                    Bundle bundle = new Bundle();
                    bundle.putString("token", mConsumer.getToken());
                    bundle.putString("tokenSecret", mConsumer.getTokenSecret());
                    bundle.putString("screenName", accountModel.screenName);

                    return bundle;
                } catch (OAuthMessageSignerException
                        | OAuthNotAuthorizedException
                        | OAuthExpectationFailedException
                        | OAuthCommunicationException
                        | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bundle bundle) {
                final AccountManager am = AccountManager.get(AuthenticatorActivity.this);
                final Bundle result = new Bundle();
                Account newAccount = new Account(bundle.getString("screenName"), AppAccount.TYPE);
                if (am.addAccountExplicitly(newAccount, null, bundle)) {
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, newAccount.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, newAccount.type);
                    am.setAuthToken(newAccount, AUTHTOKEN_TYPE_FULL_ACCESS, bundle.getString("token"));
                    QuackerSyncAdapter.syncImmediately(newAccount);
                } else {
                    result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_already_exists));
                }
                Timber.d("Save token and accountModel and finish");
                setAccountAuthenticatorResult(result);
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    @Override
    protected Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    /**
     * Retreives the AccountAuthenticatorResponse from either the intent of the icicle, if the
     * icicle is non-zero.
     *
     * @param icicle the save instance data of this Activity, may be null
     */
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }
}

