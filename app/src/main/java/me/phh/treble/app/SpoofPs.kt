package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties

object SpoofPs: EntryStartup {
    private lateinit var appContext: Context

    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofSettingsPs.enable_ps -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.ps.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Play Store Spoof to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsPs.enable_ps_sdk -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.sp00f.ps_sdk.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Play Store SDK Spoof to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsPs.ps_hardware -> {
                val defvalue = SystemProperties.get("ro.hardware")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_hardware", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_hardware", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting hardware to $value")
            }
            SpoofSettingsPs.ps_product -> {
                val defvalue = SystemProperties.get("ro.product.name")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_product", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_product", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting product to $value")
            }
            SpoofSettingsPs.ps_device -> {
                val defvalue = SystemProperties.get("ro.product.vendor.device")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_device", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_device", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting device to $value")
            }
            SpoofSettingsPs.ps_manufacturer -> {
                val defvalue = SystemProperties.get("ro.product.vendor.manufacturer")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_manufacturer", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_manufacturer", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting manufacturer to $value")
            }
            SpoofSettingsPs.ps_brand -> {
                val defvalue = SystemProperties.get("ro.product.vendor.brand")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_brand", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_brand", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting brand to $value")
            }
            SpoofSettingsPs.ps_model -> {
                val defvalue = SystemProperties.get("ro.product.vendor.model")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_model", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_model", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting model to $value")
            }
            SpoofSettingsPs.ps_fingerprint -> {
                val defvalue = SystemProperties.get("ro.vendor.build.fingerprint")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_fingerprint", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_fingerprint", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting fingerprint to $value")
            }
            SpoofSettingsPs.ps_securitypatch -> {
                val defvalue = SystemProperties.get("ro.build.version.security_patch")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_security_patch", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_security_patch", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting securitypatch to $value")
            }
            SpoofSettingsPs.ps_firstapilevel -> {
                val defvalue = SystemProperties.get("ro.product.first_api_level")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_first_api_level", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_first_api_level", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting firstapilevel to $value")
            }
            SpoofSettingsPs.ps_id -> {
                val defvalue = SystemProperties.get("ro.vendor.build.id")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_id", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_id", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting id to $value")
            }
            SpoofSettingsPs.ps_incremental -> {
                val defvalue = SystemProperties.get("ro.vendor.build.version.incremental")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_incremental", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_incremental", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting incremental to $value")
            }
            SpoofSettingsPs.ps_release -> {
                val defvalue = SystemProperties.get("ro.build.version.release")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_release", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_release", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting release to $value")
            }
            SpoofSettingsPs.ps_sdk -> {
                val defvalue = SystemProperties.get("ro.build.version.sdk")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_sdk", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.ps_sdk", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting Play Store SDK to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsPs.ps_type -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_type", value)
                }
                Log.d("PHH-SPOOF", "Setting type to $value")
            }
            SpoofSettingsPs.ps_tags -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.ps_tags", value)
                }
                Log.d("PHH-SPOOF", "Setting tags to $value")
            }
        }
        PrefSync.notifyChange()
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting SpoofPs service")

        appContext = ctxt.applicationContext

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
