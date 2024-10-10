package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Debug: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            DebugSettings.remotectl -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.remote", if (value) "true" else "false")
            }
            DebugSettings.debuggable -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.debuggable", if (value) "true" else "false")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Debug fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
    }
}
