package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object NubiaSettings : Settings {
    val dt2w = "nubia_double_tap_to_wake"
    val bypassCharger = "nubia_bypass_charger"
    val highTouchScreenSampleRate = "nubia_high_touch_sample_rate"
    val highTouchScreenSensitivity = "nubia_high_touch_sensitivity"
    val tsGameMode = "nubia_touchscreen_game_mode"
    val fanSpeed = "nubia_fan_speed"
    val logoBreath = "nubia_redmagic_logo_breath"
    val redmagicLed = "nubia_redmagic_led"
    val boostCpu = "nubia_boost_cpu"
    val boostGpu = "nubia_boost_gpu"
    val boostCache = "nubia_boost_cache"
    val boostUfs = "nubia_boost_ufs"
    val shoulderBtn = "nubia_shoulder_btn"

    override fun enabled(context: Context): Boolean {
        val isNubia = Tools.vendorFp.toLowerCase().startsWith("nubia/")
        Log.d("PHH", "NubiaSettings enabled() called, isNubia = $isNubia")
        return isNubia
    }

    fun is6Series() = Tools.vendorFp.toLowerCase().startsWith("nubia/nx669")
    fun is5GLite() = Tools.vendorFp.toLowerCase().startsWith("nubia/nx651")
    fun is5G5S() = Tools.vendorFp.toLowerCase().startsWith("nubia/nx659")
}

class NubiaSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_nubia)

        if (OnePlusSettings.enabled(context)) {
            Log.d("PHH", "Loading Nubia fragment ${NubiaSettings.enabled(context)}")
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(NubiaSettings.fanSpeed)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(NubiaSettings.redmagicLed)!!)
        }
    }
}
