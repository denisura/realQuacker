package com.github.denisura.realquacker.ui.profile;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.SingleFragmentActivity;
import com.github.denisura.realquacker.data.model.User;
import com.github.denisura.realquacker.data.sync.QuackerSyncAdapter;
import com.github.denisura.realquacker.utils.AccountUtils;

import butterknife.BindView;
import timber.log.Timber;

public class ProfileActivity extends SingleFragmentActivity

        implements AppBarLayout.OnOffsetChangedListener, ProfileFragment.Callbacks {


    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;


    private static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;


    @BindView(R.id.app_bar_layout)
    public AppBarLayout mAppBarLayout;


    @BindView(R.id.toolbar_title)
    public TextView mTitle;


    @BindView(R.id.imgToolbar)
    public ImageView mProfileImage;

    @BindView(R.id.name)
    public TextView mName;

    @BindView(R.id.screen_name)
    public TextView mScreenName;

    @BindView(R.id.description)
    public TextView mDescription;


    @BindView(R.id.following)
    public TextView mFollowing;

    @BindView(R.id.followers)
    public TextView mFollowers;

    static String mUsername;

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_profile;
    }

    @Override
    protected Fragment createFragment() {
        return ProfileFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppBarLayout.addOnOffsetChangedListener(this);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Account account = AccountUtils.getCurrentAccount(this);
        if (account == null) {
            return;
        }

        if (getIntent().hasExtra(EXTRA_USERNAME)) {
            mUsername = getIntent().getStringExtra(EXTRA_USERNAME);
        }

        getSupportActionBar().setTitle(mUsername);
        QuackerSyncAdapter.syncHomeTimelineImmediately(account);
    }

    public static Intent newIntent(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, ProfileActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {

        Timber.d("Persantage %f", percentage);
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }


    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onProfileLoaded(User user) {
        Timber.d("onLoadFinished profile name %s ", user.getName());
        Glide.with(this)
                .load(user.getProfileImageUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mProfileImage);
        mName.setText(user.getName());
        mScreenName.setText(user.getScreenName());
        mDescription.setText(user.getDescription());
        mFollowers.setText(getResources().getString(R.string.format_followers,user.followersCount));
        mFollowing.setText(getResources().getString(R.string.format_following,user.friendsCount));
    }
}
