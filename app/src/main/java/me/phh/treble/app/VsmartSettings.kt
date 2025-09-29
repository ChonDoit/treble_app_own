package me.phh.treble.app

import android.content.Context
import android.util.Log

object VsmartSettings : Settings {
    val dt2w = "key_vsmart_dt2w"

    val stateMap = mapOf(
        "key_vsmart_dt2w" to "persist.sys.phh.vsmart.dt2w",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isVsmart = Tools.vendorFp.startsWith("vsmart/")
        Log.d("PHH", "VsmartSettings enabled() called, isVsmart = $isVsmart")
        return isVsmart
    }
}

class VsmartSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_vsmart, rootKey)

        if (VsmartSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading  fragment ${VsmartSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, VsmartSettings.stateMap)
        }
    }
}
