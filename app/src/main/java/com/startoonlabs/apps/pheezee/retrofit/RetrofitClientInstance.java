package com.startoonlabs.apps.pheezee.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit instance
 */
public class RetrofitClientInstance {

    private static Retrofit retrofit;
private static final String BASE_URL = "http://13.127.78.38:3000";
//    private static final String BASE_URL = "http://192.168.1.106:3000";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
