package me.phh.treble.app

import android.content.Context
import android.content.res.Resources
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.SystemProperties
import android.preference.EditTextPreference
import android.preference.ListPreference
import android.preference.Preference
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

    fun updateSpoofState(preferenceFragment: PreferenceFragment, preferenceMap: Map<String, String>) {
        // Get the system resources (Android framework)
        val resources = Resources.getSystem()

        // Retrieve the string-array resource ID
        val arrayResId = resources.getIdentifier(
            "config_certifiedBuildProperties", "array", "android"
        )
        if (arrayResId == 0) return // Exit if the resource is not found

        // Get the string-array
        val stringArray = resources.getStringArray(arrayResId)

        // Parse the string-array into a key-value map
        val certifiedProperties = mutableMapOf<String, String>()
        for (item in stringArray) {
            val parts = item.split(":")
            if (parts.size == 2) {
                certifiedProperties[parts[0]] = parts[1]
            }
        }

        // Update each preference based on the parsed key-value pairs
        preferenceMap.forEach { (key, propertyKey) ->
            val preference = preferenceFragment.findPreference(key)
            if (preference is Preference) {
                // Set the title and summary dynamically
                val value = certifiedProperties[propertyKey]
                if (value != null) {
                    preference.title = propertyKey
                    preference.summary = value
                }
            }
        }
    }

    fun startup(ctxt: Context) {
        audioManager = ctxt.getSystemService(AudioManager::class.java)
    }
}