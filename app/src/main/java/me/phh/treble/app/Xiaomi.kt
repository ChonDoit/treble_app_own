package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.ServiceManager
import android.preference.PreferenceManager
import android.util.Log

import vendor.xiaomi.hw.touchfeature.ITouchFeature

object Xiaomi : EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            XiaomiSettings.dt2w -> {
                val value = sp.getBoolean(key, false)
                Tools.safeSetprop("persist.sys.phh.xiaomi.dt2w", if(value) "1" else "0")
                try {
                    val binder = android.os.Binder.allowBlocking(
                        ServiceManager.waitForDeclaredService(ITouchFeature.DESCRIPTOR + "/default"));
                    val instance = ITouchFeature.Stub.asInterface(binder);
                    val ret = instance.set_mode_value(0 /*touchid*/, 14 /* TOUCH_DOUBLETAP_MODE */, if(value) 1 else 0)
                    Log.d("PHH", "Setting xiaomi touch mode returned $ret")
                } catch(t: Throwable) {
                    Log.d("PHH", "Setting xiaomi touch mode failed", t)
                }
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!XiaomiSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Xiaomi service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, XiaomiSettings.dt2w)
    }
}
