package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.os.SystemProperties
import android.util.Log

object QualcommSettings : Settings {
    val alternateMediaprofile = "key_qualcomm_alternate_mediaprofile"
    val disableSoundVolumeEffect = "key_qualcomm_disable_soundvolume_effect"
    val disableStereoVoip = "key_qualcomm_disable_stereo_voip"
    val directOutputVoip = "key_qualcomm_direct_output_voip"
    val restartQCrild = "key_qualcomm_restart_qcrild"

    override fun enabled(context: Context): Boolean {
        val isQualcomm = QtiAudio.isQualcommDevice || SystemProperties.get("ro.hardware", "N/A") == "qcom"
        Log.d("PHH", "QualcommSettings enabled() called, isQualcomm = $isQualcomm")
        return isQualcomm
    }
}

class QualcommSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_qualcomm)

        if (QualcommSettings.enabled(context)) {
            Log.d("PHH", "Loading Qualcomm fragment ${QualcommSettings.enabled(context)}")
        }
    }
}
