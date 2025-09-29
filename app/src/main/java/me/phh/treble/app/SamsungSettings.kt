package me.phh.treble.app

import android.content.Context
import android.util.Log

object SamsungSettings : Settings {
    val highBrightess = "key_samsung_high_brightness"
    val gloveMode = "key_samsung_glove_mode"
    val audioStereoMode = "key_samsung_audio_stereo"
    val wirelessChargingTransmit = "key_samsung_wireless_charging_transmit"
    val doubleTapToWake = "key_samsung_double_tap_to_wake"
    val extraSensors = "key_samsung_extra_sensors"
    val colorspace = "key_samsung_colorspace"
    val brokenFingerprint = "key_samsung_broken_fingerprint"
    val backlightMultiplier = "key_samsung_backlight_multiplier"
    val cameraIds = "key_samsung_camera_ids"
    val fodSingleClick = "key_samsung_fod_single_click"
    val flashStrength = "key_samsung_flash_strength"
    val disableBackMic = "key_samsung_disable_back_mic"

    val stateMap = mapOf(
        "key_samsung_high_brightness" to "persist.sys.samsung.full_brightness",
        "key_samsung_extra_sensors" to "persist.sys.phh.samsung_sensors",
        "key_samsung_colorspace" to "persist.sys.phh.samsung_colorspace",
        "key_samsung_broken_fingerprint" to "persist.sys.phh.samsung_fingerprint",
        "key_samsung_backlight_multiplier" to "persist.sys.phh.samsung_backlight",
        "key_samsung_camera_ids" to "persist.sys.phh.samsung.camera_ids",
        "key_samsung_flash_strength" to "persist.sys.phh.flash_strength",
        "key_samsung_disable_back_mic" to "persist.sys.phh.disable_back_mic",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isSamsung = Tools.vendorFpLow.startsWith("samsung/") ||
                Tools.vendorFpLow.startsWith("kddi/scv41_")
        Log.d("PHH", "SamsungSettings enabled() called, isSamsung = $isSamsung")
        return isSamsung
    }
}

class SamsungSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_samsung, rootKey)

        if (SamsungSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Samsung fragment ${SamsungSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, SamsungSettings.stateMap)
        }
    }
}
