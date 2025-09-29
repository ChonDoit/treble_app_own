package me.phh.treble.app

import android.content.Context
import android.util.Log

object OppoSettings : Settings {
    val dt2w = "key_oppo_double_tap_to_wake"
    val gamingMode = "key_oppo_ts_game_mode"
    val usbOtg = "key_oppo_usb_otg"
    val dcDiming = "key_oppo_dc_diming"

    val stateMap = mapOf(
        "key_oppo_double_tap_to_wake" to "persist.sys.phh.oppo.dt2w",
        "key_oppo_ts_game_mode" to "persist.sys.phh.oppo.gaming_mode",
        "key_oppo_usb_otg" to "persist.sys.phh.oppo.usbotg",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isOppo = Tools.deviceId.startsWith("RMX")
        Log.d("PHH", "OppoSettings enabled() called, isOppo = $isOppo")
        return isOppo
    }
}

class OppoSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_oppo, rootKey)

        if (OppoSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Oppo fragment ${OppoSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, OppoSettings.stateMap)
        }
    }
}
