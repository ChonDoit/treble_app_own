package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties

object Spoof: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofSettings.enable -> {
                val value = sp.getString(key, "manual")
                SystemProperties.set("persist.sys.spoof.enabled", value)
                Log.d("PHH-SPOOF", "Setting in-built spoof to $value")
            }
            SpoofSettings.json -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.spoof.json", value)
                Log.d("PHH-SPOOF", "Setting hardware to $value")
            }
            SpoofSettings.bka -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.spoof.bka", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Blocking key attestation to $value")
            }
            SpoofSettings.hardware -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.spoof.hardware", value)
                Log.d("PHH-SPOOF", "Setting hardware to $value")
            }
            SpoofSettings.product -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.product", value)
                }
                Log.d("PHH-SPOOF", "Setting product to $value")
            }
            SpoofSettings.device -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.device", value)
                }
                Log.d("PHH-SPOOF", "Setting device to $value")
            }
            SpoofSettings.manufacturer -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.manufacturer", value)
                }
                Log.d("PHH-SPOOF", "Setting manufacturer to $value")
            }
            SpoofSettings.brand -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.brand", value)
                }
                Log.d("PHH-SPOOF", "Setting brand to $value")
            }
            SpoofSettings.model -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.model", value)
                }
                Log.d("PHH-SPOOF", "Setting model to $value")
            }
            SpoofSettings.fingerprint -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.fingerprint", value)
                }
                Log.d("PHH-SPOOF", "Setting fingerprint to $value")
            }
            SpoofSettings.securitypatch -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.security_patch", value)
                }
                Log.d("PHH-SPOOF", "Setting securitypatch to $value")
            }
            SpoofSettings.firstapilevel -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.first_api_level", value)
                }
                Log.d("PHH-SPOOF", "Setting firstapilevel to $value")
            }
            SpoofSettings.id -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.id", value)
                }
                Log.d("PHH-SPOOF", "Setting id to $value")
            }
            SpoofSettings.type -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.type", value)
                }
                Log.d("PHH-SPOOF", "Setting type to $value")
            }
            SpoofSettings.tags -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.tags", value)
                }
                Log.d("PHH-SPOOF", "Setting tags to $value")
            }
            SpoofSettings.incremental -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.spoof.incremental", value)
                Log.d("PHH-SPOOF", "Setting incremental to $value")
            }
            SpoofSettings.release -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.spoof.release", value)
                Log.d("PHH-SPOOF", "Setting release to $value")
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Spoof service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        spListener.onSharedPreferenceChanged(sp, SpoofSettings.enable)
    }
}