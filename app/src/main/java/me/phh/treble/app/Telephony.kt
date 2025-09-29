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
                SystemProperties.set("persist.sys.phh.force_display_5g", if (value) "1" else "0")
            }
            TelephonySettings.simCount -> {
                val value = sp.getString(key, "default")
                SystemProperties.set("persist.sys.phh.sim_count", value)
                Log.d("PHH", "Setting SIM count to $value")
            }
            TelephonySettings.restrictednetworking -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.restricted_networking", if (value) "1" else "0")
            }
            TelephonySettings.smscWorkaround -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.phh.smsc_workaround", if (value) "true" else "false")
            }
            TelephonySettings.smsc0 -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.phh.smsc_0", value)
                Log.d("PHH-SMSC", "Setting SMSC0 to $value")
            }
            TelephonySettings.smsc1 -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.phh.smsc_1", value)
                Log.d("PHH-SMSC", "Setting SMSC1 to $value")

            }
            TelephonySettings.smsc2 -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.phh.smsc_2", value)
                Log.d("PHH-SMSC", "Setting SMSC2 to $value")
            }
            TelephonySettings.smsc3 -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.phh.smsc_3", value)
                Log.d("PHH-SMSC", "Setting SMSC3 to $value")
            }
        }
        PrefSync.notifyChange()
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
