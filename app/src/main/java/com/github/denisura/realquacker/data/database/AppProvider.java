package com.github.denisura.realquacker.data.database;

import android.content.ContentValues;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

@ContentProvider(authority = AppContract.CONTENT_AUTHORITY,
        database = AppDatabase.class,
        packageName = AppContract.PROVIDER_NAMESPACE)
public class AppProvider {

    private AppProvider() {
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = AppContract.BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = AppContract.HomeTimelineEntry.TABLE_NAME)
    public static class Tweets {

        public static String[] PROJECTION = new String[]{
                TweetColumns._ID,
                TweetColumns.USER_ID,
                TweetColumns.TWEET,
                TweetColumns.CREATED_AT,
                TweetColumns.USER_NAME,
                TweetColumns.USER_SCREEN_NAME,
                TweetColumns.USER_PROFILE_IAMGE_URL
        };

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();
            map.put(TweetColumns._ID, AppContract.HomeTimelineEntry.TABLE_NAME + "." + TweetColumns._ID);
            map.put(TweetColumns.USER_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.NAME);
            map.put(TweetColumns.USER_SCREEN_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.SCREEN_NAME);
            map.put(TweetColumns.USER_DESCRIPTION, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.DESCRIPTION);
            map.put(TweetColumns.USER_FOLLOWERS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FOLLOWERS_COUNT);
            map.put(TweetColumns.USER_FRIENDS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FRIENDS_COUNT);
            map.put(TweetColumns.USER_FAVOURITES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FAVOURITES_COUNT);
            map.put(TweetColumns.USER_STATUSES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.STATUSES_COUNT);
            map.put(TweetColumns.USER_PROFILE_IAMGE_URL, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.PROFILE_IAMGE_URL);
            return map;
        }

        @ContentUri(
                path = AppContract.PATH_TWEETS,
                type = AppContract.HomeTimelineEntry.CONTENT_TYPE,
                join = "LEFT JOIN " + AppContract.UserEntry.TABLE_NAME +
                        " ON " + AppContract.UserEntry.TABLE_NAME + "." + UserColumns._ID
                        + " = " + AppContract.HomeTimelineEntry.TABLE_NAME + "." + TweetColumns.USER_ID,
                defaultSort = TweetColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(AppContract.PATH_TWEETS);

        @InexactContentUri(
                path = AppContract.PATH_TWEETS + "/#",
                name = "TWEET_ID",
                join = "INNER JOIN " + AppContract.UserEntry.TABLE_NAME +
                        " ON " + AppContract.UserEntry.TABLE_NAME + "." + UserColumns._ID
                        + " = " + AppContract.HomeTimelineEntry.TABLE_NAME + "." + TweetColumns.USER_ID,
                type = AppContract.HomeTimelineEntry.CONTENT_ITEM_TYPE,
                whereColumn = AppContract.HomeTimelineEntry._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(AppContract.PATH_TWEETS, String.valueOf(id));
        }

        @NotifyInsert(paths = AppContract.PATH_TWEETS)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{
                    AppProvider.Tweets.CONTENT_URI
            };
        }

        public static long getTweetIdFromUri(Uri uri) {
            return Long.valueOf(uri.getPathSegments().get(1));
        }
    }


    @TableEndpoint(table = AppContract.MentionsTimelineEntry.TABLE_NAME)
    public static class Mentions {

        public static String[] PROJECTION = new String[]{
                TweetColumns._ID,
                TweetColumns.USER_ID,
                TweetColumns.TWEET,
                TweetColumns.CREATED_AT,
                TweetColumns.USER_NAME,
                TweetColumns.USER_SCREEN_NAME,
                TweetColumns.USER_PROFILE_IAMGE_URL
        };

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();
            map.put(TweetColumns._ID, AppContract.MentionsTimelineEntry.TABLE_NAME + "." + TweetColumns._ID);
            map.put(TweetColumns.USER_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.NAME);
            map.put(TweetColumns.USER_SCREEN_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.SCREEN_NAME);
            map.put(TweetColumns.USER_DESCRIPTION, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.DESCRIPTION);
            map.put(TweetColumns.USER_FOLLOWERS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FOLLOWERS_COUNT);
            map.put(TweetColumns.USER_FRIENDS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FRIENDS_COUNT);
            map.put(TweetColumns.USER_FAVOURITES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FAVOURITES_COUNT);
            map.put(TweetColumns.USER_STATUSES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.STATUSES_COUNT);
            map.put(TweetColumns.USER_PROFILE_IAMGE_URL, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.PROFILE_IAMGE_URL);
            return map;
        }

        @ContentUri(
                path = AppContract.PATH_MENTIONS,
                type = AppContract.MentionsTimelineEntry.CONTENT_TYPE,
                join = "INNER JOIN " + AppContract.UserEntry.TABLE_NAME +
                        " ON " + AppContract.UserEntry.TABLE_NAME + "." + UserColumns._ID
                        + " = " + AppContract.MentionsTimelineEntry.TABLE_NAME + "." + TweetColumns.USER_ID,
                defaultSort = TweetColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(AppContract.PATH_MENTIONS);

    }


    @TableEndpoint(table = AppContract.TimelineEntry.TABLE_NAME)
    public static class Timeline {

        public static String[] PROJECTION = new String[]{
                TweetColumns._ID,
                TweetColumns.USER_ID,
                TweetColumns.TWEET,
                TweetColumns.CREATED_AT,
                TweetColumns.USER_NAME,
                TweetColumns.USER_SCREEN_NAME,
                TweetColumns.USER_PROFILE_IAMGE_URL
        };

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();
            map.put(TweetColumns._ID, AppContract.TimelineEntry.TABLE_NAME + "." + TweetColumns._ID);
            map.put(TweetColumns.USER_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.NAME);
            map.put(TweetColumns.USER_SCREEN_NAME, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.SCREEN_NAME);
            map.put(TweetColumns.USER_DESCRIPTION, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.DESCRIPTION);
            map.put(TweetColumns.USER_FOLLOWERS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FOLLOWERS_COUNT);
            map.put(TweetColumns.USER_FRIENDS_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FRIENDS_COUNT);
            map.put(TweetColumns.USER_FAVOURITES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.FAVOURITES_COUNT);
            map.put(TweetColumns.USER_STATUSES_COUNT, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.STATUSES_COUNT);
            map.put(TweetColumns.USER_PROFILE_IAMGE_URL, AppContract.UserEntry.TABLE_NAME + "." + UserColumns.PROFILE_IAMGE_URL);
            return map;
        }

        @ContentUri(
                path = AppContract.PATH_TIMELINE,
                type = AppContract.TimelineEntry.CONTENT_TYPE,
                join = "INNER JOIN " + AppContract.UserEntry.TABLE_NAME +
                        " ON " + AppContract.UserEntry.TABLE_NAME + "." + UserColumns._ID
                        + " = " + AppContract.TimelineEntry.TABLE_NAME + "." + TweetColumns.USER_ID,
                defaultSort = TweetColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(AppContract.PATH_TIMELINE);

                @InexactContentUri(
                path = AppContract.PATH_TIMELINE + "/*",
                name = "TIMELINE_USER",
                join = "INNER JOIN " + AppContract.UserEntry.TABLE_NAME +
                        " ON " + AppContract.UserEntry.TABLE_NAME + "." + UserColumns._ID
                        + " = " + AppContract.TimelineEntry.TABLE_NAME + "." + TweetColumns.USER_ID +
                        " INNER JOIN " + AppContract.UserTweetEntry.TABLE_NAME +
                        " ON " + AppContract.TimelineEntry.TABLE_NAME + "." + TweetColumns._ID
                        + " = " + AppContract.UserTweetEntry.TABLE_NAME + "." + TimelineColumns.TWEET_ID
                ,
                type = AppContract.HomeTimelineEntry.CONTENT_ITEM_TYPE,
                whereColumn = AppContract.UserTweetEntry.TIMELINE_USER,
                pathSegment = 1)
        public static Uri withId(String username) {
            return buildUri(AppContract.PATH_TIMELINE, username);
        }
    }

    @TableEndpoint(table = AppContract.UserTweetEntry.TABLE_NAME)
    public static class UserTweet {

        @ContentUri(
                path = AppContract.PATH_USERTWEET,
                type = AppContract.UserTweetEntry.CONTENT_TYPE,
                defaultSort = TimelineColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(AppContract.PATH_USERTWEET);
    }

    @TableEndpoint(table = AppContract.UserEntry.TABLE_NAME)
    public static class Users {

        @ContentUri(
                path = AppContract.PATH_USERS,
                type = AppContract.UserEntry.CONTENT_TYPE,
                defaultSort = UserColumns._ID + " DESC")
        public static final Uri CONTENT_URI = buildUri(AppContract.PATH_USERS);

        @InexactContentUri(
                path = AppContract.PATH_USERS + "/#",
                name = "USER_ID",
                type = AppContract.UserEntry.CONTENT_ITEM_TYPE,
                whereColumn = AppContract.UserEntry._ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(AppContract.PATH_USERS, String.valueOf(id));
        }

        @InexactContentUri(
                path = AppContract.PATH_USERS + "/username/*",
                name = "USERNAME",
                limit = "1",
                type = AppContract.UserEntry.CONTENT_ITEM_TYPE,
                whereColumn = AppContract.UserEntry.SCREEN_NAME,
                pathSegment = 2)
        public static Uri withId(String username) {
            return buildUri(AppContract.PATH_USERS, "username", username);
        }


        @NotifyInsert(paths = AppContract.PATH_USERS)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{
                    Users.CONTENT_URI, Tweets.CONTENT_URI
            };
        }
    }
}