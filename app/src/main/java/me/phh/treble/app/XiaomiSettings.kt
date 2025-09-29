package me.phh.treble.app

import android.content.Context
import android.util.Log

object XiaomiSettings : Settings {
    val dt2w = "key_xiaomi_dt2w"

    val stateMap = mapOf(
        "key_xiaomi_dt2w" to "persist.sys.phh.xiaomi.dt2w",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isXiaomi = Tools.vendorFp.toLowerCase().startsWith("xiaomi") ||
                Tools.vendorFp.toLowerCase().startsWith("redmi/") ||
                Tools.vendorFp.toLowerCase().startsWith("poco/")
        Log.d("PHH", "XiaomiSettings enabled() called, isXiaomi = $isXiaomi")
        return isXiaomi
    }
}

class XiaomiSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_xiaomi, rootKey)

        if (XiaomiSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Xiaomi fragment ${XiaomiSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, XiaomiSettings.stateMap)
        }
    }
}
