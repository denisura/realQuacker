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
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.StringDef;

import com.github.denisura.realquacker.data.database.AppContract;
import com.github.denisura.realquacker.data.database.AppProvider;
import com.github.denisura.realquacker.data.database.TimelineColumns;
import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.data.network.TwitterApi;
import com.github.denisura.realquacker.data.network.TwitterService;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class QuackerSyncAdapter extends AbstractThreadedSyncAdapter {

    private final static String SYNC_EXTRAS_MAX_ID = "maxId";
    private final static String SYNC_EXTRAS_USERNAME = "userId";
    private final static String SYNC_EXTRAS_TIMELINE_TYPE = "timelineType";
    private final static int TWEETS_COUNT = 12;


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({HOME_TIMELINE, USER_TIMELINE, MENTIONS_TIMELINE})
    public @interface TimelineTypes {
    }

    // Declare the constants
    public static final String HOME_TIMELINE = "home";
    public static final String USER_TIMELINE = "user";
    public static final String MENTIONS_TIMELINE = "mentions";

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
        long appUserId = Long.valueOf(accountManager.getUserData(account, "userId"));

        Timber.d("onPerformSync: token %s secret %s appUserId %d", token, tokenSecret, appUserId);

        TwitterApi _apiService = TwitterService.newService(token, tokenSecret);
        long maxId = extras.getLong(SYNC_EXTRAS_MAX_ID, Long.MAX_VALUE / 10);

        Call<List<Tweet>> call;
        Uri ContentUri;
        String fetchUserName = "";

        @TimelineTypes String type = extras.getString(SYNC_EXTRAS_TIMELINE_TYPE, HOME_TIMELINE);

        switch (type) {
            case HOME_TIMELINE:
                call = _apiService.getHomeTimeline(TWEETS_COUNT, maxId);
                ContentUri = AppProvider.Tweets.CONTENT_URI;
                break;

            case USER_TIMELINE:
                fetchUserName = extras.getString(SYNC_EXTRAS_USERNAME, "");
                if (fetchUserName.equals("")) {
                    return;
                }
                call = _apiService.getUserTimeline(25, maxId, fetchUserName);
                ContentUri = AppProvider.Timeline.CONTENT_URI;
                break;

            case MENTIONS_TIMELINE:
                call = _apiService.getMentionsTimeline(TWEETS_COUNT, maxId);
                ContentUri = AppProvider.Mentions.CONTENT_URI;
                break;
            default:
                return;
        }

        try {
            Response<List<Tweet>> response = call.execute();
            if (response.code() == 429) {
                Timber.d("Too Many Requests");
                return;
            }
            List<Tweet> tweets = response.body();

            Timber.d("Found %d %s tweets", tweets.size(), type);
            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

            ContentProviderOperation.Builder builder;

            HashMap<Long, ContentValues> usersMap = new HashMap();

            if (maxId == Long.MAX_VALUE / 10) {
                switch (type) {
                    case HOME_TIMELINE:
                        builder = ContentProviderOperation.newDelete(ContentUri);
                        batch.add(builder.build());
                        break;
                    case USER_TIMELINE:
                        builder = ContentProviderOperation.newDelete(ContentUri);
                        batch.add(builder.build());
                        builder = ContentProviderOperation.newDelete(AppProvider.UserTweet.CONTENT_URI);
                        batch.add(builder.build());
                        break;
                    case MENTIONS_TIMELINE:
                        builder = ContentProviderOperation.newDelete(AppProvider.Mentions.CONTENT_URI);
                        batch.add(builder.build());
                        break;
                }
            }

            for (Tweet tweet : tweets) {
                Timber.d("%s Tweet id: %d by %s ContentUri: %s", type, tweet.id, tweet.user.screenName, ContentUri);
                builder = ContentProviderOperation.newInsert(ContentUri);
                builder.withValues(tweet.toContentValues());
                batch.add(builder.build());
                usersMap.put(tweet.getUser().getId(), tweet.getUser().toContentValues());

                if (type.equals(USER_TIMELINE)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TimelineColumns.TWEET_ID, tweet.getId());
                    contentValues.put(TimelineColumns.TIMELINE_USER, fetchUserName);
                    builder = ContentProviderOperation.newInsert(AppProvider.UserTweet.CONTENT_URI);
                    builder.withValues(contentValues);
                    batch.add(builder.build());
                }
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
    public static void syncHomeTimelineImmediately(Account syncAccount) {
        Timber.d("syncHomeTimelineImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, HOME_TIMELINE);

        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

    public static void syncHomeTimelineImmediately(Account syncAccount, long maxId) {
        Timber.d("syncHomeTimelineImmediately max id %d", maxId);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, HOME_TIMELINE);
        bundle.putLong(SYNC_EXTRAS_MAX_ID, maxId);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }


    public static void syncMentionsTimelineImmediately(Account syncAccount) {
        Timber.d("syncMentionsTimelineImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, MENTIONS_TIMELINE);

        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

    public static void syncMentionsTimelineImmediately(Account syncAccount, long maxId) {
        Timber.d("syncMentionsTimelineImmediately max id %d", maxId);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, MENTIONS_TIMELINE);
        bundle.putLong(SYNC_EXTRAS_MAX_ID, maxId);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

    public static void syncUserTimelineImmediately(Account syncAccount, String screenName) {
        Timber.d("syncUserTimelineImmediately userId %s", screenName);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, USER_TIMELINE);
        bundle.putString(SYNC_EXTRAS_USERNAME, screenName);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

    public static void syncUserTimelineImmediately(Account syncAccount, String screenName, long maxId) {
        Timber.d("syncUserTimelineImmediately userId %s max id %d", screenName, maxId);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString(SYNC_EXTRAS_TIMELINE_TYPE, USER_TIMELINE);
        bundle.putLong(SYNC_EXTRAS_MAX_ID, maxId);
        bundle.putString(SYNC_EXTRAS_USERNAME, screenName);
        ContentResolver.requestSync(syncAccount, AppContract.CONTENT_AUTHORITY, bundle);
    }

}