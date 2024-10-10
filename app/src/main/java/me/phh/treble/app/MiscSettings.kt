package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.preference.Preference
import android.preference.PreferenceFragment
import android.util.Log
import java.io.File

object MiscSettings : Settings {
    val biometricstrong = "key_misc_biometricstrong"
    val launcher3 = "key_misc_launcher3"
    val dt2w = "key_misc_dt2w"
    val disableSaeUpgrade = "key_misc_disable_sae_upgrade"
    val storageFUSE = "key_misc_storage_fuse"
    val securize = "key_misc_securize"
    val dynamicsuperuser = "key_misc_dynamic_superuser"

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
                var cmds = listOf(
                    arrayOf("su", "-c", "/system/bin/phh-securize.sh"),
                    arrayOf("/system/bin/phh-su", "-c", "/system/bin/phh-securize.sh")
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
