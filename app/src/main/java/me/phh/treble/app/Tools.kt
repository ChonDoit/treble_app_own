package me.phh.treble.app

import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.SystemProperties
import android.preference.EditTextPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
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
            }
        }
    }

    fun startup(ctxt: Context) {
        audioManager = ctxt.getSystemService(AudioManager::class.java)
    }
}