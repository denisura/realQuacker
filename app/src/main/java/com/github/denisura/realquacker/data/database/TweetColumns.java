package com.github.denisura.realquacker.data.database;

import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;
import static net.simonvt.schematic.annotation.DataType.Type.TEXT;

public interface TweetColumns {

    @DataType(INTEGER)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    String _ID = "_id";

    @DataType(TEXT)
    @NotNull
    String CREATED_AT = "created_at";

    @DataType(TEXT)
    @NotNull
    String TWEET = "text";

    @DataType(INTEGER)
    @NotNull
    String USER_ID = "user_id";

    @DataType(INTEGER)
    @NotNull
    String RETWEET_COUNT = "retweet_count";

    @DataType(INTEGER)
    @NotNull
    String FAVORITE_COUNT = "favorite_count";

    @DataType(INTEGER)
    @NotNull
    String FAVORITED = "favorited";

    @DataType(INTEGER)
    @NotNull
    String RETWEETED = "retweeted";

    String USER_NAME = "user_name";
    String USER_SCREEN_NAME = "user_screen_name";
    String USER_DESCRIPTION = "user_description";
    String USER_FOLLOWERS_COUNT = "user_followers_count";
    String USER_FRIENDS_COUNT = "user_friends_count";
    String USER_FAVOURITES_COUNT = "user_favourites_count";
    String USER_STATUSES_COUNT = "user_statuses_count";
    String USER_PROFILE_IAMGE_URL = "user_profile_image_url";

}
