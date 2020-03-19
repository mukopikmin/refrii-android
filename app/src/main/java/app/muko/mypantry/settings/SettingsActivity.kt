package app.muko.mypantry.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import app.muko.mypantry.App
import app.muko.mypantry.R
import app.muko.mypantry.signin.SignInActivity
import app.muko.mypantry.webview.WebViewActivity
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_activity)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        var result = true

        when (id) {
            android.R.id.home -> finish()
            else -> result = super.onOptionsItemSelected(item)
        }

        return result
    }

    class SettingsFragment : PreferenceFragmentCompat(), SettingsContract.View {

        @Inject
        lateinit var mPresenter: SettingsPresenter

        private var mPreferences: SharedPreferences? = null
        private val mOnChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            when (key) {
                getString(R.string.preference_key_name) -> updateUser(pref.getString(key, null))
            }
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            (activity?.application as App).getComponent().inject(this)

            setPreferenceLinkListener("privacy_policy", "https://refrii.com/privacy")
            setPreferenceLinkListener("oss_license", "file:///android_asset/licenses.html")
            setVersionPreference()
            setSignoutPreference()
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            mPreferences = PreferenceManager.getDefaultSharedPreferences(context).apply {
                registerOnSharedPreferenceChangeListener(mOnChangeListener)
            }
        }

        override fun onDestroy() {
            super.onDestroy()

            mPreferences?.unregisterOnSharedPreferenceChangeListener(mOnChangeListener)
        }

        private fun updateUser(name: String?) {
            val id = mPreferences?.getInt(getString(R.string.preference_key_id), -1)

            if (id == null || id == -1) {
                return
            }

            mPresenter.updateUser(id, name)
        }

        private fun setPreferenceLinkListener(key: String, url: String) {
            val pref = findPreference<Preference>(key)

            pref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(activity, WebViewActivity::class.java)

                intent.putExtra("url", url)
                startActivity(intent)

                true
            }
        }

        private fun setVersionPreference() {
            val pref = findPreference<Preference>("version")
            val packageInfo = activity?.packageManager?.getPackageInfo(activity?.application?.packageName, 0)
            val version = packageInfo?.versionName

            pref?.summary = version
        }

        private fun setSignoutPreference() {
            val pref = findPreference<Preference>("signout")

            pref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(activity, SignInActivity::class.java)

                startActivity(intent)

                true
            }
        }
    }
}