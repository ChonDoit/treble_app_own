package me.phh.treble.app

import android.content.Context
import android.content.Intent
import android.os.Process
import androidx.preference.Preference
import android.util.Log

object DebugSettings : Settings {
    val restartApp = "key_debug_restart_app"
    val debuggable = "key_debug_debuggable_mode"

    val stateMap = mapOf(
        "key_debug_debuggable_mode" to "persist.sys.phh.debuggable",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Debug settings")
        return true
    }
}

class DebugSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_debug, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, DebugSettings.stateMap)

        val restartAppHandler: Preference? = findPreference(DebugSettings.restartApp)
        restartAppHandler?.setOnPreferenceClickListener {
            restartApp()
            true
        }
    }

    private fun restartApp() {
        val intent = activity?.baseContext?.packageManager
            ?.getLaunchIntentForPackage(activity?.baseContext?.packageName ?: return)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        Process.killProcess(Process.myPid())
    }
}
