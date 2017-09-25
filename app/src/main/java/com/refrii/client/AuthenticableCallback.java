package com.refrii.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by yusuke on 2017/09/25.
 */

public class AuthenticableCallback<T> implements Callback<T> {

    private Context mContext;

    public AuthenticableCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences("DATA", Context.MODE_PRIVATE).edit();

        if (response.code() == 403) {

        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {

    }
}
