package com.refrii.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    public static <T> T create(Class<T> clazz) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://refrii-api.herokuapp.com/")
//                .baseUrl("http://192.168.10.101:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(clazz);
    }
}
