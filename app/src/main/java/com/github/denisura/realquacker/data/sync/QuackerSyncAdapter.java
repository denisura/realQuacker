package com.github.denisura.realquacker.data.sync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;

import com.github.denisura.realquacker.data.database.AppContract;
import com.github.denisura.realquacker.data.database.AppProvider;
import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.data.network.TwitterApi;
import com.github.denisura.realquacker.data.network.TwitterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import timber.log.Timber;

public class QuackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String SYNC_EXTRAS_MAX_ID = "maxId";


    public QuackerSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Timber.d("onPerformSync: %s", account.toString());

        AccountManager accountManager =
                (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);

        String token = accountManager.getUserData(account, "token");
        String tokenSecret = accountManager.getUserData(account, "tokenSecret");
        Timber.d("onPerformSync: token %s secret %s", token, tokenSecret);

        TwitterApi _apiService = TwitterService.newService(token, tokenSecret);
        long maxId = extras.getLong(SYNC_EXTRAS_MAX_ID, Long.MAX_VALUE / 10);
        Call<List<Tweet>> call = _apiService.getTimeline(25, maxId);
        try {
            List<Tweet> tweets = call.execute().body();
            Timber.d("Found %d tweets", tweets.size());
            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

            ContentProviderOperation.Builder builder;

            HashMap<Long, ContentValues> usersMap = new HashMap();

            if (maxId == Long.MAX_VALUE / 10) {
                builder = ContentProviderOperation.newDelete(AppProvider.Tweets.CONTENT_URI);
                batch.add(builder.build());
            }

            for (Tweet tweet : tweets) {
                Timber.d("Tweet id: %d by %s", tweet.id, tweet.user.screenName);
                builder = ContentProviderOperation.newInsert(AppProvider.Tweets.CONTENT_URI);
                builder.withValues(tweet.toContentValues());
                batch.add(builder.build());
                usersMap.put(tweet.getUser().getId(), tweet.getUser().toContentValues());
            }

            for (Long userId : usersMap.keySet()) {
                builder = ContentProviderOperation.newInsert(AppProvider.Users.CONTENT_URI);
                builder.withValues(usersMap.get(userId));
                batch.add(builder.build());
            }


            int operations = batch.size();
            if (operations > 0) {
                try {
                    getContext().getContentResolver().applyBatch(AppContract.CONTENT_AUTHORITY, batch);
                } catch (RemoteException | OperationApplicationException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            //TODO let people know about IO error
            e.printStackTrace();
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     */
    public static void syncImmediately(Account syncAccount) {
        Timber.d("syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

    public static void syncImmediately(Account syncAccount, long maxId) {
        Timber.d("syncImmediately max id %d", maxId);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putLong(SYNC_EXTRAS_MAX_ID, maxId);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }
}