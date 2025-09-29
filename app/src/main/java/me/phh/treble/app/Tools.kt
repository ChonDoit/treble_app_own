package me.phh.treble.app

import android.app.ActivityManager
import android.content.Context
import android.database.Cursor
import android.media.AudioManager
import android.net.Uri
import android.os.SystemProperties
import android.util.Log

object Tools {
    lateinit var audioManager: AudioManager

    val vendorFp = SystemProperties.get("ro.vendor.build.fingerprint")
    val vendorFpLow = vendorFp.lowercase()
    val deviceId = SystemProperties.get("ro.build.overlay.deviceid")
    val devicePlatform = SystemProperties.get("ro.board.platform")
    val phhsu = "/system/bin/phh-su"

    private val currentSdk: Int
        get() = SystemProperties.getInt("ro.build.version.sdk", 0)
    fun isAtLeastSdk(targetSdk: Int): Boolean = currentSdk >= targetSdk
    fun isLowerAsSdk(targetSdk: Int): Boolean = currentSdk <= targetSdk

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

    // Check if APN already exist
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

    fun forceStopPackages(context: Context, vararg packageNames: String) {
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val forceStopMethod = am.javaClass.getMethod("forceStopPackage", String::class.java)

            for (pkg in packageNames) {
                forceStopMethod.invoke(am, pkg)
                Log.i("PHH-Tools", "$pkg process killed")
            }
        } catch (e: Exception) {
            Log.e("PHH-Tools", "Failed to kill packages", e)
        }
    }

    fun reloadAudioSettings() {
        Thread {
            Thread.sleep(1000)
            try {
                audioManager.reloadAudioSettings()
                Log.d("PHH-Tools", "Successfully triggered audio settings reload via AudioManager")
            } catch (e: Exception) {
                Log.e("PHH-Tools", "Failed to reload audio settings via AudioManager: ${e.message}")
                Log.d("PHH-Tools", "Attempting fallback: restarting audioserver")
                safeSetprop("ctl.restart", "audioserver")
            }
        }.start()
    }

    fun startup(ctxt: Context) {
        audioManager = ctxt.getSystemService(AudioManager::class.java)
    }
}