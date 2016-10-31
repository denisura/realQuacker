package com.github.denisura.realquacker.data.sync;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class QuackerSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static QuackerSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new QuackerSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}