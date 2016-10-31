package com.github.denisura.realquacker.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.github.denisura.realquacker.account.AppAccount;

public class AccountUtils {
    public static Account getCurrentAccount(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        final Account availableAccounts[] = accountManager.getAccountsByType(AppAccount.TYPE);

        if (availableAccounts.length != 0) {
            return availableAccounts[0];
        }
        accountManager.addAccount(AppAccount.TYPE, AppAccount.AUTHTOKEN_TYPE_FULL_ACCESS, null, new Bundle(), (Activity) context, null, null);

        return null;
    }
}
