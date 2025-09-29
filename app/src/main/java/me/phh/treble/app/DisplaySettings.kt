package me.phh.treble.app

import android.content.Context
import android.hardware.display.DisplayManager
import androidx.preference.ListPreference
import android.util.Log

object DisplaySettings : Settings {
    val displayFps = "key_display_display_fps"
    val dynamicFps = "key_display_dynamic_fps"
    val noHwcomposer = "key_display_no_hwcomposer"
    val aod = "key_display_aod"
    val enableSfGlBackpressure = "key_display_enable_sf_gl_backpressure"
    val enableSfHwcBackpressure = "key_display_enable_sf_hwc_backpressure"
    val sfBlurAlgorithm = "key_display_sf_blur_algorithm"
    val sfRenderEngineBackend = "key_display_sf_renderengine_backend"

    val stateMap = mapOf(
        "key_display_dynamic_fps" to "persist.sys.phh.dynamic_fps",
        "key_display_aod" to "persist.sys.overlay.aod",
        "key_display_enable_sf_gl_backpressure" to "debug.sf.enable_gl_backpressure",
        "key_display_enable_sf_hwc_backpressure" to "persist.sys.phh.enable_sf_hwc_backpressure",
        "key_display_sf_blur_algorithm" to "persist.sys.phh.sf.background_blur",
        "key_display_sf_renderengine_backend" to "debug.renderengine.backend",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Display settings")
        return true
    }
}

class DisplaySettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_display, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, DisplaySettings.stateMap)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(DisplaySettings.displayFps)!!)

        // FPS preference setup
        val fpsPref = findPreference<ListPreference>(DisplaySettings.displayFps)
        val displayManager = activity?.getSystemService(DisplayManager::class.java)
        displayManager?.displays?.firstOrNull()?.let { display ->
            val fpsEntries = listOf("Don't force") + display.supportedModes.map {
                val fps = it.refreshRate
                val w = it.physicalWidth
                val h = it.physicalHeight
                "${w}x${h}@${fps}"
            }
            val fpsValues = (-1..display.supportedModes.size).toList().map { it.toString() }
            fpsPref?.entries = fpsEntries.toTypedArray()
            fpsPref?.entryValues = fpsValues.toTypedArray()
        }
    }
}