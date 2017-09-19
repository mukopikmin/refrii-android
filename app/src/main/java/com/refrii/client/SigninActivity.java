package com.refrii.client;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.*;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.provider.SyncStateContract.Columns.ACCOUNT_TYPE;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "SigninActivity";
    private static final String AUTH_SCOPE = "oauth2:profile email";

    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount googleSignInAccount;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        GoogleSignInOptions googleSigninOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
//                .addScope(mScope)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOptions)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                Log.d(TAG, "success");
                googleSignInAccount = result.getSignInAccount();
                getGoogleToken(googleSignInAccount.getEmail());
                finish();
            } else {
                Log.d(TAG, "fail");
                Log.d(TAG, googleSignInAccount.getEmail());
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getGoogleToken(String accountName) {
        new AsyncTask<String, Void, String> () {
            @Override
            protected String doInBackground(String... accounts) {
                String scopes = AUTH_SCOPE;
                String token = null;
                try {
                    token = GoogleAuthUtil.getToken(getApplicationContext(), accounts[0], scopes);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), RC_SIGN_IN);
                } catch (GoogleAuthException e) {
                    Log.e(TAG, e.getMessage());
                }
                return token;
            }

            @Override
            protected void onPostExecute(String googleToken) {
                super.onPostExecute(googleToken);
                Log.d(TAG, "Token: " + googleToken);
                sharedPreferences.edit().putString("google-token", googleToken);

                AuthService service = RetrofitFactory.create(AuthService.class);

                HashMap<String,String> params = new HashMap<>();
                params.put("token", googleToken);

                Call<Credential> call = service.getToken(params);
                call.enqueue(new Callback<Credential>() {
                    @Override
                    public void onResponse(Call<Credential> call, Response<Credential> response) {
                        Log.d(TAG, "" + response.body().getJwt());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("jwt", response.body().getJwt());
                        editor.commit();
                        Log.d(TAG, "" + sharedPreferences.getString("jwt", "!!!"));

                    }

                    @Override
                    public void onFailure(Call<Credential> call, Throwable t) {
                        Log.d("debug2", t.getMessage());
                    }
                });
            }
        }.execute(accountName);
    }
}
