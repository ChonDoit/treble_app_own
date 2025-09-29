package me.phh.treble.app

import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationContext.startServiceAsUser(
            Intent(applicationContext, EntryService::class.java), UserHandle.SYSTEM
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : BasePreferenceFragment() {
        override fun loadPreferences(rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_headers, rootKey)

            // Define a map of preference keys to their corresponding fragment classes
            val setFragments = mapOf(
                "mydevice_settings" to "me.phh.treble.app.MyDeviceSettingsFragment",
                "oneplus_settings" to "me.phh.treble.app.OnePlusSettingsFragment",
                "nubia_settings" to "me.phh.treble.app.NubiaSettingsFragment",
                "vsmart_settings" to "me.phh.treble.app.VsmartSettingsFragment",
                "qualcomm_settings" to "me.phh.treble.app.QualcommSettingsFragment",
                "huawei_settings" to "me.phh.treble.app.HuaweiSettingsFragment",
                "samsung_settings" to "me.phh.treble.app.SamsungSettingsFragment",
                "transsion_settings" to "me.phh.treble.app.TranssionSettingsFragment",
                "lenovo_settings" to "me.phh.treble.app.LenovoSettingsFragment",
                "xiaomi_settings" to "me.phh.treble.app.XiaomiSettingsFragment",
                "oppo_settings" to "me.phh.treble.app.OppoSettingsFragment",
                "asus_settings" to "me.phh.treble.app.AsusSettingsFragment",
                "doze_settings" to "me.phh.treble.app.DozeSettingsFragment",
                "mediatek_settings" to "me.phh.treble.app.MediatekSettingsFragment",
                "display_settings" to "me.phh.treble.app.DisplaySettingsFragment",
                "audio_settings" to "me.phh.treble.app.AudioSettingsFragment",
                "audiofx_settings" to "me.phh.treble.app.AudioEffectsFragment",
				"bluetooth_settings" to "me.phh.treble.app.BluetoothSettingsFragment",
                "telephony_settings" to "me.phh.treble.app.TelephonySettingsFragment",
                "ims_settings" to "me.phh.treble.app.ImsSettingsFragment",
                "camera_settings" to "me.phh.treble.app.CameraSettingsFragment",
                "misc_settings" to "me.phh.treble.app.MiscSettingsFragment",
                "spoof_settings" to "me.phh.treble.app.SpoofSettingsFragment",
                "spoof_settings_gms" to "me.phh.treble.app.SpoofSettingsGmsFragment",
                "spoof_settings_ps" to "me.phh.treble.app.SpoofSettingsPsFragment",
                "ui_settings" to "me.phh.treble.app.UiSettingsFragment",
                "debug_settings" to "me.phh.treble.app.DebugSettingsFragment",
                "backlight_settings" to "me.phh.treble.app.BacklightSettingsFragment",
            )

            for ((preferenceKey, fragmentClassName) in setFragments) {
                findPreference<Preference>(preferenceKey)?.setOnPreferenceClickListener {
                    (activity as AppCompatActivity).supportActionBar?.title = it.title
                    val fragment = requireActivity().supportFragmentManager.fragmentFactory.instantiate(
                        requireActivity().classLoader,
                        fragmentClassName
                    )
                    fragment.arguments = it.extras

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
            }
        }

        override fun onResume() {
            super.onResume()
            (activity as AppCompatActivity).supportActionBar?.title = "Treble Settings"
        }
    }

    companion object {
        fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, newValue ->
                val stringValue = newValue.toString()
                val defaultSummary = pref.summary

                when (pref) {
                    is ListPreference -> {
                        val index = pref.findIndexOfValue(stringValue)
                        pref.summary = if (index >= 0) pref.entries[index] else defaultSummary
                    }
                    is EditTextPreference -> {
                        pref.summary = if (stringValue.isNotEmpty()) stringValue else defaultSummary
                    }
                    else -> {
                        // For other preference types (like SwitchPreference), don't change the summary
                        return@OnPreferenceChangeListener true
                    }
                }
                true
            }

            val preferenceManager = PreferenceManager.getDefaultSharedPreferences(preference.context)

            // Handle different preference types when setting initial value
            when (preference) {
                is ListPreference, is EditTextPreference -> {
                    // Only get string for preferences that actually store strings
                    val value = preferenceManager.getString(preference.key, "")
                    preference.onPreferenceChangeListener?.onPreferenceChange(preference, value)
                }
                else -> {
                    // Do nothing for other preference types
                }
            }
        }

        fun bindPreferenceSummariesFromStateMap(fragment: PreferenceFragmentCompat, stateMap: Map<String, String>) {
            stateMap.forEach { (preferenceKey, _) ->
                val preference = fragment.findPreference<Preference>(preferenceKey)
                preference?.let {
                    // Only bind summaries to EditText and List preferences
                    if (it is ListPreference || it is EditTextPreference) {
                        bindPreferenceSummaryToValue(it)
                    }
                }
            }
        }
    }
}