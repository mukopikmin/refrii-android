package com.refrii.client.views.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView

import com.google.android.gms.auth.GoogleAuthException
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.refrii.client.R
import com.refrii.client.factories.RetrofitFactory
import com.refrii.client.models.Credential
import com.refrii.client.services.AuthService

import java.io.IOException
import java.util.HashMap

import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class SigninActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var googleSignInAccount: GoogleSignInAccount? = null
    private var sharedPreferences: SharedPreferences? = null
    private var signInButton: SignInButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        signInButton = findViewById<SignInButton>(R.id.googleSignInButton) as SignInButton



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)//getSharedPreferences("DATA", Context.MODE_PRIVATE);
        val mailAddress = sharedPreferences!!.getString("mail", null)
        if (mailAddress != null) {
            getGoogleToken(mailAddress)
        }

        val googleSigninOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOptions)
                .build()

        signInButton!!.setOnClickListener {
            Log.e(TAG, "aaaaaaaaaaaaaaaaaaaaaaaaaa")
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    public override fun onResume() {
        super.onResume()

        for (i in 0 until signInButton!!.childCount) {
            val v = signInButton!!.getChildAt(i)

            if (v is TextView) {
                v.setPadding(0, 0, 20, 0)
                return
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            val editor = sharedPreferences!!.edit()

            if (result.isSuccess) {
                googleSignInAccount = result.signInAccount
                getGoogleToken(googleSignInAccount!!.email)

                editor.putString("mail", googleSignInAccount!!.email)
                editor.putString("name", googleSignInAccount!!.displayName)
                editor.putString("avatar", googleSignInAccount!!.photoUrl.toString())
                editor.commit()
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    private fun getGoogleToken(accountName: String) {
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg accounts: String): String? {
                val scopes = AUTH_SCOPE
                var token: String? = null
                try {
                    token = GoogleAuthUtil.getToken(this@SigninActivity, accounts[0], scopes)
                    Log.e(TAG, token)
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                } catch (e: UserRecoverableAuthException) {
                    startActivityForResult(e.intent, RC_SIGN_IN)
                } catch (e: GoogleAuthException) {
                    Log.e(TAG, e.message)
                }

                return token
            }

            override fun onPostExecute(googleToken: String) {
                super.onPostExecute(googleToken)

                val editor = sharedPreferences!!.edit()
                editor.putString("google-token", googleToken)
                editor.commit()

                val params = HashMap<String, String>()
                params.put("token", googleToken)

                RetrofitFactory.getClient(AuthService::class.java, this@SigninActivity)
                        .getToken(params)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object: Subscriber<Credential>() {
                            override fun onNext(t: Credential) {
                                val editor = sharedPreferences!!.edit()
                                editor.putString("jwt", t.jwt)
                                editor.putLong("expires_at", t.expiresAt!!.time)
                                editor.commit()
                                finish()
                            }

                            override fun onCompleted() {
                            }

                            override fun onError(e: Throwable) {
                                Log.d(TAG, e.message)
                            }

                        })
            }
        }.execute(accountName)
    }

    companion object {

        private val RC_SIGN_IN = 1
        private val TAG = "SigninActivity"
        private val AUTH_SCOPE = "oauth2:profile email "
    }
}
