package me.phh.treble.app

import android.content.Context
import android.util.Log
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.phh.treble.app.SpoofSettingsGms.stateMap

object SpoofSettingsGms : Settings {
    val enable_pi = "key_spoof_enable_pi"
    val update_pi = "key_spoof_update_pi"
    val enable_gms = "key_spoof_enable_gms"
    val check_codename = "key_spoof_check_codename"
    val set_codename = "key_spoof_set_codename"
    val gms_hardware = "key_spoof_gms_hardware"
    val gms_product = "key_spoof_gms_product"
    val gms_device = "key_spoof_gms_device"
    val gms_manufacturer = "key_spoof_gms_manufacturer"
    val gms_brand = "key_spoof_gms_brand"
    val gms_model = "key_spoof_gms_model"
    val gms_fingerprint = "key_spoof_gms_fingerprint"
    val gms_securitypatch = "key_spoof_gms_securitypatch"
    val gms_firstapilevel = "key_spoof_gms_firstapilevel"
    val gms_id = "key_spoof_gms_id"
    val gms_type = "key_spoof_gms_type"
    val gms_tags = "key_spoof_gms_tags"
    val gms_incremental = "key_spoof_gms_incremental"
    val gms_release = "key_spoof_gms_release"
    val gms_sdk = "key_spoof_gms_sdk"

    val stateMap = mapOf(
        "key_spoof_enable_gms" to "persist.sys.sp00f.pi.enabled",
        "key_spoof_check_codename" to "persist.sys.sp00f.pi.check_codename",
        "key_spoof_set_codename" to "persist.sys.sp00f.pi.set_codename",
        "key_spoof_enable_gms" to "persist.sys.sp00f.gms.enabled",
        "key_spoof_gms_hardware" to "persist.sys.sp00f.gms_hardware",
        "key_spoof_gms_product" to "persist.sys.sp00f.gms_product",
        "key_spoof_gms_device" to "persist.sys.sp00f.gms_device",
        "key_spoof_gms_manufacturer" to "persist.sys.sp00f.gms_manufacturer",
        "key_spoof_gms_brand" to "persist.sys.sp00f.gms_brand",
        "key_spoof_gms_model" to "persist.sys.sp00f.gms_model",
        "key_spoof_gms_fingerprint" to "persist.sys.sp00f.gms_fingerprint",
        "key_spoof_gms_securitypatch" to "persist.sys.sp00f.gms_security_patch",
        "key_spoof_gms_firstapilevel" to "persist.sys.sp00f.gms_first_api_level",
        "key_spoof_gms_id" to "persist.sys.sp00f.gms_id",
        "key_spoof_gms_type" to "persist.sys.sp00f.gms_type",
        "key_spoof_gms_tags" to "persist.sys.sp00f.gms_tags",
        "key_spoof_gms_incremental" to "persist.sys.sp00f.gms_incremental",
        "key_spoof_gms_release" to "persist.sys.sp00f.gms_release",
        "key_spoof_gms_sdk" to "persist.sys.sp00f.gms_sdk",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        return SpoofSettings.enabled(context)
    }
}

class SpoofSettingsGmsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_spoof_gms, rootKey)

        if (SpoofSettings.enabled(requireContext())) {
            Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled(requireContext())}")

            SettingsActivity.bindPreferenceSummariesFromStateMap(this, stateMap)
        }

        val updatePIPref: Preference? = findPreference(SpoofSettingsGms.update_pi)
        updatePIPref?.setOnPreferenceClickListener {
            showUpdateDialog()
            true
        }
    }

    private fun showUpdateDialog() {
        val builder = MaterialAlertDialogBuilder(activity!!)
        builder.setTitle(getString(R.string.updating_props))
            .setMessage(getString(R.string.updating_props_summary))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-SPOOF", "Running Play Services props update")
                SpoofPropertyManager().fetchAndApply(
                    requireContext(),
                    SpoofPropertyManager.ApplyMode.GMS_ONLY
                ) { success -> }
            }
            .setNegativeButton(android.R.string.no, null)

        builder.show()
    }
}