package me.phh.treble.app

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceFragment
import android.util.Log

object DisplaySettings : Settings {
    val displayFps = "key_display_display_fps"
    val dynamicFps = "key_display_dynamic_fps"
    val noHwcomposer = "key_display_no_hwcomposer"
    val aod = "key_display_aod"
    val disableSfGlBackpressure = "key_display_disable_sf_gl_backpressure"
    val disableSfHwcBackpressure = "key_display_disable_sf_hwc_backpressure"
    val sfBlurAlgorithm = "key_display_sf_blur_algorithm"
    val sfRenderEngineBackend = "key_display_sf_renderengine_backend"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Display settings")
        return true
    }
}

class DisplaySettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_display)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.displayFps)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.sfBlurAlgorithm)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.sfRenderEngineBackend)!!)

        // FPS preference setup
        val fpsPref = findPreference(DisplaySettings.displayFps) as ListPreference
        val displayManager = activity?.getSystemService(DisplayManager::class.java)
        displayManager?.displays?.get(0)?.let { display ->
            val fpsEntries = listOf("Don't force") + display.supportedModes.map {
                val fps = it.refreshRate
                val w = it.physicalWidth
                val h = it.physicalHeight
                "${w}x${h}@${fps}"
            }
            val fpsValues = (-1..display.supportedModes.size).toList().map { it.toString() }
            fpsPref.entries = fpsEntries.toTypedArray()
            fpsPref.entryValues = fpsValues.toTypedArray()
        }
    }
}