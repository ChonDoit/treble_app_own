package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager

object Ui: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            UiSettings.twoPaneLayout -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.two_pane_layout", if (value) "true" else "false")
            }
            UiSettings.forceNavbarOff -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.mainkeys", if (value) "1" else "0")
            }
            UiSettings.pointerType -> {
                val value = sp.getString(key, "mouse")
                when (value) {
                    "mouse" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "false")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "false")
                    }
                    "pen" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "true")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "false")
                    }
                    "transparent" -> {
                        SystemProperties.set("persist.sys.overlay.spen_pointer", "false")
                        SystemProperties.set("persist.sys.overlay.trans_pointer", "true")
                    }
                }
            }
            UiSettings.fodColor -> {
                val value = sp.getString(key, "00ff00")
                SystemProperties.set("persist.sys.phh.fod_color", value)
            }
            UiSettings.statusbarpaddingtop -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                if(value > -1) {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_top", value.toString())
                } else {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_top", "-1")
                }
            }
            UiSettings.statusbarpaddingstart -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                if(value > -1) {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_start", value.toString())
                } else {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_start", "-1")
                }
            }
            UiSettings.statusbarpaddingend -> {
                val value = sp.getString(key, "-1")?.toIntOrNull() ?: -1
                if (value > -1) {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_end", value.toString())
                } else {
                    SystemProperties.set("persist.sys.phh.status_bar_padding_end", "-1")
                }
            }
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Loading UI fragment")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
        Telephony.spListener.onSharedPreferenceChanged(sp, UiSettings.twoPaneLayout)
    }
}
