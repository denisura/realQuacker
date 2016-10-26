package com.github.denisura.realquacker.data.network;


import com.github.denisura.realquacker.BuildConfig;
import com.github.denisura.realquacker.data.model.Account;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TwitterApi {

    String REST_CONSUMER_KEY = BuildConfig.QUACKER_CONSUMER_KEY;
    String REST_CONSUMER_SECRET = BuildConfig.QUACKER_CONSUMER_SECRET;
    String BASE_URL = "https://api.twitter.com/";
    String REST_CALLBACK_URL = "oauth://cprealquacker";

    @GET("1.1/account/verify_credentials.json")
    Call<Account> getAccountDetails();


}
