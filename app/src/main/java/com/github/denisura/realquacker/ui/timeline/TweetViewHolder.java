package com.github.denisura.realquacker.ui.timeline;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.data.model.Tweet;
import com.github.denisura.realquacker.ui.profile.ProfileActivity;
import com.github.denisura.realquacker.utils.JodaUtils;
import com.github.denisura.realquacker.utils.PatternEditableBuilder;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class TweetViewHolder
        extends RecyclerView.ViewHolder {


    @BindView(R.id.profileUrl)
    ImageView mProfileUrl;

    @BindView(R.id.name)
    TextView mName;


    @BindView(R.id.screen_name)
    TextView mScreenName;

    @BindView(R.id.text)
    TextView mText;


    @BindView(R.id.created_at)
    TextView mCreatedAt;


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
        mScreenName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("Open profile for %s", tweet.getUser().getScreenName());
                Intent intent = ProfileActivity.newIntent(mProfileUrl.getContext(), tweet.getUser().getScreenName());
                mProfileUrl.getContext().startActivity(intent);
            }
        });
        mText.setText(tweet.getText());
        mCreatedAt.setText(JodaUtils.getRelativeTimeAgo(tweet.getCreatedAt()));

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"),
                        mProfileUrl.getContext().getResources().getColor(R.color.colorAccent),
                        new PatternEditableBuilder.SpannableClickedListener() {
                            @Override
                            public void onSpanClicked(String text) {
                                Timber.d("Open profile for %s", text);

                                Intent intent = ProfileActivity.newIntent(mProfileUrl.getContext(), text.substring(1));
                                mProfileUrl.getContext().startActivity(intent);
                            }
                        }).into(mText);

        mProfileUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("on profile image click %s", tweet.getUser().getScreenName());
                Intent intent = ProfileActivity.newIntent(mProfileUrl.getContext(), tweet.getUser().getScreenName());
                mProfileUrl.getContext().startActivity(intent);
            }
        });
    }
}


