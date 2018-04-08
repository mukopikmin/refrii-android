package com.refrii.client.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.api.models.Credential
import kotterknife.bindView
import java.util.*
import javax.inject.Inject

class SigninActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, SigninContract.View {

    private val mSigninButton: SignInButton by bindView(R.id.googleSignInButton)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    @Inject
    lateinit var mPresenter: SigninPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_signin)

        mSigninButton.setOnClickListener { googleSignin() }
    }

    private fun googleSignin() {
        val googleSigninOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSigninOptions)
                .build()

        if (googleApiClient.hasConnectedApi(Auth.GOOGLE_SIGN_IN_API)) {
            googleApiClient.clearDefaultAccountAndReconnect()
        }

        val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(intent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mailAddress = sharedPreferences.getString("mail", null)

        onLoaded()
        mPresenter.takeView(this)

        // Auto re-authenticate if token expired
        mPresenter.auth(mailAddress)
    }

    public override fun onResume() {
        super.onResume()

        // Set sign in button text to center
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
            GOOGLE_SIGN_IN_REQUEST_CODE -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                if (result.isSuccess) {
                    onGoogleSigninSuccess(result.signInAccount)
                }
            }
        }
    }

    private fun onGoogleSigninSuccess(signInAccount: GoogleSignInAccount?) {
        signInAccount ?: return

        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()

        editor.apply {
            putString("mail", signInAccount.email)
            putString("name", signInAccount.displayName)
            putString("avatar", signInAccount.photoUrl.toString())
        }
        editor.apply()

        mPresenter.auth(signInAccount.email)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    override fun onAuthCompleted(credential: Credential?) {
        credential ?: return

        val editor = PreferenceManager.getDefaultSharedPreferences(this@SigninActivity).edit()

        editor.apply {
            putString("jwt", credential.jwt)
            putLong("expires_at", credential.expiresAt?.time ?: Date().time)
            putInt("id", credential.user?.id ?: 0)
            putString("provider", credential.user?.provider)
        }.apply()

        finish()
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
    }

    override fun onLoaded() {
        mProgressBar.visibility = View.GONE
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
    }
}
