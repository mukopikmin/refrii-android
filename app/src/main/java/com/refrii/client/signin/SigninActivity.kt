package com.refrii.client.signin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
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
import com.refrii.client.data.api.models.Credential
import kotterknife.bindView
import java.util.*
import javax.inject.Inject


class SigninActivity : AppCompatActivity(), SigninContract.View {

    private val mSignInButton: SignInButton by bindView(R.id.googleSignInButton)
    private val mProgressBar: ProgressBar by bindView(R.id.progressBar)

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth

    @Inject
    lateinit var mPresenter: SigninPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getComponent().inject(this)

        setContentView(R.layout.activity_signin)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mFirebaseAuth = FirebaseAuth.getInstance()

        mSignInButton.setOnClickListener { googleSignIn() }
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val mailAddress = sharedPreferences.getString("mail", null)

        onLoaded()
        mPresenter.takeView(this)

        // Auto re-authenticate if token expired
//        mPresenter.auth(mailAddress)
    }

    override fun onResume() {
        super.onResume()

        // Set sign in button text to center
        for (i in 0 until mSignInButton.childCount) {
            val v = mSignInButton.getChildAt(i)

            if (v is TextView) {
                v.setPadding(0, 0, 20, 0)
                return
            }
        }
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
                    Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)

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

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun onGoogleSignInSuccess(account: FirebaseUser?) {
        account ?: return

        val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()

        account.getIdToken(true).addOnCompleteListener {
            editor.apply {
                putString("jwt", it.result?.token)
                putString("mail", account.email)
                putString("name", account.displayName)
                putString("avatar", account.photoUrl.toString())
            }
            editor.apply()
            finish()
        }
    }

    companion object {
        private const val TAG = "SigninActivity"
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
    }
}
