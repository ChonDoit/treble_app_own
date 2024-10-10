package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import java.io.File

object Lenovo: EntryStartup {
    val dtPanel = "/sys/devices/virtual/touch/tp_dev/gesture_on"
    private val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            LenovoSettings.dt2w -> {
                val b = sp.getBoolean(key, false)
                val value = if (b) "1" else "0"
                writeToFileNofail(dtPanel, value)
            }
        }
    }

    fun writeToFileNofail(path: String, content: String) {
        try {
            File(path).printWriter().use { it.println(content) }
        } catch(t: Throwable) {
            Log.d("PHH", "Failed writing to $path", t)
        }
    }

    override fun startup(ctxt: Context) {
        if (!LenovoSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Lenovo service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        spListener.onSharedPreferenceChanged(sp, LenovoSettings.dt2w)
    }
}
