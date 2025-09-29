package me.phh.treble.app

import android.content.Context
import android.util.Log

object LenovoSettings : Settings {
    val dt2w = "lenovo_double_tap_to_wake"
    val support_pen = "lenovo_support_pen"

    val stateMap: Map<String, String> = mapOf()
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isLenovo = Tools.vendorFp.contains("Lenovo")
        Log.d("PHH", "LenovoSettings enabled() called, isLenovo = $isLenovo")
        return isLenovo
    }
}

class LenovoSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_lenovo, rootKey)

        if (LenovoSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Lenovo fragment ${LenovoSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, LenovoSettings.stateMap)
        }
    }
}
