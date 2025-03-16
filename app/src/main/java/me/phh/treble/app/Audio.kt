package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Audio: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AudioSettings.headsetDevinput -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.overlay.devinputjack", if (value) "true" else "false")
            }
            AudioSettings.disableAudioEffects -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_audio_effects", if (value) "1" else "0")
            }
            AudioSettings.disableFastAudio -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_fast_audio", if (value) "1" else "0")
            }
            AudioSettings.disableVoiceCallIn -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_voice_call_in", if (value) "true" else "false")
            }
            AudioSettings.alternateAudiopolicy -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                Tools.safeSetprop("persist.sys.phh.caf.audio_policy", value)
            }
            // Bluetooth
            AudioSettings.sysbta -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.bluetooth.system_audio_hal.enabled", if (value) "true" else "false")
            }
            AudioSettings.workarounds -> {
                val value = sp.getString(key, "none")
                when (value) {
                    "none" -> {
                        SystemProperties.set("persist.sys.bt.unsupported.commands", "")
                        SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.lefeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.states", "")
                    }
                    "mediatek", "huawei" -> {
                        SystemProperties.set("persist.sys.bt.unsupported.commands", "182")
                        SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.lefeatures", "")
                        SystemProperties.set("persist.sys.bt.unsupported.states", "")
                    }
                }
            }
            AudioSettings.escoTransportUnitSize -> {
                val value = sp.getString(key, "0")
                SystemProperties.set("persist.sys.bt.esco_transport_unit_size", value)
            }
            AudioSettings.maxBTAudioDevices -> {
                val value = sp.getString(key, "1")?.toInt() ?: 1
                if (value >= 1) {
                    SystemProperties.set("persist.bluetooth.maxconnectedaudiodevices", value.toString())
                } else {
                    SystemProperties.set("persist.bluetooth.maxconnectedaudiodevices", null)
                }
            }
            AudioSettings.unsupportedCommands -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.commands", value)
                Log.d("PHH-Audio", "Setting Bluetooth unsupported commands to $value")
            }
            AudioSettings.unsupportedOgFeatures -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", value)
                Log.d("PHH-Audio", "Setting Bluetooth unsupported og features to $value")
            }
            AudioSettings.unsupportedLeFeatures -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.lefeatures", value)
                Log.d("PHH-Audio", "Setting Bluetooth unsupported le features to $value")
            }
            AudioSettings.unsupportedStates -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.states", value)
                Log.d("PHH-Audio", "Setting Bluetooth unsupported states to $value")
            }
            AudioSettings.leVersionCap -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.max_vendor_cap", value)
                Log.d("PHH-Audio", "Capping Bluetooth LE version to $value")
            }
            AudioSettings.disableLeApcfExtended -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.bt.le.disable_apcf_extended_features", if (value) "1" else "0")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Starting Audio service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        val unsupportedCommands = sp.getString(AudioSettings.unsupportedCommands, "none")

        spListener.onSharedPreferenceChanged(sp, AudioSettings.unsupportedCommands)
        spListener.onSharedPreferenceChanged(sp, AudioSettings.unsupportedOgFeatures)
        spListener.onSharedPreferenceChanged(sp, AudioSettings.unsupportedLeFeatures)
        spListener.onSharedPreferenceChanged(sp, AudioSettings.unsupportedStates)
        spListener.onSharedPreferenceChanged(sp, AudioSettings.leVersionCap)

        sp.edit().putBoolean(AudioSettings.sysbta, SystemProperties.getBoolean("persist.bluetooth.system_audio_hal.enabled", false)).apply()
        if (SamsungSettings.enabled(ctxt)) { sp.edit().putString(AudioSettings.escoTransportUnitSize, "16").apply() }
        if (unsupportedCommands.isNullOrEmpty()) {
            if (HuaweiSettings.enabled(ctxt)) { sp.edit().putString(AudioSettings.workarounds, "huawei").apply() }
            if (MediatekSettings.enabled(ctxt)) { sp.edit().putString(AudioSettings.workarounds, "mediatek").apply() }
            spListener.onSharedPreferenceChanged(sp, AudioSettings.workarounds)
            Log.d("PHH-Audio", "Reapplied AudioSettings.workarounds on boot because unsupportedCommands is empty")
        }
    }
}
