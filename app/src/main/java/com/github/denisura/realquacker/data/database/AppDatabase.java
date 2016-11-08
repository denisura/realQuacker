package com.github.denisura.realquacker.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = AppDatabase.VERSION,
        packageName = AppContract.PROVIDER_NAMESPACE)

public final class AppDatabase {
    private AppDatabase() {
    }

    public static final int VERSION = 1;

    public static class Tables {
        @Table(TweetColumns.class)
        public static final String TWEETS = AppContract.HomeTimelineEntry.TABLE_NAME;
        @Table(UserColumns.class)
        public static final String USERS = AppContract.UserEntry.TABLE_NAME;
        @Table(TweetColumns.class)
        public static final String MENTIONS = AppContract.MentionsTimelineEntry.TABLE_NAME;
        @Table(TweetColumns.class)
        public static final String TIMELINE = AppContract.TimelineEntry.TABLE_NAME;
        @Table(TimelineColumns.class)
        public static final String USER_TWEET_MAP = AppContract.UserTweetEntry.TABLE_NAME;
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TWEETS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.MENTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TIMELINE);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USER_TWEET_MAP);
        onCreate(context, db);
    }
}