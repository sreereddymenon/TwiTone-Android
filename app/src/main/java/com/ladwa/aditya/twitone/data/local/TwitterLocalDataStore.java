package com.ladwa.aditya.twitone.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.ladwa.aditya.twitone.R;
import com.ladwa.aditya.twitone.data.TwitterDataStore;
import com.ladwa.aditya.twitone.data.local.models.DirectMessage;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOContentResolverDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOContentResolverGetResolver;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOContentResolverPutResolver;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOSQLiteDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOSQLiteGetResolver;
import com.ladwa.aditya.twitone.data.local.models.DirectMessageStorIOSQLitePutResolver;
import com.ladwa.aditya.twitone.data.local.models.Interaction;
import com.ladwa.aditya.twitone.data.local.models.InteractionStorIOContentResolverDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.InteractionStorIOContentResolverGetResolver;
import com.ladwa.aditya.twitone.data.local.models.InteractionStorIOContentResolverPutResolver;
import com.ladwa.aditya.twitone.data.local.models.Trend;
import com.ladwa.aditya.twitone.data.local.models.TrendStorIOContentResolverDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.TrendStorIOContentResolverGetResolver;
import com.ladwa.aditya.twitone.data.local.models.TrendStorIOContentResolverPutResolver;
import com.ladwa.aditya.twitone.data.local.models.Tweet;
import com.ladwa.aditya.twitone.data.local.models.TweetStorIOContentResolverDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.TweetStorIOContentResolverGetResolver;
import com.ladwa.aditya.twitone.data.local.models.TweetStorIOContentResolverPutResolver;
import com.ladwa.aditya.twitone.data.local.models.User;
import com.ladwa.aditya.twitone.data.local.models.UserStorIOContentResolverDeleteResolver;
import com.ladwa.aditya.twitone.data.local.models.UserStorIOContentResolverGetResolver;
import com.ladwa.aditya.twitone.data.local.models.UserStorIOContentResolverPutResolver;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import java.util.List;

import rx.Observable;

/**
 * A class that implements TwitterDataStore for Local Database
 * Created by Aditya on 04-Jul-16.
 */
public class TwitterLocalDataStore implements TwitterDataStore {
    private static TwitterLocalDataStore INSTANCE;
    private static StorIOContentResolver mStorIOContentResolver;
    private static StorIOSQLite mStorIOSQLite;
    private static SharedPreferences preferences;
    private static Context mContext;

    private TwitterLocalDataStore(@NonNull Context context) {
        mContext = context;
        TwitterDbHelper mTwitterDbHelper = new TwitterDbHelper(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        mStorIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(mTwitterDbHelper)
                .addTypeMapping(DirectMessage.class, SQLiteTypeMapping.<DirectMessage>builder()
                        .putResolver(new DirectMessageStorIOSQLitePutResolver())
                        .getResolver(new DirectMessageStorIOSQLiteGetResolver())
                        .deleteResolver(new DirectMessageStorIOSQLiteDeleteResolver())
                        .build())
                .build();

        mStorIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(context.getContentResolver())
                .addTypeMapping(User.class, ContentResolverTypeMapping.<User>builder()
                        .putResolver(new UserStorIOContentResolverPutResolver())
                        .getResolver(new UserStorIOContentResolverGetResolver())
                        .deleteResolver(new UserStorIOContentResolverDeleteResolver())
                        .build())
                .addTypeMapping(Tweet.class, ContentResolverTypeMapping.<Tweet>builder()
                        .putResolver(new TweetStorIOContentResolverPutResolver())
                        .getResolver(new TweetStorIOContentResolverGetResolver())
                        .deleteResolver(new TweetStorIOContentResolverDeleteResolver())
                        .build())
                .addTypeMapping(Interaction.class, ContentResolverTypeMapping.<Interaction>builder()
                        .putResolver(new InteractionStorIOContentResolverPutResolver())
                        .getResolver(new InteractionStorIOContentResolverGetResolver())
                        .deleteResolver(new InteractionStorIOContentResolverDeleteResolver())
                        .build())
                .addTypeMapping(DirectMessage.class, ContentResolverTypeMapping.<DirectMessage>builder()
                        .putResolver(new DirectMessageStorIOContentResolverPutResolver())
                        .getResolver(new DirectMessageStorIOContentResolverGetResolver())
                        .deleteResolver(new DirectMessageStorIOContentResolverDeleteResolver())
                        .build())
                .addTypeMapping(Trend.class, ContentResolverTypeMapping.<Trend>builder()
                        .putResolver(new TrendStorIOContentResolverPutResolver())
                        .getResolver(new TrendStorIOContentResolverGetResolver())
                        .deleteResolver(new TrendStorIOContentResolverDeleteResolver())
                        .build())
                .build();
    }

    public static TwitterLocalDataStore getInstance(@NonNull Context context) {
        if (INSTANCE == null)
            INSTANCE = new TwitterLocalDataStore(context);
        return INSTANCE;
    }

    @Override
    public Observable<User> getUserInfo(long userID) {
        return mStorIOContentResolver.get()
                .object(User.class)
                .withQuery(Query.builder()
                        .uri(TwitterContract.User.CONTENT_URI + "/" + userID)
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<List<Tweet>> getTimeLine(long sinceId) {
        return mStorIOContentResolver.get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder().uri(TwitterContract.Tweet.CONTENT_URI)
                        .sortOrder(TwitterContract.Tweet.COLUMN_ID + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<List<Interaction>> getInteraction(long sinceId) {
        return mStorIOContentResolver.get()
                .listOfObjects(Interaction.class)
                .withQuery(Query.builder().uri(TwitterContract.Interaction.CONTENT_URI)
                        .sortOrder(TwitterContract.Interaction.COLUMN_ID + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<List<DirectMessage>> getDirectMessage(long sinceId) {
        long userId = preferences.getLong(mContext.getString(R.string.pref_userid), 0);
        return mStorIOContentResolver.get()
                .listOfObjects(DirectMessage.class)
                .withQuery(Query.builder().uri(TwitterContract.DirectMessage.CONTENT_URI)
                        .where(TwitterContract.DirectMessage.COLUMN_SENDER_ID + " != ? GROUP BY " + TwitterContract.DirectMessage.COLUMN_RECIPIENT_ID + " , " + TwitterContract.DirectMessage.COLUMN_SENDER_ID)
                        .whereArgs(userId)
                        .sortOrder(TwitterContract.DirectMessage.COLUMN_ID + " DESC")
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<List<Trend>> getTrends() {
        return mStorIOContentResolver.get()
                .listOfObjects(Trend.class)
                .withQuery(Query.builder().uri(TwitterContract.Trends.CONTENT_URI)
                        .build())
                .prepare()
                .asRxObservable();
    }

    @Override
    public Observable<List<Trend>> getLocalTrends(double latitude, double longitude) {
        return null;
    }

    public static Observable<List<DirectMessage>> getDirectMessageOfUser(long id) {
        return mStorIOContentResolver.get()
                .listOfObjects(DirectMessage.class)
                .withQuery(Query.builder().uri(TwitterContract.DirectMessage.CONTENT_URI)
                        .where(TwitterContract.DirectMessage.COLUMN_SENDER_ID + " = ?  OR " + TwitterContract.DirectMessage.COLUMN_RECIPIENT_ID + " = ?")
                        .whereArgs(id, id)
                        .sortOrder(TwitterContract.DirectMessage.COLUMN_ID + " DESC ")
                        .build())
                .prepare()
                .asRxObservable();

    }

    public static long getLastDirectMessageId() {
        Interaction interaction = mStorIOContentResolver.get().object(Interaction.class)
                .withQuery(Query.builder().uri(TwitterContract.Interaction.CONTENT_URI)
                        .sortOrder(TwitterContract.Interaction.COLUMN_ID + " DESC LIMIT 1")
                        .build())
                .prepare()
                .executeAsBlocking();

        if (interaction == null)
            return 1;
        else
            return interaction.getId();
    }

    public static long getLastTweetId() {
        Tweet tweet = mStorIOContentResolver.get().object(Tweet.class)
                .withQuery(Query.builder().uri(TwitterContract.Tweet.CONTENT_URI)
                        .sortOrder(TwitterContract.Tweet.COLUMN_ID + " DESC LIMIT 1")
                        .build())
                .prepare()
                .executeAsBlocking();

        if (tweet == null)
            return 1;
        else
            return tweet.getId();
    }

    public static long getLastInteractionId() {
        Interaction interaction = mStorIOContentResolver.get().object(Interaction.class)
                .withQuery(Query.builder().uri(TwitterContract.Interaction.CONTENT_URI)
                        .sortOrder(TwitterContract.Interaction.COLUMN_ID + " DESC LIMIT 1")
                        .build())
                .prepare()
                .executeAsBlocking();
        if (interaction == null)
            return 1;
        else
            return interaction.getId();
    }

    public static void createFavourite(Tweet tweet) {
        mStorIOContentResolver.put().object(tweet).prepare().executeAsBlocking();

    }

    public static void createRetweet(Tweet tweet) {
        mStorIOContentResolver.put().object(tweet).prepare().executeAsBlocking();
    }

    public static void destoryFavourite(Tweet tweet) {
        mStorIOContentResolver.put().object(tweet).prepare().executeAsBlocking();
    }

    public static void createFavouriteInteraction(Interaction interaction) {
        mStorIOContentResolver.put().object(interaction).prepare().executeAsBlocking();

    }

    public static void createRetweetInteraction(Interaction interaction) {
        mStorIOContentResolver.put().object(interaction).prepare().executeAsBlocking();
    }

    public static void destoryFavouriteInteraction(Interaction interaction) {
        mStorIOContentResolver.put().object(interaction).prepare().executeAsBlocking();
    }

    public static void saveUserInfo(User user) {
        mStorIOContentResolver.put().object(user).prepare().executeAsBlocking();
    }

    public static void saveTimeLine(List<Tweet> tweetList) {
        mStorIOContentResolver.put().objects(tweetList).prepare().executeAsBlocking();
    }

    public static void saveInteraction(List<Interaction> interactionList) {
        mStorIOContentResolver.put().objects(interactionList).prepare().executeAsBlocking();
    }

    public static void saveDirectMessage(List<DirectMessage> directMessageList) {
        mStorIOContentResolver.put().objects(directMessageList).prepare().executeAsBlocking();
    }

    public static void saveSingleDirectMessage(DirectMessage directMessage) {
        mStorIOContentResolver.put().object(directMessage).prepare().executeAsBlocking();
    }

    public static void deleteTrends() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.Trends.CONTENT_URI)
                .where(TwitterContract.Trends.COLUMN_LOCAL + " = ? ")
                .whereArgs(0)
                .build()).prepare().executeAsBlocking();
    }

    public static void saveTrend(List<Trend> trendList) {
        mStorIOContentResolver.put().objects(trendList).prepare().executeAsBlocking();
    }

    public static void deleteLocalTrends() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.Trends.CONTENT_URI)
                .where(TwitterContract.Trends.COLUMN_LOCAL + " = ? ")
                .whereArgs(1)
                .build()).prepare().executeAsBlocking();
    }

    public static void deleteTimeLine() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.Tweet.CONTENT_URI)
                .build()).prepare().executeAsBlocking();
    }

    public static void deleteMessage() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.DirectMessage.CONTENT_URI)
                .build()).prepare().executeAsBlocking();
    }

    public static void deleteInteraction() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.Interaction.CONTENT_URI)
                .build()).prepare().executeAsBlocking();
    }

    public static void deleteUser() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.User.CONTENT_URI)
                .build()).prepare().executeAsBlocking();
    }

    public static void deleteAllTrends() {
        mStorIOContentResolver.delete().byQuery(DeleteQuery.builder().uri(TwitterContract.Trends.CONTENT_URI)
                .build()).prepare().executeAsBlocking();
    }

    public static void saveLocalTrend(List<Trend> trendList) {
        mStorIOContentResolver.put().objects(trendList).prepare().executeAsBlocking();
    }

}
