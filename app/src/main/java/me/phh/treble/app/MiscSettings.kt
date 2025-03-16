package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import android.widget.Toast
import java.io.File

object MiscSettings : Settings {
    val biometricstrong = "key_misc_biometricstrong"
    val launcher3 = "key_misc_launcher3"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val securize = "key_misc_securize"
    val dynamicsuperuser = "key_misc_dynamic_superuser"
    val unihertzdt2w = "key_misc_unihertz_dt2w"

    val stateMap = mapOf(
        "key_misc_dynamic_superuser" to "persist.sys.phh.dynamic_superuser",
    )

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Misc settings")
        return true
    }

    fun isRoot() = File(Tools.phhsu).exists()
}

class MiscSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_misc)

        Tools.updatePreferenceState(this, MiscSettings.stateMap)

        // Check enabled status for each preference and remove if not enabled
        val context = activity ?: return
        val checkEnabled = EntryService.getEnabledPreferences(context)
        checkEnabled.forEach { (key, isEnabled) ->
            if (!isEnabled) {
                val preference = findPreference(key)
                preference?.let {
                    val parent = preference.parent
                    parent?.removePreference(preference)
                }
            }
        }

        val securizeHandler: Preference? = findPreference(MiscSettings.securize)
        securizeHandler?.setOnPreferenceClickListener {
            securizeDialog()
            true
        }
    }

    private fun securizeDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Removing Root")
            .setMessage("Are you sure? This will remove in-built root access")
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
