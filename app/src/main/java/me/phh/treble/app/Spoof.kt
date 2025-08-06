package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.os.SystemProperties
import android.widget.Toast

object Spoof: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            SpoofSettings.enable -> {
                val value = sp.getString(key, "manual")
                SystemProperties.set("persist.sys.spoof.enabled", value)
                Log.d("PHH-SPOOF", "Setting in-built spoof to $value")
            }
            SpoofSettings.enable_ps -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.spoof.ps.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Play Store Spoof to $value")
            }
            SpoofSettings.enable_ps_sdk -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.spoof.ps_sdk.enabled", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Play Store SDK Spoof to $value")
            }
            SpoofSettings.json_url -> {
                val value = sp.getString(key, "")
                SystemProperties.set("persist.sys.spoof.json_url", value)
                Log.d("PHH-SPOOF", "Setting JSON URL to $value")
            }
            SpoofSettings.run_on_boot -> {
                val value = sp.getBoolean(key, true)
                SystemProperties.set("persist.sys.spoof.run_on_boot", if (value) "true" else "false")
                Log.d("PHH-SPOOF", "Setting Run on boot to $value")
            }
        }
    }

    override fun startup(ctxt: Context) {
        if (!SpoofSettings.enabled(ctxt)) return
        Log.d("PHH", "Starting Spoof service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        val shouldRunOnBoot = SystemProperties.getBoolean("persist.sys.spoof.run_on_boot", false)
        if (shouldRunOnBoot) {
            Log.d("PHH-SPOOF", "Running props fetch on boot")
            SpoofPropertyManager().fetchAndApply { success ->
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(ctxt, R.string.update_props_on_boot, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
