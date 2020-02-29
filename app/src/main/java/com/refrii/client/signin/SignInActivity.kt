package com.refrii.client.signin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.util.Linkify
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.data.models.User
import com.refrii.client.foodlist.FoodListActivity
import java.util.regex.Pattern
import javax.inject.Inject


class SignInActivity : AppCompatActivity(), SigninContract.View {

    @BindView(R.id.googleSigninButton)
    lateinit var mSignInButton: SignInButton
    @BindView(R.id.googleSignupButton)
    lateinit var mSignUpButton: SignInButton
    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar
    @BindView(R.id.textViewSignup)
    lateinit var mSignupTextView: TextView
    @BindView(R.id.acceptPrivacyPolicyCheckBox)
    lateinit var mAcceptPrivacyPolicy: CheckBox
    @BindView(R.id.titleTextView)
    lateinit var mTitleTextView: TextView

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mPreference: SharedPreferences

    @Inject
    lateinit var mPresenter: SigninPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = getString(R.string.app_name)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        (application as App).getComponent().inject(this)
        setContentView(R.layout.activity_signin)
        ButterKnife.bind(this)

        mPreference = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mFirebaseAuth = FirebaseAuth.getInstance()
        mSignInButton.setOnClickListener { googleSignIn() }
        mSignUpButton.setOnClickListener { googleSignUp() }
        mAcceptPrivacyPolicy.setOnCheckedChangeListener { _, isChecked -> mSignUpButton.isEnabled = isChecked }
        mSignUpButton.isEnabled = false
        mTitleTextView.text = String.format(getString(R.string.message_welcome_title), appName)

        setGoogleSigninButtonText(mSignInButton, "Google でログイン")
        setGoogleSigninButtonText(mSignUpButton, "Google でアカウントを作成")
        setLinkedSignupMessage()
    }

    private fun setGoogleSigninButtonText(signInButton: SignInButton, buttonText: String) {
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)

            if (v is TextView) {
                val tv = v as TextView
                tv.text = buttonText
                return
            }
        }
    }

    private fun setLinkedSignupMessage() {
        val pattern = Pattern.compile("プライバシーポリシー")
        val strUrl = getString(R.string.privacy_policy_url)
        val filter = Linkify.TransformFilter { _, _ -> strUrl }

        Linkify.addLinks(mSignupTextView, pattern, strUrl, null, filter)
        mSignupTextView.setLinkTextColor(getColor(R.color.colorAccent))
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
            GOOGLE_SIGN_IN_REQUEST_CODE, GOOGLE_SIGN_UP_REQUEST_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)

                    onLoading()
                    firebaseAuthWithGoogle(account, requestCode)
                } catch (e: ApiException) {
                    showToast("Login failed with code: " + e.statusCode)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?, requestCode: Int) {
        account ?: return

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val editor = mPreference.edit()

                        mFirebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener {
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

                            when (requestCode) {
                                GOOGLE_SIGN_IN_REQUEST_CODE -> mPresenter.verifyAccount()
                                GOOGLE_SIGN_UP_REQUEST_CODE -> mPresenter.signup()
                            }
                        }
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

    override fun saveAccount(user: User?) {
        user ?: return

        val editor = mPreference.edit()

        editor.apply {
            putInt(getString(R.string.preference_key_id), user.id)
        }
        editor.apply()
    }

    override fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onLoginCompleted() {
        val intent = Intent(this, FoodListActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun googleSignUp() {
        val signInIntent = mGoogleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_UP_REQUEST_CODE)
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
        private const val GOOGLE_SIGN_UP_REQUEST_CODE = 102
    }
}
