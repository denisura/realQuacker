package com.github.denisura.realquacker.ui.timeline;


import android.content.Context;

import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.utils.JodaUtils;

public class TweetViewModel {
    private final Context mContext;
    private Tweet mTweet;

    public TweetViewModel(Context context) {
        mContext = context;
    }


    public String getThumbnail() {
        return mTweet.getUser().getProfileImageUrl();
    }


    public Tweet getTweet() {
        return mTweet;
    }

    public String getText() {
        return mTweet.text;
    }
    public String getName() {
        return mTweet.getUser().getName();
    }

    public String getScreenName() {
        return "@" + mTweet.getUser().getScreenName();
    }

    public String getCreatedAt() {
        return JodaUtils.getRelativeTimeAgo(mTweet.getCreatedAt());
    }



    public void setTweet(Tweet tweet) {
        mTweet = tweet;
    }



    public void onTweetClick() {
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_action_share);
//
//        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
//                .setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
//                .setActionButton(bitmap, "Share Link", getPendingShareIntent(mContext, mTweet.getWebUrl()), true)
//                .addDefaultShareMenuItem()
//                .build();
//
//        CustomTabActivityHelper.openCustomTab((Activity) mContext, customTabsIntent, Uri.parse(mTweet.getWebUrl()),
//                (activity, uri) -> {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    activity.startActivity(intent);
//                });
    }
}
