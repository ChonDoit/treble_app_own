package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties
import android.util.Log

object QualcommSettings : Settings {
    val alternateMediaprofile = "key_qualcomm_alternate_mediaprofile"
    val disableSoundVolumeEffect = "key_qualcomm_disable_soundvolume_effect"
    val disableStereoVoip = "key_qualcomm_disable_stereo_voip"
    val directOutputVoip = "key_qualcomm_direct_output_voip"
    val restartQCrild = "key_qualcomm_restart_qcrild"

    val stateMap = mapOf(
        "key_qualcomm_alternate_mediaprofile" to "persist.sys.phh.caf.media_profile",
        "key_qualcomm_disable_soundvolume_effect" to "persist.sys.phh.disable_soundvolume_effect",
        "key_qualcomm_disable_stereo_voip" to "persist.sys.phh.disable_stereo_voip",
        "key_qualcomm_direct_output_voip" to "persist.sys.phh.direct_output_voip",
        "key_qualcomm_restart_qcrild" to "persist.sys.phh.restart_qcrild",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isQualcomm = QtiAudio.isQualcommDevice || SystemProperties.get("ro.hardware", "N/A") == "qcom"
        Log.d("PHH", "QualcommSettings enabled() called, isQualcomm = $isQualcomm")
        return isQualcomm
    }
}

class QualcommSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_qualcomm, rootKey)

        if (QualcommSettings.enabled(requireContext())) {
            Log.d("PHH", "Loading Qualcomm fragment ${QualcommSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, QualcommSettings.stateMap)
        }
    }
}
