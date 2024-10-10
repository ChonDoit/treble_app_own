package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Backlight: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            BacklightSettings.minimalBrightness -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.overlay.minimal_brightness", if (value) "true" else "false")
            }
            BacklightSettings.disableButtonsBacklight -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_buttons_light", if (value) "true" else "false")
            }
            BacklightSettings.backlightScale -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.backlight.scale", if (value) "1" else "0")
            }
            BacklightSettings.lowGammaBrightness -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.low_gamma_brightness", if (value) "true" else "false")
            }
            BacklightSettings.linearBrightness -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.linear_brightness", if(value) "1" else "0")
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading Backlight fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
    }
}
