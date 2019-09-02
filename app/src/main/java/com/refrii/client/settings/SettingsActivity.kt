package com.refrii.client.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.refrii.client.App
import com.refrii.client.R
import com.refrii.client.webview.WebViewActivity
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
    }
}