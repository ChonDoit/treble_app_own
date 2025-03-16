package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Telephony: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            TelephonySettings.mobileSignal -> {
                val value = sp.getString(key, "default")
                SystemProperties.set("persist.sys.signal.level", value)
                Log.d("PHH", "Setting signal level method to $value")
            }
            TelephonySettings.restartRil -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.restart_ril", if (value) "true" else "false")
            }
            TelephonySettings.forceDisplay5g -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.force_display_5g", if(value) "1" else "0")
            }
            TelephonySettings.simCount -> {
                val value = sp.getString(key, "default")
                SystemProperties.set("persist.sys.phh.sim_count", value)
                Log.d("PHH", "Setting SIM count to $value")
            }
            TelephonySettings.smsc -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.phh.smsc", value)
                Log.d("PHH", "Setting SMSC to $value")
            }
            TelephonySettings.restrictednetworking -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.restricted_networking", if(value) "1" else "0")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Starting Telephony service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, TelephonySettings.mobileSignal)
        spListener.onSharedPreferenceChanged(sp, TelephonySettings.restrictednetworking)
    }
}
