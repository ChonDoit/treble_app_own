package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.ServiceManager
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

import vendor.mediatek.hardware.agolddaemon.IAgoldDaemon

object Misc: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            MiscSettings.biometricstrong -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.biometricstrong", if (value) "true" else "false")
            }
            MiscSettings.launcher3 -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.launcher3", if (value) "true" else "false")
            }
            MiscSettings.disableSaeUpgrade -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.wifi_disable_sae", if (value) "true" else "false")
            }
            MiscSettings.storageFUSE -> {
                val value = sp.getBoolean(key, false)
                Log.d("PHH", "Setting storageFUSE to $value")
                SystemProperties.set("persist.sys.fflag.override.settings_fuse", if (!value) "true" else "false")
            }
            MiscSettings.dynamicsuperuser -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.dynamic_superuser", if (value) "1" else "0")
            }
            MiscSettings.unihertzdt2w -> {
                val value = sp.getBoolean(key, false)
                try {
                    val binder = android.os.Binder.allowBlocking(
                        ServiceManager.waitForDeclaredService(IAgoldDaemon.DESCRIPTOR + "/default"));
                    val instance = IAgoldDaemon.Stub.asInterface(binder);
                    val ret = instance.SendMessageToIoctl(100, 0, if(value) 1 else 0, if(value) 1 else 0)
                    Log.d("PHH", "Setting agold touch mode returned $ret")
                } catch(t: Throwable) {
                    Log.d("PHH", "Setting agold touch mode failed", t)
                }
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Misc fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, MiscSettings.storageFUSE)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.dynamicsuperuser)
        spListener.onSharedPreferenceChanged(sp, MiscSettings.unihertzdt2w)
    }
}
