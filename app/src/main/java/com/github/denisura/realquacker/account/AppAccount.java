package com.github.denisura.realquacker.account;

import android.accounts.Account;
import android.os.Parcel;

public class AppAccount extends Account {

    public static final String TYPE = AppAccount.class.getPackage().getName();
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";

    public AppAccount(Parcel in) {
        super(in);
    }

    public AppAccount(String name) {
        super(name, TYPE);
    }

}