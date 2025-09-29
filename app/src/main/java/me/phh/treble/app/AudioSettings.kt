package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties
import android.util.Log
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AudioSettings : Settings {
    val headsetDevinput = "key_audio_headset_devinput"
    val disableAudioEffects = "key_audio_disable_audio_effects"
    val disableFastAudio = "key_audio_disable_fast_audio"
    val disableVoiceCallIn = "key_audio_disable_voice_call_in"
    val alternateAudiopolicy = "key_audio_alternate_audiopolicy"
    val emptyMountAudio = "key_audio_empty_mount_audio"
    val restartAudioServices = "key_audio_restart_audio_services"

    val stateMap = mapOf(
        "key_audio_headset_devinput" to "persist.sys.overlay.devinputjack",
        "key_audio_disable_audio_effects" to "persist.sys.phh.disable_audio_effects",
        "key_audio_disable_fast_audio" to "persist.sys.phh.disable_fast_audio",
        "key_audio_disable_voice_call_in" to "persist.sys.phh.disable_voice_call_in",
        "key_audio_empty_mount_audio" to "persist.sys.phh.empty_mount_audio",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Audio settings")
        return true
    }
}

class AudioSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_audio, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, AudioSettings.stateMap)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.alternateAudiopolicy)!!)

        val restartAudioPref: Preference? = findPreference(AudioSettings.restartAudioServices)
        restartAudioPref?.setOnPreferenceClickListener {
            restartAudioDialog()
            true
        }
    }

    private fun restartAudioDialog() {
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.restarting_audio_services_title))
            .setMessage(getString(R.string.restarting_audio_services_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-AUDIO", "Restarting Audio services")
                SystemProperties.set("persist.sys.phh.restart_audio_server", "true")
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
