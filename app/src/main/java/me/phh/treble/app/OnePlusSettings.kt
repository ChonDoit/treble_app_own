package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object OnePlusSettings : Settings {
    val displayModeKey = "key_oneplus_display_mode"
    val highBrightnessModeKey = "key_oneplus_display_high_brightness"
    val usbOtgKey = "key_oneplus_usb_otg"
    val dt2w = "key_oneplus_double_tap_to_wake"

    override fun enabled(context: Context): Boolean {
        val isOnePlus = Tools.vendorFp.contains("OnePlus")
        Log.d("PHH", "OnePlusSettings enabled() called, isOnePlus = $isOnePlus")
        return isOnePlus
    }
}

class OnePlusSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_oneplus)

        if (OnePlusSettings.enabled(context)) {
            Log.d("PHH", "Loading OnePlus fragment ${OnePlusSettings.enabled(context)}")
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(OnePlusSettings.displayModeKey)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(OnePlusSettings.highBrightnessModeKey)!!)
        }
    }
}
