package app.muko.mypantry.fragments.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.muko.mypantry.R
import app.muko.mypantry.data.models.User
import app.muko.mypantry.di.ViewModelFactory
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
import dagger.android.support.DaggerFragment
import java.util.regex.Pattern
import javax.inject.Inject


class SigninFragment : DaggerFragment() {

    @BindView(R.id.googleSigninButton)
    lateinit var signInButton: SignInButton

    @BindView(R.id.googleSignupButton)
    lateinit var signUpButton: SignInButton

    @BindView(R.id.progressBar)
    lateinit var progressBar: ProgressBar

    @BindView(R.id.textViewSignup)
    lateinit var signUpTextView: TextView

    @BindView(R.id.acceptPrivacyPolicyCheckBox)
    lateinit var acceptPrivacyPolicy: CheckBox

    @BindView(R.id.titleTextView)
    lateinit var titleTextView: TextView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var preference: SharedPreferences

    private lateinit var viewModel: SigninViewModel
    private lateinit var drawerLocker: DrawerLocker
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onAttach(context: Context) {
        super.onAttach(context)

        drawerLocker = context as DrawerLocker
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.signin_fragment, container, false)
        val appName = getString(R.string.app_name)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        ButterKnife.bind(this, view)

        googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
        firebaseAuth = FirebaseAuth.getInstance()
        signInButton.setOnClickListener { googleSignIn() }
        signUpButton.setOnClickListener { googleSignUp() }
        acceptPrivacyPolicy.setOnCheckedChangeListener { _, isChecked -> signUpButton.isEnabled = isChecked }
        signUpButton.isEnabled = false
        titleTextView.text = String.format(getString(R.string.message_welcome_title), appName)

        setGoogleSigninButtonText(signInButton, "Google でログイン")
        setGoogleSigninButtonText(signUpButton, "Google でアカウントを作成")
        setLinkedSignupMessage()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, viewModelFactory).get(SigninViewModel::class.java)

        drawerLocker.setDrawerLocked(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                saveAccount(it)
                (activity as SigninCompletable).signinCompleted()
            }
        })
        viewModel.isAuthorizing.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        drawerLocker.setDrawerLocked(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            GOOGLE_SIGN_IN_REQUEST_CODE, GOOGLE_SIGN_UP_REQUEST_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {
                    val account = task.getResult(ApiException::class.java)

                    firebaseAuthWithGoogle(account, requestCode)
                } catch (e: ApiException) {
                    showToast("Login failed with code: " + e.statusCode)
                }
            }
        }
    }

    private fun saveAccount(user: User) {
        preference.edit().apply {
            putInt(getString(R.string.preference_key_id), user.id)
        }.apply()
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun googleSignUp() {
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_SIGN_UP_REQUEST_CODE)
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

        Linkify.addLinks(signUpTextView, pattern, strUrl, null, filter)
        signUpTextView.setLinkTextColor(activity!!.getColor(R.color.colorAccent))
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?, requestCode: Int) {
        account ?: return

        activity?.let {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(it) { task ->
                        if (!task.isSuccessful) {
                            showToast("Failed to login")

                            return@addOnCompleteListener
                        }

                        firebaseAuth.currentUser?.getIdToken(true)?.addOnCompleteListener {
                            when (requestCode) {
                                GOOGLE_SIGN_IN_REQUEST_CODE -> viewModel.verifyAccount()
                                GOOGLE_SIGN_UP_REQUEST_CODE -> viewModel.signup()
                            }
                        }
                    }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 101
        private const val GOOGLE_SIGN_UP_REQUEST_CODE = 102

        fun newInstance() = SigninFragment()
    }
}