package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log


object MediatekSettings : Settings {
    val mtkTouchHintIsRotate = "key_mtk_mediatek_touch_hint_rotate"
    val mtkGedKpi = "key_mtk_mediatek_ged_kpi"
    val cognitive = "key_mtk_force_cognitive"

    override fun enabled(context: Context): Boolean {
        val isMediatek = Tools.devicePlatform.toLowerCase().startsWith("mt")
        Log.d("PHH", "MediatekSettings enabled() called, isMediatek = $isMediatek")
        return isMediatek
    }
}

class MediatekSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_mediatek)

        if (MediatekSettings.enabled(context)) {
            Log.d("PHH", "Loading Mediatek fragment ${MediatekSettings.enabled(context)}")
        }
    }
}
