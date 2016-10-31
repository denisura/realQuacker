package com.github.denisura.realquacker.ui.timeline;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.utils.JodaUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetViewHolder
        extends RecyclerView.ViewHolder {


    @BindView(R.id.profileUrl)
    ImageView mProfileUrl;

    @BindView(R.id.name)
    TextView mName;


    @BindView(R.id.screen_name)
     TextView  mScreenName;

    @BindView(R.id.text)
    TextView  mText;


    @BindView(R.id.created_at)
    TextView  mCreatedAt;


    TweetViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public void bindTweet(Tweet tweet) {

        Glide.with(mProfileUrl.getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(mProfileUrl);
        mName.setText(tweet.getUser().getName());
        mScreenName.setText("@" + tweet.getUser().getScreenName());
        mText.setText(tweet.getText());
        mCreatedAt.setText(JodaUtils.getRelativeTimeAgo(tweet.getCreatedAt()));
    }
}


