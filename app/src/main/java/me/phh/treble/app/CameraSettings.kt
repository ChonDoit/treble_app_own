package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object CameraSettings : Settings {
    val multiCameras = "key_camera_multi_camera"
    val forceCamera2APIHAL3 = "key_camera_force_camera2api_hal3"
    val cameraTimestampOverride = "key_camera_camera_timestamp"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Camera settings")
        return true
    }
}

class CameraSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_camera)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(CameraSettings.cameraTimestampOverride)!!)
    }
}
