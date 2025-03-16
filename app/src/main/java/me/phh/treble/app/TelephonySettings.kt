package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.widget.Toast

object TelephonySettings : Settings {
    val mobileSignal = "key_telephony_mobile_signal"
    val restartRil = "key_telephony_restart_ril"
    val forceDisplay5g = "key_telephony_force_display_5g"
    val removeTelephony = "key_telephony_removetelephony"
    val simCount = "key_telephony_simcount"
    val resetSimCount = "key_telephony_reset_simcount"
    val smsc = "key_telephony_smsc"
    val restrictednetworking = "key_telephony_restricted_networking"

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

        val resetSimCountHandler: Preference? = findPreference(TelephonySettings.resetSimCount)
        resetSimCountHandler?.setOnPreferenceClickListener {
            resetSimCountDialog()
            true
        }
    }

    private fun removeTelephonyDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Removing Telephony")
            .setMessage("Are you sure? This will delete it forever")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val outputStream = process.outputStream
                    val writer = outputStream.bufferedWriter()

                    writer.write("/system/bin/remove-telephony.sh\n")
                    writer.write("exit\n")
                    writer.flush()
                    writer.close()

                    val exitCode = process.waitFor()

                    if (exitCode == 0) {
                        Log.d("PHH", "Successfully executed remove-telephony.sh via su shell!")
                        Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
                    } else {
                        Log.e("PHH", "Failed with exit code: $exitCode")
                    }
                } catch (e: Exception) {
                    Log.d("PHH", "Failed to exec su shell directly: ${e.message}")
                }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }

    private fun resetSimCountDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.reset_simcount))
            .setMessage(getString(R.string.reset_simcount_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                SystemProperties.set("persist.sys.phh.sim_count", "reset")
                Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
