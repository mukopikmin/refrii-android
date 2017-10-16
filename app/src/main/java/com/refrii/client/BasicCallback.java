package com.refrii.client;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yusuke on 2017/09/28.
 */

public class BasicCallback<T> implements Callback<T> {

    private static final String TAG = "BasicCallback";

    private Context mContext;

    public BasicCallback(Context context) {
        mContext = context;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        Intent intent;

        Log.e(TAG, "" + response.code());

        switch (response.code()) {
            case 401:
            case 403:
            case 404:
                intent = new Intent(mContext, SigninActivity.class);
                mContext.startActivity(intent);
                break;
            case 500:
                intent = new Intent(mContext, ErrorActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.e(TAG, t.getMessage());
    }
}
