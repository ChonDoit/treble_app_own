package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object AudioSettings : Settings {
    val headsetDevinput = "key_audio_headset_devinput"
    val disableAudioEffects = "key_audio_disable_audio_effects"
    val disableFastAudio = "key_audio_disable_fast_audio"
    val disableVoiceCallIn = "key_audio_disable_voice_call_in"
    val alternateAudiopolicy = "key_audio_alternate_audiopolicy"
    val sysbta = "key_bt_dynamic_sysbta"
    val workarounds = "key_bt_workarounds"
    val escoTransportUnitSize = "key_bt_esco_transport_unit_size"
    val maxBTAudioDevices = "key_bt_max_bluetooth_audio_devices"
    val unsupportedCommands = "key_bt_unsupported_commands"
    val unsupportedOgFeatures = "key_bt_unsupported_og"
    val unsupportedLeFeatures = "key_bt_unsupported_le"
    val unsupportedStates = "key_bt_unsupported_states"
    val leVersionCap = "key_bt_le_version_cap"
    val disableLeApcfExtended = "key_bt_disable_le_apcfe"


    val stateMap = mapOf(
        "key_bt_unsupported_commands" to "persist.sys.bt.unsupported.commands",
        "key_bt_unsupported_og" to "persist.sys.bt.unsupported.ogfeatures",
        "key_bt_unsupported_le" to "persist.sys.bt.unsupported.lefeatures",
        "key_bt_unsupported_states" to "persist.sys.bt.unsupported.states",
        "key_bt_le_version_cap" to "persist.sys.bt.max_vendor_cap",
    )

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Audio settings")
        return true
    }
}

class AudioSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_audio)

        Tools.updatePreferenceState(this, AudioSettings.stateMap)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.workarounds)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.escoTransportUnitSize)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.maxBTAudioDevices)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.unsupportedCommands)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.unsupportedOgFeatures)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.unsupportedLeFeatures)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.unsupportedStates)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(AudioSettings.leVersionCap)!!)
    }
}
