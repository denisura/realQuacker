package com.github.denisura.realquacker.data.database;

import android.content.ContentResolver;
import android.net.Uri;

public class AppContract {

    public static final String CONTENT_AUTHORITY = "com.github.denisura.realquacker";
    public static final String PROVIDER_NAMESPACE = "com.github.denisura.realquacker.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TWEETS = "tweets";
    public static final String PATH_USERS = "users";
//    public static final String PATH_MENTIONS = "mentions";
//    public static final String PATH_TIMELINE = "timeline";

    public static final class HomeTimelineEntry implements TweetColumns {
        public static final String TABLE_NAME = "home_timeline";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TWEETS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TWEETS;
    }

    public static final class MentionsTimelineEntry implements TweetColumns {
        public static final String TABLE_NAME = "mentions";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TWEETS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TWEETS;
    }


    public static final class UserEntry implements UserColumns {
        public static final String TABLE_NAME = "users";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
    }

//    public static final class TimelineEntry implements TimelineColumns{
//        public static final String TABLE_NAME = "timeline";
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMELINE;
//        public static final String CONTENT_ITEM_TYPE =
//                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMELINE;
//    }
}
