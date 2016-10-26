package com.github.denisura.realquacker.ui.authenticator;

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
import com.github.denisura.realquacker.data.model.Account;
import com.github.denisura.realquacker.data.network.TwitterApi;
import com.github.denisura.realquacker.data.network.TwitterService;

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
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Timber.d("new intent");
        Uri uri = intent.getData();
        if (uri != null) {
            onOAuthCallback(uri);
        }
    }

    private void onOAuthCallback(final Uri uri) {
        mConsumer = RealQuackerApplication.getOAuthConsumer();
        mProvider = RealQuackerApplication.getOAuthProvider();
        new AsyncTask<Void, Void, Account>() {

            @Override
            protected Account doInBackground(Void... voids) {
                String pinCode = uri.getQueryParameter("oauth_verifier");
                try {
                    mProvider.retrieveAccessToken(mConsumer, pinCode);

                    String token = mConsumer.getToken();
                    String tokenSecret = mConsumer.getTokenSecret();

                    TwitterApi _apiService = TwitterService.newService(token, tokenSecret);
                    Call<Account> call = _apiService.getAccountDetails();
                    Account account = call.execute().body();
                    account.token = mConsumer.getToken();
                    account.tokenSecret = mConsumer.getTokenSecret();

                    Timber.d("Username %s", account.screenName);

                    return account;
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
            protected void onPostExecute(Account account) {
                final AccountManager am = AccountManager.get(AuthenticatorActivity.this);
                final Bundle result = new Bundle();
                AppAccount newAccount = new AppAccount(account.screenName);
                Bundle bundle = new Bundle();
                bundle.putString("token", account.token);
                bundle.putString("tokenSecret", account.tokenSecret);
                if (am.addAccountExplicitly(newAccount, null, bundle)) {
                    result.putString(AccountManager.KEY_ACCOUNT_NAME, newAccount.name);
                    result.putString(AccountManager.KEY_ACCOUNT_TYPE, newAccount.type);
                    am.setAuthToken(newAccount, AUTHTOKEN_TYPE_FULL_ACCESS, account.token);
                } else {
                    result.putString(AccountManager.KEY_ERROR_MESSAGE, getString(R.string.account_already_exists));
                }




                Timber.d("Save token and account and finish");
                setAccountAuthenticatorResult(result);
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }

    @Override
    protected Fragment createFragment() {

        Uri uri = this.getIntent().getData();
        if (uri != null) {
            return SuccessFragment.newInstance();
        }
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

