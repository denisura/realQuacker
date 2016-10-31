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
        public static final String TWEETS = AppContract.TweetEntry.TABLE_NAME;
        @Table(UserColumns.class)
        public static final String USERS = AppContract.UserEntry.TABLE_NAME;
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {
    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.TWEETS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);
        onCreate(context, db);
    }
}