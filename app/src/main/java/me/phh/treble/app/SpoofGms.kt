package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties

object SpoofGms: EntryStartup {
    private lateinit var appContext: Context

    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofSettingsGms.enable_pi -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.pi.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting in-built spoof to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsGms.enable_gms -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.sp00f.gms.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting GMS spoof to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsGms.check_codename -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.sp00f.pi.check_codename", value)
                Log.d("PHH-SPOOF", "Setting check_codename to $value")
            }
            SpoofSettingsGms.set_codename -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.sp00f.pi.set_codename", value)
                Log.d("PHH-SPOOF", "Setting set_codename to $value")
            }
            SpoofSettingsGms.gms_hardware -> {
                val defvalue = SystemProperties.get("ro.hardware")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_hardware", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_hardware", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting hardware to $value")
            }
            SpoofSettingsGms.gms_product -> {
                val defvalue = SystemProperties.get("ro.product.name")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_product", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_product", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting product to $value")
            }
            SpoofSettingsGms.gms_device -> {
                val defvalue = SystemProperties.get("ro.product.vendor.device")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_device", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_device", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting device to $value")
            }
            SpoofSettingsGms.gms_manufacturer -> {
                val defvalue = SystemProperties.get("ro.product.vendor.manufacturer")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_manufacturer", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_manufacturer", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting manufacturer to $value")
            }
            SpoofSettingsGms.gms_brand -> {
                val defvalue = SystemProperties.get("ro.product.vendor.brand")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_brand", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_brand", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting brand to $value")
            }
            SpoofSettingsGms.gms_model -> {
                val defvalue = SystemProperties.get("ro.product.vendor.model")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_model", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_model", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting model to $value")
            }
            SpoofSettingsGms.gms_fingerprint -> {
                val defvalue = SystemProperties.get("ro.vendor.build.fingerprint")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_fingerprint", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_fingerprint", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting fingerprint to $value")
            }
            SpoofSettingsGms.gms_securitypatch -> {
                val defvalue = SystemProperties.get("ro.build.version.security_patch")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_security_patch", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_security_patch", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting securitypatch to $value")
            }
            SpoofSettingsGms.gms_firstapilevel -> {
                val defvalue = SystemProperties.get("ro.product.first_api_level")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_first_api_level", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_first_api_level", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting firstapilevel to $value")
            }
            SpoofSettingsGms.gms_id -> {
                val defvalue = SystemProperties.get("ro.vendor.build.id")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_id", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_id", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting id to $value")
            }
            SpoofSettingsGms.gms_incremental -> {
                val defvalue = SystemProperties.get("ro.vendor.build.version.incremental")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_incremental", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_incremental", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting incremental to $value")
            }
            SpoofSettingsGms.gms_release -> {
                val defvalue = SystemProperties.get("ro.build.version.release")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_release", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_release", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting release to $value")
            }
            SpoofSettingsGms.gms_sdk -> {
                val defvalue = SystemProperties.get("ro.build.version.sdk")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_sdk", value)
                } else {
                    SystemProperties.set("persist.sys.sp00f.gms_sdk", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting Play Store SDK to $value")
                Tools.forceStopPackages(appContext, "com.google.android.gms", "com.android.vending")
            }
            SpoofSettingsGms.gms_type -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_type", value)
                }
                Log.d("PHH-SPOOF", "Setting type to $value")
            }
            SpoofSettingsGms.gms_tags -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.sp00f.gms_tags", value)
                }
                Log.d("PHH-SPOOF", "Setting tags to $value")
            }
        }
        PrefSync.notifyChange()
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting SpoofGms service")

        appContext = ctxt.applicationContext

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
