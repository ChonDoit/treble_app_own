package me.phh.treble.app

import android.content.Context
import android.util.Log
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.phh.treble.app.SpoofSettingsPs.stateMap

object SpoofSettingsPs : Settings {
    val enable_ps = "key_spoof_enable_ps"
    val enable_ps_sdk = "key_spoof_enable_ps_sdk"
    val update_ps = "key_spoof_update_ps"
    val ps_hardware = "key_spoof_ps_hardware"
    val ps_product = "key_spoof_ps_product"
    val ps_device = "key_spoof_ps_device"
    val ps_manufacturer = "key_spoof_ps_manufacturer"
    val ps_brand = "key_spoof_ps_brand"
    val ps_model = "key_spoof_ps_model"
    val ps_fingerprint = "key_spoof_ps_fingerprint"
    val ps_securitypatch = "key_spoof_ps_securitypatch"
    val ps_firstapilevel = "key_spoof_ps_firstapilevel"
    val ps_id = "key_spoof_ps_id"
    val ps_type = "key_spoof_ps_type"
    val ps_tags = "key_spoof_ps_tags"
    val ps_incremental = "key_spoof_ps_incremental"
    val ps_release = "key_spoof_ps_release"
    val ps_sdk = "key_spoof_ps_sdk"

    val stateMap = mapOf(
        "key_spoof_enable_ps" to "persist.sys.sp00f.ps.enabled",
        "key_spoof_enable_ps_sdk" to "persist.sys.sp00f.ps_sdk.enabled",
        "key_spoof_ps_hardware" to "persist.sys.sp00f.ps_hardware",
        "key_spoof_ps_product" to "persist.sys.sp00f.ps_product",
        "key_spoof_ps_device" to "persist.sys.sp00f.ps_device",
        "key_spoof_ps_manufacturer" to "persist.sys.sp00f.ps_manufacturer",
        "key_spoof_ps_brand" to "persist.sys.sp00f.ps_brand",
        "key_spoof_ps_model" to "persist.sys.sp00f.ps_model",
        "key_spoof_ps_fingerprint" to "persist.sys.sp00f.ps_fingerprint",
        "key_spoof_ps_securitypatch" to "persist.sys.sp00f.ps_security_patch",
        "key_spoof_ps_firstapilevel" to "persist.sys.sp00f.ps_first_api_level",
        "key_spoof_ps_id" to "persist.sys.sp00f.ps_id",
        "key_spoof_ps_type" to "persist.sys.sp00f.ps_type",
        "key_spoof_ps_tags" to "persist.sys.sp00f.ps_tags",
        "key_spoof_ps_incremental" to "persist.sys.sp00f.ps_incremental",
        "key_spoof_ps_release" to "persist.sys.sp00f.ps_release",
        "key_spoof_ps_sdk" to "persist.sys.sp00f.ps_sdk",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        return SpoofSettings.enabled(context)
    }
}

class SpoofSettingsPsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_spoof_ps, rootKey)

        if (SpoofSettings.enabled(requireContext())) {
            Log.d("PHH-SPOOF", "Loading SpoofPs fragment ${SpoofSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, stateMap)
        }

        val updatePsPref: Preference? = findPreference(SpoofSettingsPs.update_ps)
        updatePsPref?.setOnPreferenceClickListener {
            showUpdateDialog()
            true
        }
    }
    
    private fun showUpdateDialog() {
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.updating_props))
            .setMessage(getString(R.string.updating_props_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-SPOOF", "Running Play Store props update")
                SpoofPropertyManager().fetchAndApply(
                    requireContext(), 
                    SpoofPropertyManager.ApplyMode.PLAY_STORE_ONLY
                ) { success -> }
            }
            .setNegativeButton(android.R.string.no, null)
    
        builder.show()
    }
}