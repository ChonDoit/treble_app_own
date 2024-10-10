package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.preference.Preference
import android.preference.PreferenceFragment

object TelephonySettings : Settings {
    val mobileSignal = "key_telephony_mobile_signal"
    val restartRil = "key_telephony_restart_ril"
    val forceDisplay5g = "key_telephony_force_display_5g"
    val removeTelephony = "key_telephony_removetelephony"
    val simCount = "key_telephony_simcount"
    val smsc = "key_telephony_smsc"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Audio settings")
        return true
    }
}

class TelephonySettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_telephony)

        SettingsActivity.bindPreferenceSummaryToValue(findPreference(TelephonySettings.mobileSignal)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(TelephonySettings.simCount)!!)
        SettingsActivity.bindPreferenceSummaryToValue(findPreference(TelephonySettings.smsc)!!)

        val removeTelephonyHandler: Preference? = findPreference(TelephonySettings.removeTelephony)
        removeTelephonyHandler?.setOnPreferenceClickListener {
            removeTelephonyDialog()
            true
        }
    }

    private fun removeTelephonyDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Removing Telephony")
            .setMessage("Are you sure? This will delete it forever")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                val cmds = listOf(
                    arrayOf("su", "-c", "/system/bin/remove-telephony.sh"),
                    arrayOf("phh-su", "-c", "/system/bin/remove-telephony.sh")
                )
                for (cmd in cmds) {
                    try {
                        Runtime.getRuntime().exec(cmd).waitFor()
                        break
                    } catch (t: Throwable) {
                        Log.d("PHH", "Failed to exec \"${cmd.joinToString(" ")}\", skipping")
                    }
                }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
