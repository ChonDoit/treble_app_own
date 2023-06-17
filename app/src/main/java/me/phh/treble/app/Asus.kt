package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

object Asus: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AsusSettings.dt2w -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                Misc.safeSetprop("persist.sys.phh.asus.dt2w", value)
            }
        }
    }

    override fun startup(ctxt: Context) {
        if(!AsusSettings.enabled()) return
        Log.d("PHH", "Starting Asus service")
        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
