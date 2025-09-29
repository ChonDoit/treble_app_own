package me.phh.treble.app

import android.content.Context
import android.util.Log

object TranssionSettings : Settings {
    val usbOtg = "key_transsion_usb_otg"
    val dt2w = "key_transsion_dt2w"

    val stateMap = mapOf(
        "key_transsion_usb_otg" to "persist.sys.phh.transsion.usbotg",
        "key_transsion_dt2w" to "persist.sys.phh.transsion.dt2w",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isTranssion = Tools.vendorFp.startsWith("Infinix/") || Tools.vendorFp.startsWith("TECNO/")
                || Tools.vendorFp.startsWith("Itel/")
        Log.d("PHH", "TranssionSettings enabled() called, isTranssion = $isTranssion")
        return isTranssion
    }
}

class TranssionSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_transsion, rootKey)

        if (TranssionSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Transsion fragment ${TranssionSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, TranssionSettings.stateMap)
        }
    }
}
