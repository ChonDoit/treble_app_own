package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Camera: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            CameraSettings.multiCameras -> {
                val value = sp.getBoolean(key, false)

                Tools.safeSetprop("persist.sys.phh.include_all_cameras", if(value) "true" else "false")
                if (value ||
                        SystemProperties.get("vendor.camera.aux.packagelist", null) == null ||
                        SystemProperties.get("camera.aux.packagelist", null) == null) {
                    Tools.safeSetprop("vendor.camera.aux.packagelist", if (value) "nothing" else null)
                    Tools.safeSetprop("camera.aux.packagelist", if (value) "nothing" else null)
                    Tools.safeSetprop("ctl.restart", "vendor.camera-provider-2-4")
                    Tools.safeSetprop("ctl.restart", "camera-provider-2-4")
                    Tools.safeSetprop("ctl.restart", "cameraserver")
                }
            }
            CameraSettings.forceCamera2APIHAL3 -> {
                val value = sp.getBoolean(key, false)
                val defValue = "0"
                val newValue = if (value) "1" else defValue

                if (value ||
                        SystemProperties.get("persist.vendor.camera.HAL3.enabled", defValue) != newValue ||
                        SystemProperties.get("persist.vendor.camera.eis.enable", defValue) != newValue) {
                    Tools.safeSetprop("persist.vendor.camera.HAL3.enabled", newValue)
                    Tools.safeSetprop("persist.vendor.camera.eis.enable", newValue)
                    Log.d("PHH", "forced Camera2API HAL3 to $value")
                    // Restart services
                    Tools.safeSetprop("ctl.restart", "vendor.camera-provider-2-4")
                    Tools.safeSetprop("ctl.restart", "camera-provider-2-4")
                }
            }
            CameraSettings.cameraTimestampOverride -> {
                val value = sp.getString(key, "-1")
                Log.d("PHH", "Setting cameraTimestampOverride to $value")
                SystemProperties.set("persist.sys.phh.camera.force_timestampsource", value)
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Camera fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, CameraSettings.cameraTimestampOverride)
        spListener.onSharedPreferenceChanged(sp, CameraSettings.forceCamera2APIHAL3)
    }
}
