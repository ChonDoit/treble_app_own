package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties
import androidx.preference.Preference
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object TelephonySettings : Settings {
    val mobileSignal = "key_telephony_mobile_signal"
    val restartRil = "key_telephony_restart_ril"
    val forceDisplay5g = "key_telephony_force_display_5g"
    val removeTelephony = "key_telephony_removetelephony"
    val simCount = "key_telephony_simcount"
    val resetSimCount = "key_telephony_reset_simcount"
    val restrictednetworking = "key_telephony_restricted_networking"
    val smscWorkaround = "key_telephony_smsc_workaround"
    val smsc0 = "key_telephony_smsc0"
    val smsc1 = "key_telephony_smsc1"
    val smsc2 = "key_telephony_smsc2"
    val smsc3 = "key_telephony_smsc3"

    val stateMap = mapOf(
        "key_telephony_mobile_signal" to "persist.sys.signal.level",
        "key_telephony_restart_ril" to "persist.sys.phh.restart_ril",
        "key_telephony_force_display_5g" to "persist.sys.phh.force_display_5g",
        "key_telephony_simcount" to "persist.sys.phh.sim_count",
        "key_telephony_restricted_networking" to "persist.sys.phh.restricted_networking",
        "key_telephony_smsc_workaround" to "persist.sys.phh.smsc_workaround",
        "key_telephony_smsc0" to "persist.sys.phh.smsc_0",
        "key_telephony_smsc1" to "persist.sys.phh.smsc_1",
        "key_telephony_smsc2" to "persist.sys.phh.smsc_2",
        "key_telephony_smsc3" to "persist.sys.phh.smsc_3",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Audio settings")
        return true
    }
}

class TelephonySettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_telephony, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, TelephonySettings.stateMap)

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
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.remove_telephony_subsystem_dialog_title))
            .setMessage(getString(R.string.remove_telephony_subsystem_dialog_summary))
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
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.reset_simcount_title))
            .setMessage(getString(R.string.reset_simcount_dialog_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                SystemProperties.set("persist.sys.phh.sim_count", "reset")
                Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
