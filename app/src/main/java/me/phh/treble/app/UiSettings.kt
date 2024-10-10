package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log

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
        "key_ui_sb_padding_top" to "persist.sys.phh.status_bar_padding_top",
        "key_ui_sb_padding_start" to "persist.sys.phh.status_bar_padding_start",
        "key_ui_sb_padding_end" to "persist.sys.phh.status_bar_padding_end",
    )

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing UI settings")
        return true
    }
}

class UiSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_ui)

        Tools.updatePreferenceState(this, UiSettings.stateMap)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.fodColor)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.pointerType)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.statusbarpaddingtop)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.statusbarpaddingstart)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(UiSettings.statusbarpaddingend)!!)

        val restartUIHandler: Preference? = findPreference(UiSettings.restartSystemUI)
        restartUIHandler?.setOnPreferenceClickListener {
            restartUIDialog()
            true
        }
    }

    private fun restartUIDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Restarting System UI")
            .setMessage("Are you sure?")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                var cmds = listOf(
                    arrayOf("su", "-c", "/system/bin/killall com.android.systemui"),
                    arrayOf("phh-su", "-c", "/system/bin/killall com.android.systemui")
                )
                for (cmd in cmds) {
                    try {
                        Runtime.getRuntime().exec(cmd).waitFor()
                        break
                    } catch (t: Throwable) {
                        Log.d("PHH", "Failed to exec \"" + cmd.joinToString(separator = " ") + "\", skipping")
                    }
                }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
