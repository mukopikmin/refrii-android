package com.refrii.client

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.refrii.client.webview.WebViewActivity

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

    class SettingsFragment : PreferenceFragmentCompat() {

        private var mPreferences: SharedPreferences? = null
        private val mOnChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
            System.out.println(pref)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            mPreferences = PreferenceManager.getDefaultSharedPreferences(context).apply {
                registerOnSharedPreferenceChangeListener(mOnChangeListener)
            }

            when (rootKey) {
                "privacy_policy" -> onCreatePrivacyPolicyPreference()
            }
        }

        private fun onCreatePrivacyPolicyPreference() {
            val termsOfServicePreference = findPreference<Preference>("privacy_policy")

            termsOfServicePreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(activity, WebViewActivity::class.java)

                intent.putExtra("url", "https://refrii.com/privacy")
                startActivity(intent)

                true
            }
        }

        override fun onDestroy() {
            super.onDestroy()

            mPreferences?.unregisterOnSharedPreferenceChangeListener(mOnChangeListener)
        }
    }
}