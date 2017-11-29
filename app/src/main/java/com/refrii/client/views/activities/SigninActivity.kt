package com.refrii.client.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
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
import kotterknife.bindView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

class SigninActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val mSigninButton: SignInButton by bindView(R.id.googleSignInButton)

    private var mGoogleSignInAccount: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mailAddress = sharedPreferences.getString("mail", null)
        val googleSigninOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOptions)
                .build()

        mailAddress?.let { getGoogleToken(it) }

        mSigninButton.setOnClickListener {
            if (googleApiClient.hasConnectedApi(Auth.GOOGLE_SIGN_IN_API)) {
                googleApiClient.clearDefaultAccountAndReconnect();
            }

            val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(intent, GOOGLE_SIGNIN_REQUEST_CODE)
        }

    }

    public override fun onResume() {
        super.onResume()

        for (i in 0 until mSigninButton.childCount) {
            val v = mSigninButton.getChildAt(i)

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

        when (requestCode) {
            GOOGLE_SIGNIN_REQUEST_CODE -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                if (result.isSuccess) {
                    mGoogleSignInAccount = result.signInAccount
                    mGoogleSignInAccount?.let { account ->
                        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()

                        editor.apply {
                            putString("mail", account.email)
                            putString("name", account.displayName)
                            putString("avatar", account.photoUrl.toString())
                        }
                        editor.apply()

                        account.email?.let { getGoogleToken(it) }
                    }
                }
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    @SuppressLint("StaticFieldLeak")
    private fun getGoogleToken(accountName: String) {
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg accounts: String): String? {
                var token: String? = null

                try {
                    token = GoogleAuthUtil.getToken(this@SigninActivity, accounts[0], AUTH_SCOPE)
                } catch (e: IOException) {
                    Log.e(TAG, e.message)
                } catch (e: UserRecoverableAuthException) {
                    startActivityForResult(e.intent, GOOGLE_SIGNIN_REQUEST_CODE)
                } catch (e: GoogleAuthException) {
                    Log.e(TAG, e.message)
                }

                return token
            }

            override fun onPostExecute(googleToken: String?) {
                super.onPostExecute(googleToken)

                googleToken ?: return

                val editor = PreferenceManager.getDefaultSharedPreferences(this@SigninActivity).edit()
                editor.putString("google-token", googleToken)
                editor.apply()

                getJwt(googleToken)
            }
        }.execute(accountName)
    }

    private fun getJwt(googleToken: String) {
        val params = HashMap<String, String>()
        params.put("token", googleToken)

        RetrofitFactory.getClient(AuthService::class.java, this@SigninActivity)
                .getToken(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Subscriber<Credential>() {
                    override fun onNext(t: Credential) {
                        val editor = PreferenceManager.getDefaultSharedPreferences(this@SigninActivity).edit()
                        editor.apply {
                            putString("jwt", t.jwt)
                            t.expiresAt?.let { putLong("expires_at", it.time) }
                        }
                        editor.apply()
                    }

                    override fun onCompleted() {
                        finish()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(this@SigninActivity, e.message, Toast.LENGTH_LONG).show()
                    }

                })
    }

    companion object {
        private val TAG = "SigninActivity"
        private val AUTH_SCOPE = "oauth2:profile email "
        private val GOOGLE_SIGNIN_REQUEST_CODE = 101
    }
}
