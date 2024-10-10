package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Misc: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            MiscSettings.biometricstrong -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.biometricstrong", if (value) "true" else "false")
            }
            MiscSettings.launcher3 -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.launcher3", if (value) "true" else "false")
            }
            MiscSettings.disableSaeUpgrade -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.wifi_disable_sae", if (value) "true" else "false")
            }
            MiscSettings.storageFUSE -> {
                val value = sp.getBoolean(key, false)
                Log.d("PHH", "Setting storageFUSE to $value")
                SystemProperties.set("persist.sys.fflag.override.settings_fuse", if (!value) "true" else "false")
            }
            MiscSettings.dynamicsuperuser -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.dynamic_superuser", if (value) "1" else "0")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Misc fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, MiscSettings.storageFUSE)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.dynamicsuperuser)
    }
}
