package com.github.denisura.realquacker;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.squareup.leakcanary.LeakCanary;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import timber.log.Timber;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class RealQuackerApplication extends Application {

    private static Context mContext;
    private static OAuthConsumer mConsumer;
    private static OAuthProvider mProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        enabledStrictMode();
        LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
        RealQuackerApplication.mContext = this;
    }


    private void enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll() //
                    .penaltyLog() //
                    .penaltyDeath() //
                    .build());
        }
    }

    public static OAuthConsumer getOAuthConsumer() {

        if (mConsumer == null) {
            mConsumer = new DefaultOAuthConsumer(
                    BuildConfig.QUACKER_CONSUMER_KEY,
                    BuildConfig.QUACKER_CONSUMER_SECRET);
        }
        return mConsumer;
    }


    public static OAuthProvider getOAuthProvider() {

        if (mProvider == null) {
            mProvider = new DefaultOAuthProvider(
                    "https://api.twitter.com/oauth/request_token",
                    "https://api.twitter.com/oauth/access_token",
                    "https://api.twitter.com/oauth/authorize");
        }
        return mProvider;
    }
}
