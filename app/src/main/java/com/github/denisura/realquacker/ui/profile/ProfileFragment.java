package com.github.denisura.realquacker.ui.profile;


import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.denisura.realquacker.R;
import com.github.denisura.realquacker.account.AppAccount;
import com.github.denisura.realquacker.data.database.AppContract;
import com.github.denisura.realquacker.data.database.AppProvider;
import com.github.denisura.realquacker.data.model.User;
import com.github.denisura.realquacker.data.sync.QuackerSyncAdapter;
import com.github.denisura.realquacker.ui.EndlessRecyclerViewScrollListener;
import com.github.denisura.realquacker.ui.timeline.TimelineAdapter;
import com.github.denisura.realquacker.utils.AccountUtils;

import timber.log.Timber;

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int TIMELINE_LOADER = 0;
    public static final int PROFILE_LOADER = 1;

    private EndlessRecyclerViewScrollListener scrollListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public RecyclerView mRecyclerView;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public TimelineAdapter mAdapter;
    private Callbacks mCallbacks;
    public String mUsername;

    public interface Callbacks {
        void onProfileLoaded(User user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        Timber.d("onCreateView %s", AppProvider.Tweets.CONTENT_URI);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        LinearLayoutManager recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        mAdapter = new TimelineAdapter(getContext(), null);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                Account account = AccountUtils.getCurrentAccount(getContext());
                if (account != null) {
                    QuackerSyncAdapter.syncUserTimelineImmediately(account, mUsername);
                }
            }
        });


        scrollListener = new EndlessRecyclerViewScrollListener(recyclerViewLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Timber.d("Last tweet id %d", mAdapter.getItemId(totalItemsCount - 1));
                long maxId = mAdapter.getItemId(totalItemsCount - 1);
                Account account = AccountUtils.getCurrentAccount(getContext());
                if (account != null) {
                    QuackerSyncAdapter.syncUserTimelineImmediately(account, mUsername, maxId);

                }
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated");

        getLoaderManager().initLoader(TIMELINE_LOADER, null, this);
        getLoaderManager().initLoader(PROFILE_LOADER, null, this);
    }

    /**
     * Handle to a SyncObserver. The ProgressBar element is visible until the SyncObserver reports
     * that the sync is complete.
     * <p>
     * <p>This allows us to delete our SyncObserver once the application is no longer in the
     * foreground.
     */
    private Object mSyncObserverHandle;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Timber.d("onCreateLoader %s", AppProvider.Tweets.CONTENT_URI);

        switch (id) {
            case PROFILE_LOADER:
                return new CursorLoader(getActivity(),
                        AppProvider.Users.withId(mUsername),
                        null,
                        null,
                        null,
                        null);

            case TIMELINE_LOADER:
                return new CursorLoader(getActivity(),
                        AppProvider.Timeline.withId(mUsername),
                        AppProvider.Timeline.PROJECTION,
                        null,
                        null,
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        switch (loader.getId()) {
            case PROFILE_LOADER:
                Timber.d("onLoadFinished profile %d", data.getCount());
                if (data.getCount() > 0) {
                    data.moveToFirst();
                    User profile = User.fromCursor(data);
                    mCallbacks.onProfileLoaded(profile);
                }
                break;
            case TIMELINE_LOADER:
                Timber.d("onLoadFinished %d", data.getCount());
                if (data.getCount() == 0) {
                    Account account = AccountUtils.getCurrentAccount(getContext());
                    if (account != null) {
                        QuackerSyncAdapter.syncUserTimelineImmediately(account, mUsername);
                    }
                }
                mAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        Timber.d("onLoaderReset");

        switch (loader.getId()) {
            case PROFILE_LOADER:
                Timber.d("onLoaderReset profile");
                break;
            case TIMELINE_LOADER:
                if (mAdapter != null) {
                    mAdapter.changeCursor(null);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setRefreshActionButtonState(boolean refreshing) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(refreshing);
    }

    /**
     * Crfate a new anonymous SyncStatusObserver. It's attached to the app's ContentResolver in
     * onResume(), and removed in onPause(). If status changes, it sets the state of the Refresh
     * button. If a sync is active or pending, the Refresh button is replaced by an indeterminate
     * ProgressBar; otherwise, the button itself is displayed.
     */
    private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
        /** Callback invoked with the sync adapter status changes. */
        @Override
        public void onStatusChanged(int which) {


            getActivity().runOnUiThread(new Runnable() {
                /**
                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
                 * runs on the UI thread.
                 */
                @Override
                public void run() {
                    Account account = new AppAccount("denisura");

                    // Test the ContentResolver to see if the sync adapter is active or pending.
                    // Set the state of the refresh button accordingly.
                    boolean syncActive = ContentResolver.isSyncActive(
                            account, AppContract.CONTENT_AUTHORITY);
                    boolean syncPending = ContentResolver.isSyncPending(
                            account, AppContract.CONTENT_AUTHORITY);
                    setRefreshActionButtonState(syncActive || syncPending);
                }
            });
        }
    };

    @Override
    public void onDestroyView() {
        mAdapter = null;
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}
