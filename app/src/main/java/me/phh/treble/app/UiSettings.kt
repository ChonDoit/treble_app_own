package me.phh.treble.app

import android.content.Context
import androidx.preference.Preference
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object UiSettings : Settings {
    val twoPaneLayout = "key_ui_two_pane_layout"
    val forceNavbarOff = "key_UI_force_navbar_off"
    val fodColor = "key_ui_fod_color"
    val pointerType = "key_ui_pointer_type"
    val statusbarpaddingtop = "key_ui_sb_padding_top"
    val statusbarpaddingstart = "key_ui_sb_padding_start"
    val statusbarpaddingend = "key_ui_sb_padding_end"
    val restartSystemUI = "key_ui_restart_systemui"

    val stateMap = mapOf(
        "key_ui_two_pane_layout" to "persist.sys.phh.two_pane_layout",
        "key_UI_force_navbar_off" to "persist.sys.phh.mainkeys",
        "key_ui_fod_color" to "persist.sys.phh.fod_color",
        "key_ui_sb_padding_top" to "persist.sys.phh.status_bar_padding_top",
        "key_ui_sb_padding_start" to "persist.sys.phh.status_bar_padding_start",
        "key_ui_sb_padding_end" to "persist.sys.phh.status_bar_padding_end",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing UI settings")
        return true
    }
}

class UiSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_ui, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, UiSettings.stateMap)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.pointerType)!!)

        val restartUIHandler: Preference? = findPreference(UiSettings.restartSystemUI)
        restartUIHandler?.setOnPreferenceClickListener {
            restartUIDialog()
            true
        }
    }

    private fun restartUIDialog(): Boolean {
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.restarting_system_ui))
            .setMessage(getString(R.string.are_you_sure))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val outputStream = process.outputStream
                    val writer = outputStream.bufferedWriter()

                    writer.write("/system/bin/killall com.android.systemui\n")
                    writer.write("exit\n")
                    writer.flush()
                    writer.close()

                    val exitCode = process.waitFor()

                    if (exitCode == 0) {
                        Log.d("PHH", "Successfully executed killall via su shell!")
                        Toast.makeText(activity, R.string.restart_system_ui, Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("PHH", "Failed with exit code: $exitCode")
                    }

                } catch (e: Exception) {
                    Log.d("PHH", "Failed to exec su shell directly: ${e.message}")
                }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
        return true
    }
}
