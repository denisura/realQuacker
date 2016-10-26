package com.github.denisura.realquacker.account;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class AppAuthenticatorService extends Service {

    private AppAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new AppAuthenticator(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}