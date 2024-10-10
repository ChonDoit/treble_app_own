package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object OppoSettings : Settings {
    val dt2w = "key_oppo_double_tap_to_wake"
    val gamingMode = "key_oppo_ts_game_mode"
    val usbOtg = "key_oppo_usb_otg"
    val dcDiming = "key_oppo_dc_diming"

    override fun enabled(context: Context): Boolean {
        val isOppo = Tools.deviceId.startsWith("RMX")
        Log.d("PHH", "OppoSettings enabled() called, isOppo = $isOppo")
        return isOppo
    }
}

class OppoSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_oppo)

        if (OppoSettings.enabled(context)) {
            Log.d("PHH", "Loading Oppo fragment ${OppoSettings.enabled(context)}")
        }
    }
}
