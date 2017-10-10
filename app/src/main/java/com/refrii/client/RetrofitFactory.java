package com.refrii.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {

    private static final String TAG = "RetrofitFactory";

//    public static <T> T getClient(Class<T> clazz, final Context context) {
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
////                .baseUrl("https://refrii-api.herokuapp.com/")
//                .baseUrl("http://192.168.10.102:3000/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//        return retrofit.create(clazz);
//    }

    public static <T> T getClient(Class<T> clazz, final Context context) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
                        Request original = chain.request();
                        String jwt = sharedPreferences.getString("jwt", null);
                        Request request = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("Authorization", "Bearer " + jwt)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit retrofit = new Retrofit.Builder()
               .baseUrl("https://refrii-api.herokuapp.com/")
         //       .baseUrl("http://192.168.10.102:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();
        return retrofit.create(clazz);
    }
}
