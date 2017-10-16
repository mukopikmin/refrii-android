package com.refrii.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "SigninActivity";
    private static final String AUTH_SCOPE = "oauth2:profile email ";

    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount googleSignInAccount;
    private SharedPreferences sharedPreferences;
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        signInButton = (SignInButton) findViewById(R.id.googleSignInButton);



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String mailAddress = sharedPreferences.getString("mail", null);
        if (mailAddress != null) {
            getGoogleToken(mailAddress);
        }

        GoogleSignInOptions googleSigninOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOptions)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaa");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setPadding(0, 0, 20, 0);
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if (result.isSuccess()) {
                googleSignInAccount = result.getSignInAccount();
                getGoogleToken(googleSignInAccount.getEmail());

                editor.putString("mail", googleSignInAccount.getEmail());
                editor.putString("name", googleSignInAccount.getDisplayName());
                editor.putString("avatar", googleSignInAccount.getPhotoUrl().toString());
                editor.commit();
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
                    token = GoogleAuthUtil.getToken(SigninActivity.this, accounts[0], scopes);
                    Log.e(TAG, token);
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

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("google-token", googleToken);
                editor.commit();

                AuthService service = RetrofitFactory.getClient(AuthService.class, SigninActivity.this);

                HashMap<String,String> params = new HashMap<>();
                params.put("token", googleToken);

                Call<Credential> call = service.getToken(params);
                call.enqueue(new Callback<Credential>() {
                    @Override
                    public void onResponse(Call<Credential> call, Response<Credential> response) {
                        Credential credential = response.body();
                        Log.e(TAG, response.body().getJwt());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("jwt", credential.getJwt());
                        editor.putLong("expires_at", credential.getExpiresAt().getTime());
                        editor.commit();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Credential> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
            }
        }.execute(accountName);
    }
}
