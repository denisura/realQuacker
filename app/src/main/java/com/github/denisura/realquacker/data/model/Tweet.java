package com.github.denisura.realquacker.data.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.github.denisura.realquacker.data.database.TweetColumns;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Tweet {

    @SerializedName("created_at")
    @Expose
    public String createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @SerializedName("id")

    @Expose
    public Long id;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("user")
    @Expose
    public User user;

    @SerializedName("retweet_count")
    @Expose
    public Integer retweetCount;

    @SerializedName("favorite_count")
    @Expose
    public Integer favoriteCount;

    @SerializedName("favorited")
    @Expose
    public Boolean favorited;

    @SerializedName("retweeted")
    @Expose
    public Boolean retweeted;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Integer retweetCount) {
        this.retweetCount = retweetCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Tweet(Cursor cursor) {

        int idx_id = cursor.getColumnIndex(TweetColumns._ID);
        int idx_created_at = cursor.getColumnIndex(TweetColumns.CREATED_AT);
        int idx_tweet = cursor.getColumnIndex(TweetColumns.TWEET);
        int idx_user_id = cursor.getColumnIndex(TweetColumns.USER_ID);
        int idx_user_name = cursor.getColumnIndex(TweetColumns.USER_NAME);
        int idx_user_screen_name = cursor.getColumnIndex(TweetColumns.USER_SCREEN_NAME);
        int idx_user_profile_image_url = cursor.getColumnIndex(TweetColumns.USER_PROFILE_IAMGE_URL);
//        int idx_retweet_count = cursor.getColumnIndex(TweetColumns.RETWEET_COUNT);
//        int idx_favorite_count = cursor.getColumnIndex(TweetColumns.FAVORITE_COUNT);
//        int idx_favorited = cursor.getColumnIndex(TweetColumns.FAVORITED);
//        int idx_retweeted = cursor.getColumnIndex(TweetColumns.RETWEETED);

        setId(cursor.getLong(idx_id));
        setText(cursor.getString(idx_tweet));
        setCreatedAt(cursor.getString(idx_created_at));

//        setRetweetCount(cursor.getInt(idx_retweet_count));
//        setFavoriteCount(cursor.getInt(idx_favorite_count));
//        setFavorited((cursor.getInt(idx_favorited) > 0));
//        setRetweeted((cursor.getInt(idx_retweeted) > 0));

        //createdAt();
        User user = new User();
        user.setId(cursor.getLong(idx_user_id));
        user.setName(cursor.getString(idx_user_name));
        user.setScreenName(cursor.getString(idx_user_screen_name));
        user.setProfileImageUrl(cursor.getString(idx_user_profile_image_url));

        setUser(user);
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TweetColumns._ID, getId());
        contentValues.put(TweetColumns.CREATED_AT, getCreatedAt());
        contentValues.put(TweetColumns.TWEET, getText());
        contentValues.put(TweetColumns.USER_ID, getUser().id);
        contentValues.put(TweetColumns.RETWEET_COUNT, getRetweetCount());
        contentValues.put(TweetColumns.FAVORITE_COUNT, getFavoriteCount());
        contentValues.put(TweetColumns.FAVORITED, getFavorited());
        contentValues.put(TweetColumns.RETWEETED, getRetweeted());
        return contentValues;
    }
}
