package com.github.denisura.realquacker.data.network;


import com.github.denisura.realquacker.BuildConfig;
import com.github.denisura.realquacker.data.model.AccountModel;
import com.github.denisura.realquacker.data.model.Tweet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TwitterApi {

    String REST_CONSUMER_KEY = BuildConfig.QUACKER_CONSUMER_KEY;
    String REST_CONSUMER_SECRET = BuildConfig.QUACKER_CONSUMER_SECRET;
    String BASE_URL = "https://api.twitter.com/";
    String REST_CALLBACK_URL = "oauth://cprealquacker";

    @GET("1.1/account/verify_credentials.json")
    Call<AccountModel> getAccountDetails();


    @GET("1.1/statuses/home_timeline.json")
    Call<List<Tweet>> getTimeline(
            @Query("count") int count,
            @Query("max_id") long maxId
    );


    @POST("1.1/statuses/update.json")
    Call<Tweet> postStatus(
            @Query("status") String status
    );

}
