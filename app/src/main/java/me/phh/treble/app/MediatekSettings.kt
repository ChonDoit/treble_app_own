package me.phh.treble.app

import android.content.Context
import android.util.Log

object MediatekSettings : Settings {
    val mtkTouchHintIsRotate = "key_mtk_mediatek_touch_hint_rotate"
    val mtkGedKpi = "key_mtk_mediatek_ged_kpi"
    val cognitive = "key_mtk_force_cognitive"

    val stateMap = mapOf(
        "key_mtk_mediatek_ged_kpi" to "persist.sys.phh.mtk_ged_kpi",
        "key_mtk_force_cognitive" to "persist.sys.phh.radio.force_cognitive",

    )
    init { PrefSync.registerSettingsStateMap(stateMap) }
            
    override fun enabled(context: Context): Boolean {
        val isMediatek = Tools.devicePlatform.toLowerCase().startsWith("mt")
        Log.d("PHH", "MediatekSettings enabled() called, isMediatek = $isMediatek")
        return isMediatek
    }
}

class MediatekSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_mediatek, rootKey)

        if (MediatekSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Mediatek fragment ${MediatekSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, MediatekSettings.stateMap)
        }
    }
}
