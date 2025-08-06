package me.phh.treble.app

import android.app.ActivityManager
import android.app.IActivityManager
import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.ServiceManager
import android.os.SystemProperties
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.util.Log

object Tools {
    lateinit var audioManager: AudioManager
    val vendorFp = SystemProperties.get("ro.vendor.build.fingerprint")
    val vendorFpLow = vendorFp.lowercase()
    val deviceId = SystemProperties.get("ro.build.overlay.deviceid")
    val devicePlatform = SystemProperties.get("ro.board.platform")
    val phhsu = "/system/bin/phh-su"

    fun safeSetprop(key: String, value: String?) {
        try {
            Log.d("PHH", "Setting property $key to $value")
            SystemProperties.set(key, value)
        } catch (e: Exception) {
            Log.d("PHH", "Failed setting prop $key", e)
        }
    }

    // Check if packages is installed
    fun isPackageInstalled(context: Context, packages: List<String>): List<String> {
        val installedPackages = mutableListOf<String>()
        val pm = context.packageManager
        for (packageName in packages) {
            try {
                pm.getPackageInfo(packageName, 0)
                installedPackages.add(packageName)
            } catch (e: Exception) {
                // Package not found, ignore
            }
        }
        return installedPackages
    }

    // Cehck if APN already exist
    fun checkIfApnExists(context: Context, apnName: String): Cursor? {
        val cr = context.contentResolver ?: return null
        return cr.query(
            Uri.parse("content://telephony/carriers"),
            arrayOf("name", "apn", "type"),
            "name = ?",
            arrayOf(apnName),
            null
        )
    }

    // Update preferences states in a given PreferenceFragment
    fun updatePreferenceState(preferenceFragment: PreferenceFragment, preferenceMap: Map<String, String>) {
        preferenceMap.forEach { (key, propertyKey) ->
            val preference = preferenceFragment.findPreference(key)

            if (preference is SwitchPreference) {
                val propertyValue = SystemProperties.get(propertyKey)
                if (!propertyValue.isNullOrEmpty()) {
                    preference.isChecked = SystemProperties.getBoolean(propertyKey, false)
                }
            } else if (preference is EditTextPreference) {
                val propertyValue = SystemProperties.get(propertyKey, "")
                if (propertyValue != null) {
                    preference.text = propertyValue
                }
            } else if (preference is ListPreference) {
                val propertyValue = SystemProperties.get(propertyKey, "")
                preference.value = propertyValue
                val index = preference.findIndexOfValue(propertyValue)
                if (index >= 0) {
                    preference.summary = preference.entries[index]
                } else {
                    preference.summary = propertyValue
                }
            }
        }
    }
    
    fun forceStopPackage(context: Context, packageName: String): Boolean {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.killBackgroundProcesses(packageName)

            try {
                val appService = IActivityManager.Stub.asInterface(
                    ServiceManager.getService(Context.ACTIVITY_SERVICE))
                appService.forceStopPackage(packageName, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun startup(ctxt: Context) {
        audioManager = ctxt.getSystemService(AudioManager::class.java)
    }
}