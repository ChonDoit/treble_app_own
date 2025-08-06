package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.preference.Preference
import android.util.Log
import android.preference.PreferenceFragment
import android.widget.Toast

object SpoofSettings : Settings {
    val enable = "key_spoof_enable"
    val enable_ps = "key_spoof_enable_ps"
    val enable_ps_sdk = "key_spoof_enable_ps_sdk"
    val json_url = "key_spoof_json_url"
    val run_on_boot = "key_spoof_run_on_boot"
    val auto = "key_spoof_auto"

    val stateMap = mapOf(
        "key_spoof_enable" to "persist.sys.spoof.enabled",
        "key_spoof_enable_ps" to "persist.sys.spoof.ps.enabled",
        "key_spoof_enable_ps_sdk" to "persist.sys.spoof.ps_sdk.enabled",
        "key_spoof_json_url" to "persist.sys.spoof.json_url",
        "key_spoof_run_on_boot" to "persist.sys.spoof.run_on_boot",
    )

    override fun enabled(context: Context): Boolean {
        val isSpoof = SystemProperties.get("persist.sys.spoof.enabled", "") ?: ""
        Log.d("PHH", "SpoofSettings enabled() called, isSpoof = $isSpoof")

        return when (isSpoof) {
            "auto", "manual", "false" -> true
            else -> false
        }
    }
}

class SpoofSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_spoof)

        Tools.updatePreferenceState(this, SpoofSettings.stateMap)

        if (SpoofSettings.enabled(context)) {
            Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled(context)}")

            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.enable)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.json_url)!!)
        }

        val autoSpoofPref: Preference? = findPreference(SpoofSettings.auto)
        autoSpoofPref?.setOnPreferenceClickListener {
            showUpdateDialog()
            true
        }

        val spoofGmsPref = findPreference("key_spoof_gms")
        spoofGmsPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(android.R.id.content, SpoofGmsSettingsFragment())
                ?.addToBackStack(null)
                ?.commit()
            true
        }

        val spoofPsPref = findPreference("key_spoof_ps")
        spoofPsPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(android.R.id.content, SpoofPsSettingsFragment())
                ?.addToBackStack(null)
                ?.commit()
            true
        }
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.update_props))
            .setMessage(getString(R.string.props_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-SPOOF", "Running manual props update")
                SpoofPropertyManager().fetchAndApply { success ->
                    activity?.runOnUiThread {
                        Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}
