package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Bluetooth: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            BluetoothSettings.sysbta -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.bluetooth.system_audio_hal.enabled", if (value) "true" else "false")
            }
            BluetoothSettings.workarounds -> {
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
            BluetoothSettings.escoTransportUnitSize -> {
                val value = sp.getString(key, "0")
                SystemProperties.set("persist.sys.bt.esco_transport_unit_size", value)
            }
            BluetoothSettings.maxBTAudioDevices -> {
                val value = sp.getString(key, "1")?.toInt() ?: 1
                if (value >= 1) {
                    SystemProperties.set("persist.bluetooth.maxconnectedaudiodevices", value.toString())
                } else {
                    SystemProperties.set("persist.bluetooth.maxconnectedaudiodevices", null)
                }
            }
            BluetoothSettings.unsupportedCommands -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.commands", value)
                Log.d("PHH-Bluetooth", "Setting Bluetooth unsupported commands to $value")
            }
            BluetoothSettings.unsupportedOgFeatures -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.ogfeatures", value)
                Log.d("PHH-Bluetooth", "Setting Bluetooth unsupported og features to $value")
            }
            BluetoothSettings.unsupportedLeFeatures -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.lefeatures", value)
                Log.d("PHH-Bluetooth", "Setting Bluetooth unsupported le features to $value")
            }
            BluetoothSettings.unsupportedStates -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.unsupported.states", value)
                Log.d("PHH-Bluetooth", "Setting Bluetooth unsupported states to $value")
            }
            BluetoothSettings.leVersionCap -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.bt.max_vendor_cap", value)
                Log.d("PHH-Bluetooth", "Capping Bluetooth LE version to $value")
            }
            BluetoothSettings.disableLeApcfExtended -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.bt.le.disable_apcf_extended_features", if (value) "1" else "0")
            }
        }
        PrefSync.notifyChange()
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Starting Bluetooth service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        val unsupportedCommands = sp.getString(BluetoothSettings.unsupportedCommands, "none")

        spListener.onSharedPreferenceChanged(sp, BluetoothSettings.unsupportedCommands)
        spListener.onSharedPreferenceChanged(sp, BluetoothSettings.unsupportedOgFeatures)
        spListener.onSharedPreferenceChanged(sp, BluetoothSettings.unsupportedLeFeatures)
        spListener.onSharedPreferenceChanged(sp, BluetoothSettings.unsupportedStates)
        spListener.onSharedPreferenceChanged(sp, BluetoothSettings.leVersionCap)

        sp.edit().putBoolean(BluetoothSettings.sysbta, SystemProperties.getBoolean("persist.bluetooth.system_audio_hal.enabled", false)).apply()
        if (SamsungSettings.enabled(ctxt)) { sp.edit().putString(BluetoothSettings.escoTransportUnitSize, "16").apply() }
        if (unsupportedCommands.isNullOrEmpty()) {
            if (HuaweiSettings.enabled(ctxt)) { sp.edit().putString(BluetoothSettings.workarounds, "huawei").apply() }
            if (MediatekSettings.enabled(ctxt)) { sp.edit().putString(BluetoothSettings.workarounds, "mediatek").apply() }
            spListener.onSharedPreferenceChanged(sp, BluetoothSettings.workarounds)
            Log.d("PHH-Bluetooth", "Reapplied BluetoothSettings.workarounds on boot because unsupportedCommands is empty")
        }
    }
}
