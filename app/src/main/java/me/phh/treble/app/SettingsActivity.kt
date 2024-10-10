package me.phh.treble.app

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.os.UserHandle
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applicationContext.startServiceAsUser(
            Intent(applicationContext, EntryService::class.java), UserHandle.SYSTEM
        )

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
        }

        actionBar?.setDisplayHomeAsUpEnabled(true)
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

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_headers)

            // Check enabled status for each preference and remove if not enabled
            val context = activity ?: return
            val checkEnabled = EntryService.getEnabledPreferences(context)
            checkEnabled.forEach { (key, isEnabled) ->
                if (!isEnabled) {
                    val preference = findPreference(key)
                    preference?.let {
                        val parent = preference.parent
                        parent?.removePreference(preference)
                    }
                }
            }

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
                "telephony_settings" to "me.phh.treble.app.TelephonySettingsFragment",
                "ims_settings" to "me.phh.treble.app.ImsSettingsFragment",
                "camera_settings" to "me.phh.treble.app.CameraSettingsFragment",
                "misc_settings" to "me.phh.treble.app.MiscSettingsFragment",
                "spoof_settings" to "me.phh.treble.app.SpoofSettingsFragment",
                "ui_settings" to "me.phh.treble.app.UiSettingsFragment",
                "debug_settings" to "me.phh.treble.app.DebugSettingsFragment",
            )

            // Setup click listeners for each fragment using the map
            for ((preferenceKey, fragmentClassName) in setFragments) {
                findPreference(preferenceKey)?.setOnPreferenceClickListener {
                    activity?.actionBar?.title = it.title
                    val fragment = Fragment.instantiate(activity, fragmentClassName)
                    fragment.arguments = it.extras

                    fragmentManager.beginTransaction()
                        .replace(android.R.id.content, fragment)
                        .addToBackStack(null)
                        .commit()
                    true
                }
            }
        }

        override fun onResume() {
            super.onResume()
            activity?.actionBar?.title = "Treble Settings"
        }
    }

    companion object {
        fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, newValue ->
                val stringValue = newValue.toString()
                val defaultSummary = pref.summary

                pref.summary = when (pref) {
                    is ListPreference -> {
                        val index = pref.findIndexOfValue(stringValue)
                        if (index >= 0) pref.entries[index] else defaultSummary
                    }
                    is EditTextPreference -> {
                        if (stringValue.isNotEmpty()) stringValue.toIntOrNull()?.toString() ?: stringValue else defaultSummary
                    }
                    else -> {
                        if (stringValue.isNotEmpty()) stringValue else defaultSummary
                    }
                }
                true
            }

            val preferenceManager = PreferenceManager.getDefaultSharedPreferences(preference.context)
            preference.onPreferenceChangeListener.onPreferenceChange(
                preference,
                preferenceManager.getString(preference.key, "")
            )
        }
    }
}
