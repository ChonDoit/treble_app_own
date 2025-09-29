package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties

object Spoof: EntryStartup {
    private lateinit var appContext: Context

    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofSettings.enable -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting in-built spoof to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettings.json_url -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.sp00f.json_url", value)
                Log.d("PHH-SPOOF", "Setting JSON URL to $value")
            }
            SpoofSettings.run_on_boot -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.run_on_boot", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Run on boot to $value")
            }
            SpoofSettings.bka -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.bka", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Block Key Attestation to $value")
            }
            SpoofSettings.photos -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.photos", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Photos spoof to $value")
            }
        }
        PrefSync.notifyChange()
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Spoof service")

        appContext = ctxt.applicationContext

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
