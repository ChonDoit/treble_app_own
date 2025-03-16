package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import java.io.File

object Lenovo: EntryStartup {

    val dtPanel = "/sys/devices/virtual/touch/tp_dev/gesture_on" // K5P
    val dtPanel_Y700_2023 = "/proc/gesture_control" // DT2W for Y700 (2023)
    val supportPen_Y700_2023 = "/proc/support_pen" // stylus pen support for Y700 (2023)
    val pen_mode_goodix = "/sys/devices/platform/goodix_ts.0/support_pen" // Lenovo-goodix

    private val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when (key) {
            LenovoSettings.dt2w -> {
                if (File(dtPanel).exists()) {
                    //TODO: We need to check that the screen is on at this time
                    //This won't have any effect if done with screen off
                    val b = sp.getBoolean(key, false)
                    val value = if(b) "1" else "0"
                    writeToFileNofail(dtPanel, value)
                } else if (File(dtPanel_Y700_2023).exists()) {
                    val b = sp.getBoolean(key, false)
                    val value = if(b) "1" else "0"
                    writeToFileNofail(dtPanel_Y700_2023, value)
                }
            }
            LenovoSettings.support_pen -> {
                if (File(supportPen_Y700_2023).exists()) {
                    val b = sp.getBoolean(key, false)
                    val value = if(b) "1" else "0"
                    writeToFileNofail(supportPen_Y700_2023, value)
                } else if (File(pen_mode_goodix).exists()) {
                    val b = sp.getBoolean(key, false)
                    val value = if(b) "1" else "0"
                    writeToFileNofail(pen_mode_goodix, value)
                }
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
        spListener.onSharedPreferenceChanged(sp, LenovoSettings.support_pen)
    }
}
