package com.github.denisura.realquacker.data.network;


import com.github.denisura.realquacker.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import static com.github.denisura.realquacker.data.network.TwitterApi.BASE_URL;
import static com.github.denisura.realquacker.data.network.TwitterApi.REST_CONSUMER_KEY;
import static com.github.denisura.realquacker.data.network.TwitterApi.REST_CONSUMER_SECRET;

public class TwitterService {


    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

//    private static OkHttpClient.Builder httpClient =
//            new OkHttpClient.Builder()
//                    .readTimeout(60, TimeUnit.SECONDS)
//                    .connectTimeout(60, TimeUnit.SECONDS);


    private TwitterService() {
    }

    public static TwitterApi newService(String token, String tokenSecret) {

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(REST_CONSUMER_KEY, REST_CONSUMER_SECRET);
        consumer.setTokenWithSecret(token, tokenSecret);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer));

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            client.addInterceptor(logging);
        }

        builder.client(client.build());
        Retrofit retrofit = builder.build();
        return retrofit.create(TwitterApi.class);
    }
}
