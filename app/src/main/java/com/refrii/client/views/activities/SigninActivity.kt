package com.refrii.client.views.activities

import android.annotation.SuppressLint
import android.content.Intent
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
import kotterknife.bindView
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.*

class SigninActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val signInButton: SignInButton by bindView(R.id.googleSignInButton)

    private var googleApiClient: GoogleApiClient? = null
    private var googleSignInAccount: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mailAddress = sharedPreferences.getString("mail", null)
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

        signInButton.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    public override fun onResume() {
        super.onResume()

        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)

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

            if (result.isSuccess) {
                val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()

                googleSignInAccount = result.signInAccount
                getGoogleToken(googleSignInAccount!!.email!!)

                editor.apply {
                    putString("mail", googleSignInAccount!!.email)
                    putString("name", googleSignInAccount!!.displayName)
                    putString("avatar", googleSignInAccount!!.photoUrl.toString())
                }
                editor.apply()
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    @SuppressLint("StaticFieldLeak")
    private fun getGoogleToken(accountName: String) {
        object : AsyncTask<String, Void, String>() {
            override fun doInBackground(vararg accounts: String): String? {
                return try {
                    GoogleAuthUtil.getToken(this@SigninActivity, accounts[0], AUTH_SCOPE)
                } catch (e: IOException) {
                    Log.e(TAG, e.message)

                    null
                } catch (e: UserRecoverableAuthException) {
                    startActivityForResult(e.intent, RC_SIGN_IN)

                    null
                } catch (e: GoogleAuthException) {
                    Log.e(TAG, e.message)

                    null
                }
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
                            putLong("expires_at", t.expiresAt!!.time)
                        }
                        editor.apply()

                        finish()
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        Log.d(TAG, e.message)
                    }

                })
    }

    companion object {
        private val TAG = "SigninActivity"
        private val RC_SIGN_IN = 1
        private val AUTH_SCOPE = "oauth2:profile email "
    }
}
