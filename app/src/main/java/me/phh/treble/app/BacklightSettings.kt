package me.phh.treble.app

import android.content.Context
import android.util.Log

object BacklightSettings : Settings {
    val minimalBrightness = "key_backlight_minimal_brightness"
    val disableButtonsBacklight = "key_backlight_disable_buttons_backlight"
    val backlightScale = "key_backlight_backlight_scale"
    val lowGammaBrightness = "key_backlight_low_gamma_brightness"
    val linearBrightness = "key_backlight_linear_brightness"
    val forceHWCBrightness = "key_backlight_force_hwc_brightness"
    val forceFallbackHal = "key_backlight_force_fallback_light_hal"

    val stateMap = mapOf(
        "key_backlight_minimal_brightness" to "persist.sys.overlay.minimal_brightness",
        "key_backlight_disable_buttons_backlight" to "persist.sys.phh.disable_buttons_light",
        "key_backlight_backlight_scale" to "persist.sys.phh.backlight.scale",
        "key_backlight_low_gamma_brightness" to "persist.sys.phh.low_gamma_brightness",
        "key_backlight_linear_brightness" to "persist.sys.phh.linear_brightness",
        "key_backlight_force_hwc_brightness" to "persist.sys.sf.force_hwc_brightness",
        "key_backlight_force_fallback_light_hal" to "persist.sys.sf.force_light_brightness",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Backlight settings")
        return true
    }
}

class BacklightSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_backlight, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, BacklightSettings.stateMap)
    }
}
