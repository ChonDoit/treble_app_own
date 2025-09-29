package me.phh.treble.app

import android.content.Context
import android.util.Log

object OnePlusSettings : Settings {
    val displayModeKey = "key_oneplus_display_mode"
    val highBrightnessModeKey = "key_oneplus_display_high_brightness"
    val usbOtgKey = "key_oneplus_usb_otg"
    val dt2w = "key_oneplus_double_tap_to_wake"

    val stateMap = mapOf(
        "key_oneplus_usb_otg" to "persist.sys.oem.otg_support",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isOnePlus = Tools.vendorFp.contains("OnePlus")
        Log.d("PHH", "OnePlusSettings enabled() called, isOnePlus = $isOnePlus")
        return isOnePlus
    }
}

class OnePlusSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_oneplus, rootKey)

        if (OnePlusSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading OnePlus fragment ${OnePlusSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, OnePlusSettings.stateMap)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(OnePlusSettings.displayModeKey)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(OnePlusSettings.highBrightnessModeKey)!!)
        }
    }
}
