package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import java.lang.Exception

import vendor.ims.zenmotion.V1_0.IZenMotion

object Asus: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AsusSettings.dt2w -> {
                val value = sp.getBoolean(key, false)

                // persist.asus.dclick prop method
                Misc.safeSetprop("persist.sys.phh.asus.dt2w", if(value) "1" else "0")

                // zenmotion method
                val asusSvc = try { IZenMotion.getService() } catch(e: Exception) { null }
                if(asusSvc != null) {
                    asusSvc.setDclickEnable(if(value) 1 else 0)
                }
            }
            AsusSettings.gloveMode -> {
                val value = sp.getBoolean(key, false)
                Misc.safeSetprop("persist.asus.glove", if(value) "1" else "0")
            }
            AsusSettings.fpWake -> {
                val value = sp.getBoolean(key, false)
                Misc.safeSetprop("persist.asus.fp.wakeup", if(value) "true" else "false")
            }
        }
    }

    override fun startup(ctxt: Context) {
        if(!AsusSettings.enabled()) return
        Log.d("PHH", "Starting Asus service")
        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
