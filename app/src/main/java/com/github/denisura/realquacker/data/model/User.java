package com.github.denisura.realquacker.data.model;


import android.content.ContentValues;

import com.github.denisura.realquacker.data.database.UserColumns;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    public Long id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("screen_name")
    @Expose
    public String screenName;

    @SerializedName("description")
    @Expose
    public String description;

    @SerializedName("followers_count")
    @Expose
    public Integer followersCount;

    @SerializedName("friends_count")
    @Expose
    public Integer friendsCount;

    @SerializedName("favourites_count")
    @Expose
    public Integer favouritesCount;

    @SerializedName("statuses_count")
    @Expose
    public Integer statusesCount;

    @SerializedName("profile_image_url")
    @Expose
    public String profileImageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }

    public Integer getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(Integer friendsCount) {
        this.friendsCount = friendsCount;
    }

    public Integer getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(Integer favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public Integer getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(Integer statusesCount) {
        this.statusesCount = statusesCount;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserColumns._ID, getId());
        contentValues.put(UserColumns.NAME, getName());
        contentValues.put(UserColumns.SCREEN_NAME, getScreenName());
        contentValues.put(UserColumns.DESCRIPTION, getDescription());
        contentValues.put(UserColumns.FOLLOWERS_COUNT, getFollowersCount());
        contentValues.put(UserColumns.FRIENDS_COUNT, getFriendsCount());
        contentValues.put(UserColumns.FAVOURITES_COUNT, getFavouritesCount());
        contentValues.put(UserColumns.STATUSES_COUNT, getStatusesCount());
        contentValues.put(UserColumns.PROFILE_IAMGE_URL, getProfileImageUrl());
        return contentValues;
    }

}