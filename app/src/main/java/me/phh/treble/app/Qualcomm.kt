package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log

object Qualcomm: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            QualcommSettings.alternateMediaprofile -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "true" else "false"
                Tools.safeSetprop("persist.sys.phh.caf.media_profile", value)
            }
            QualcommSettings.disableSoundVolumeEffect -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                Tools.safeSetprop("persist.sys.phh.disable_soundvolume_effect", value)
            }
            QualcommSettings.disableStereoVoip -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "true" else "false"
                Tools.safeSetprop("persist.sys.phh.disable_stereo_voip", value)
            }
            QualcommSettings.directOutputVoip -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "true" else "false"
                Tools.safeSetprop("persist.sys.phh.direct_output_voip", value)
            }
            QualcommSettings.restartQCrild -> {
                val b = sp.getBoolean(key, false)
                val value = if(b) "1" else "0"
                Tools.safeSetprop("persist.sys.phh.restart_qcrild", value)
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!QualcommSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Qualcomm service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)
    }
}
