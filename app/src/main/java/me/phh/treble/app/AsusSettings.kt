package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object AsusSettings : Settings {
    val dt2w = "key_asus_dt2w"
    val gloveMode = "key_asus_glove_mode"
    val fpWake = "key_asus_fp_wake"
    val usbPortPicker = "key_asus_usb_port_picker"

    override fun enabled(context: Context): Boolean {
        val isAsus = Tools.vendorFp.contains("asus")
        Log.d("PHH", "AsusSettings.enabled() called, isAsus = $isAsus")
        return isAsus
    }
}

class AsusSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_asus)

        if (AsusSettings.enabled(context)) {
            Log.d("PHH", "Loading Asus fragment ${AsusSettings.enabled(context)}")
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(AsusSettings.usbPortPicker)!!)
        }
    }
}
