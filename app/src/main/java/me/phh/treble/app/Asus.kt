package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import java.lang.Exception
import android.os.SystemProperties
import vendor.ims.zenmotion.V1_0.IZenMotion

object Asus: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AsusSettings.dt2w -> {
                val value = sp.getBoolean(key, false)

                // persist.asus.dclick prop method
                Tools.safeSetprop("persist.sys.phh.asus.dt2w", if(value) "1" else "0")

                // zenmotion method
                val asusSvc = try { IZenMotion.getService() } catch(e: Exception) { null }
                if (asusSvc != null) {
                    asusSvc.setDclickEnable(if(value) 1 else 0)
                }
            }
            AsusSettings.gloveMode -> {
                val value = sp.getBoolean(key, false)
                Tools.safeSetprop("persist.asus.glove", if(value) "1" else "0")
            }
            AsusSettings.fpWake -> {
                val value = sp.getBoolean(key, false)
                Tools.safeSetprop("persist.asus.fp.wakeup", if(value) "true" else "false")
            }
            AsusSettings.usbPortPicker -> {
                val value = sp.getString(key, "none")

                val portlist = SystemProperties.get("sys.usb.all_controllers")
                val port = portlist.split("\n").toMutableList().apply { sort() }

                if (port.size < 2 || port[1].isEmpty())
                    port[1] = port[0]
                when (value) {
                    "port_1" -> {
                        Tools.safeSetprop("persist.sys.phh.asus.usb.port", port[0])
                    }
                    "port_2" -> {
                        Tools.safeSetprop("persist.sys.phh.asus.usb.port", port[1])
                    }
                }
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!AsusSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Asus service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
