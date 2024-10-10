package me.phh.treble.app

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.SystemProperties
import android.util.Log
import android.widget.Toast
import android.preference.Preference
import android.preference.PreferenceFragment

object SpoofSettings : Settings {
    val bka = "key_spoof_bka"
    val auto = "key_spoof_auto"
    val product = "key_spoof_product"
    val device = "key_spoof_device"
    val manufacturer = "key_spoof_manufacturer"
    val brand = "key_spoof_brand"
    val model = "key_spoof_model"
    val fingerprint = "key_spoof_fingerprint"
    val securitypatch = "key_spoof_securitypatch"
    val firstapilevel = "key_spoof_firstapilevel"
    val id = "key_spoof_id"
    val type = "key_spoof_type"
    val tags = "key_spoof_tags"
    val incremental = "key_spoof_incremental"
    val release = "key_spoof_release"

    val stateMap = mapOf(
        "key_spoof_bka" to "persist.sys.spoof.bka",
        "key_spoof_product" to "persist.sys.spoof.product",
        "key_spoof_device" to "persist.sys.spoof.device",
        "key_spoof_manufacturer" to "persist.sys.spoof.manufacturer",
        "key_spoof_brand" to "persist.sys.spoof.brand",
        "key_spoof_model" to "persist.sys.spoof.model",
        "key_spoof_fingerprint" to "persist.sys.spoof.fingerprint",
        "key_spoof_securitypatch" to "persist.sys.spoof.security_patch",
        "key_spoof_firstapilevel" to "persist.sys.spoof.first_api_level",
        "key_spoof_id" to "persist.sys.spoof.id",
        "key_spoof_type" to "persist.sys.spoof.type",
        "key_spoof_tags" to "persist.sys.spoof.tags",
        "key_spoof_incremental" to "persist.sys.spoof.incremental",
        "key_spoof_release" to "persist.sys.spoof.release",
    )

    override fun enabled(context: Context): Boolean {
        val isSpoof = SystemProperties.get("persist.sys.spoof.enabled", "false") == "true"
        Log.d("PHH", "SpoofSettings enabled() called, isSpoof = $isSpoof")
        return isSpoof
    }
}

class SpoofSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_spoof)

        Tools.updatePreferenceState(this, SpoofSettings.stateMap)

        if (SpoofSettings.enabled(context)) {
            Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled(context)}")

            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.product)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.device)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.manufacturer)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.brand)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.model)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.fingerprint)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.securitypatch)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.firstapilevel)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.id)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.type)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.tags)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.incremental)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofSettings.release)!!)
        }

        val autoSpoofPref: Preference? = findPreference(SpoofSettings.auto)
        autoSpoofPref?.setOnPreferenceClickListener {
            showUpdateDialog()
            true
        }
    }

    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.update_props))
            .setMessage(getString(R.string.props_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-SPOOF", "Running props update")
                SystemProperties.set("persist.sys.spoof.auto_update", "true")
                Toast.makeText(activity, R.string.toast_reboot, Toast.LENGTH_LONG).show()
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }

}
