package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import androidx.preference.PreferenceManager
import android.util.Log

object Mediatek: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            MediatekSettings.mtkTouchHintIsRotate -> {
                val value = sp.getBoolean(key, false)
                if(value) {
                    SystemProperties.set("persist.sys.phh.touch_hint", "rotate")
                } else {
                    SystemProperties.set("persist.sys.phh.touch_hint", "touch")
                }
            }
            MediatekSettings.mtkGedKpi -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.mtk_ged_kpi", if (value) "1" else "0")
            }
            MediatekSettings.cognitive -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.radio.force_cognitive", if (value) "true" else "false")
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!MediatekSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Mediatek service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}