package me.phh.treble.app

import android.content.Context
import android.util.Log

object BluetoothSettings : Settings {
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
        "key_bt_dynamic_sysbta" to "persist.bluetooth.system_audio_hal.enabled",
        "key_bt_esco_transport_unit_size" to "persist.sys.bt.esco_transport_unit_size",
        "key_bt_max_bluetooth_audio_devices" to "persist.bluetooth.maxconnectedaudiodevices",
        "key_bt_unsupported_commands" to "persist.sys.bt.unsupported.commands",
        "key_bt_unsupported_og" to "persist.sys.bt.unsupported.ogfeatures",
        "key_bt_unsupported_le" to "persist.sys.bt.unsupported.lefeatures",
        "key_bt_unsupported_states" to "persist.sys.bt.unsupported.states",
        "key_bt_le_version_cap" to "persist.sys.bt.max_vendor_cap",
        "key_bt_disable_le_apcfe" to "persist.sys.bt.le.disable_apcf_extended_features",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Bluetooth settings")
        return true
    }
}

class BluetoothSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_bluetooth, rootKey)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(BluetoothSettings.workarounds)!!)
        SettingsActivity.bindPreferenceSummariesFromStateMap(this, BluetoothSettings.stateMap)
    }
}
