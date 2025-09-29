package me.phh.treble.app

import android.content.Context
import android.util.Log

object AsusSettings : Settings {
    val dt2w = "key_asus_dt2w"
    val gloveMode = "key_asus_glove_mode"
    val fpWake = "key_asus_fp_wake"
    val usbPortPicker = "key_asus_usb_port_picker"

    val stateMap = mapOf(
        "key_asus_dt2w" to "persist.sys.phh.asus.dt2w",
        "key_asus_glove_mode" to "persist.asus.glove",
        "key_asus_fp_wake" to "persist.asus.fp.wakeup",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }
    
    override fun enabled(context: Context): Boolean {
        val isAsus = Tools.vendorFp.contains("asus")
        Log.d("PHH", "AsusSettings.enabled() called, isAsus = $isAsus")
        return isAsus
    }
}

class AsusSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_asus, rootKey)

        if (LenovoSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Asus fragment ${AsusSettings.enabled(requireContext())}")
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(AsusSettings.usbPortPicker)!!)
        }
    }
}
