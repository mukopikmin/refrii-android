package com.refrii.client.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.refrii.client.App
import com.refrii.client.R
import kotterknife.bindView
import javax.inject.Inject

class SignInActivity : AppCompatActivity(), SigninContract.View {

    private val mSignInButton: SignInButton by bindView(R.id.googleSignInButton)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth

    @Inject
    lateinit var mPresenter: SigninPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_signin)

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mSignInButton.setOnClickListener { googleSignIn() }
    }

    override fun onStart() {
        super.onStart()

        onLoaded()
        mPresenter.takeView(this)
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GOOGLE_SIGN_IN_REQUEST_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)

                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    showToast("Login failed with code: " + e.statusCode)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        account ?: return

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mFirebaseAuth.currentUser

                        onGoogleSignInSuccess(user)
                    } else {
                        showToast("Failed to login")
                    }
                }
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

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun onGoogleSignInSuccess(account: FirebaseUser?) {
        account ?: return

        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()

        account.getIdToken(true).addOnCompleteListener {
            editor.apply {
                putString(getString(R.string.preference_key_jwt), it.result?.token)
                putString(getString(R.string.preference_key_mail), account.email)
                putString(getString(R.string.preference_key_name), account.displayName)
                putString(getString(R.string.preference_key_avatar), account.photoUrl.toString())
                putString(getString(R.string.preference_key_signin_provider), it.result?.signInProvider)

                it.result?.expirationTimestamp?.let {
                    putLong(getString(R.string.preference_key_expiration_timestamp), it)
                }
            }
            editor.apply()

            finish()
        }
    }

    companion object {
        @Suppress("unused")
        private const val TAG = "SignInActivity"
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
    }
}