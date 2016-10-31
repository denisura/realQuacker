package com.github.denisura.realquacker.ui.timeline;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.denisura.realquacker.CursorRecyclerViewAdapter;
import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.data.model.Tweet;

public class TimelineAdapter extends CursorRecyclerViewAdapter<TweetViewHolder>
         {


    private Context mContext;
    public long mSelected = -1;


    public TimelineAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        if (viewGroup instanceof RecyclerView) {
            int layoutId = R.layout.cardview_tweet_item;
            View view = layoutInflater.inflate(layoutId, viewGroup, false);
            return new TweetViewHolder(view);
        }
        throw new RuntimeException("Not bound to RecyclerView");
    }

    @Override
    public void onBindViewHolder(TweetViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindTweet(new Tweet(cursor));
    }
}