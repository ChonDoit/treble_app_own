package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties

object SpoofGms: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofGmsSettings.gms_hardware -> {
                val defvalue = SystemProperties.get("ro.hardware")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_hardware", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_hardware", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting hardware to $value")
            }
            SpoofGmsSettings.gms_product -> {
                val defvalue = SystemProperties.get("ro.product.name")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_product", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_product", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting product to $value")
            }
            SpoofGmsSettings.gms_device -> {
                val defvalue = SystemProperties.get("ro.product.vendor.device")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_device", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_device", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting device to $value")
            }
            SpoofGmsSettings.gms_manufacturer -> {
                val defvalue = SystemProperties.get("ro.product.vendor.manufacturer")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_manufacturer", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_manufacturer", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting manufacturer to $value")
            }
            SpoofGmsSettings.gms_brand -> {
                val defvalue = SystemProperties.get("ro.product.vendor.brand")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_brand", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_brand", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting brand to $value")
            }
            SpoofGmsSettings.gms_model -> {
                val defvalue = SystemProperties.get("ro.product.vendor.model")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_model", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_model", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting model to $value")
            }
            SpoofGmsSettings.gms_fingerprint -> {
                val defvalue = SystemProperties.get("ro.vendor.build.fingerprint")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_fingerprint", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_fingerprint", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting fingerprint to $value")
            }
            SpoofGmsSettings.gms_securitypatch -> {
                val defvalue = SystemProperties.get("ro.build.version.security_patch")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_security_patch", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_security_patch", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting securitypatch to $value")
            }
            SpoofGmsSettings.gms_firstapilevel -> {
                val defvalue = SystemProperties.get("ro.product.first_api_level")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_first_api_level", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_first_api_level", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting firstapilevel to $value")
            }
            SpoofGmsSettings.gms_id -> {
                val defvalue = SystemProperties.get("ro.vendor.build.id")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_id", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_id", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting id to $value")
            }
            SpoofGmsSettings.gms_incremental -> {
                val defvalue = SystemProperties.get("ro.vendor.build.version.incremental")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_incremental", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_incremental", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting incremental to $value")
            }
            SpoofGmsSettings.gms_release -> {
                val defvalue = SystemProperties.get("ro.build.version.release")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_release", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_release", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting release to $value")
            }
            SpoofGmsSettings.gms_sdk -> {
                val defvalue = SystemProperties.get("ro.build.version.sdk")
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_sdk", value)
                } else {
                    SystemProperties.set("persist.sys.spoof.gms_sdk", defvalue)
                }
                Log.d("PHH-SPOOF", "Setting Play Store SDK to $value")
            }
            SpoofGmsSettings.gms_type -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_type", value)
                }
                Log.d("PHH-SPOOF", "Setting type to $value")
            }
            SpoofGmsSettings.gms_tags -> {
                val value = sp.getString(key, "")
                if (!value.isNullOrEmpty()) {
                    SystemProperties.set("persist.sys.spoof.gms_tags", value)
                }
                Log.d("PHH-SPOOF", "Setting tags to $value")
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting SpoofGms service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
