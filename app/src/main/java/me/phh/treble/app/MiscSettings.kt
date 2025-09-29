package me.phh.treble.app

import android.content.Context
import androidx.preference.Preference
import android.util.Log
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

object MiscSettings : Settings {
    val biometricstrong = "key_misc_biometricstrong"
    val launcher3 = "key_misc_launcher3"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val securize = "key_misc_securize"
    val dynamicsuperuser = "key_misc_dynamic_superuser"
    val unihertzdt2w = "key_misc_unihertz_dt2w"
    val virtualSensorsAreReal = "key_misc_virtual_sensors_are_real"

    val stateMap = mapOf(
        "key_misc_dynamic_superuser" to "persist.sys.phh.dynamic_superuser",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Misc settings")
        return true
    }

    fun isRoot() = File(Tools.phhsu).exists()
}

class MiscSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_misc, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, MiscSettings.stateMap)

        val securizeHandler = findPreference<Preference>(MiscSettings.securize)
        securizeHandler?.setOnPreferenceClickListener {
            securizeDialog()
            true
        }
    }

    private fun securizeDialog() {
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.securize_dialog_title))
            .setMessage(getString(R.string.securize_dialog_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val outputStream = process.outputStream
                    val writer = outputStream.bufferedWriter()

                    writer.write("/system/bin/phh-securize.sh\n")
                    writer.write("exit\n")
                    writer.flush()
                    writer.close()

                    val exitCode = process.waitFor()

                    if (exitCode == 0) {
                        Log.d("PHH", "Successfully executed phh-securize.sh via su shell!")
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
}
