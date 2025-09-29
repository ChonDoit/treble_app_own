package me.phh.treble.app

import android.content.Context
import android.util.Log

object CameraSettings : Settings {
    val multiCameras = "key_camera_multi_camera"
    val forceCamera2APIHAL3 = "key_camera_force_camera2api_hal3"
    val cameraTimestampOverride = "key_camera_camera_timestamp"

    val stateMap = mapOf(
        "key_camera_multi_camera" to "persist.sys.phh.include_all_cameras",
        "key_camera_force_camera2api_hal3" to "persist.sys.bt.esco_transport_unit_size",
        "key_camera_camera_timestamp" to "persist.sys.phh.camera.force_timestampsource",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }
        
    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Camera settings")
        return true
    }
}

class CameraSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_camera, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, CameraSettings.stateMap)
    }
}
