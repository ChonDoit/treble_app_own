package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties
import android.util.Log
import me.phh.treble.app.SpoofSettings.stateMap

object SpoofSettings : Settings {
    val enable = "key_spoof_enabled"
    val json_url = "key_spoof_json_url"
    val run_on_boot = "key_spoof_run_on_boot"
    val bka = "key_spoof_bka"
    val photos = "key_spoof_photos"

    val stateMap = mapOf(
        "key_spoof_enabled" to "persist.sys.sp00f.enabled",
        "key_spoof_json_url" to "persist.sys.sp00f.json_url",
        "key_spoof_run_on_boot" to "persist.sys.sp00f.run_on_boot",
        "key_spoof_bka" to "persist.sys.sp00f.bka",
        "key_spoof_photos" to "persist.sys.sp00f.photos",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isSpoof = SystemProperties.getBoolean("persist.sys.sp00f.enabled", true)
        Log.d("PHH", "SpoofSettings enabled() called, isSpoof = $isSpoof")
        return when (isSpoof) {
            true, false -> true
            else -> false
        }
    }
}

class SpoofSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_spoof, rootKey)

        if (SpoofSettings.enabled(requireContext())) {
            Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, stateMap)
        }
    }
}
