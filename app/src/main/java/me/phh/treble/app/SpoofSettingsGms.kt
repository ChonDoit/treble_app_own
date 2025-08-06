package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.preference.PreferenceFragment

object SpoofGmsSettings : Settings {
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
        "key_spoof_gms_hardware" to "persist.sys.spoof.gms_hardware",
        "key_spoof_gms_product" to "persist.sys.spoof.gms_product",
        "key_spoof_gms_device" to "persist.sys.spoof.gms_device",
        "key_spoof_gms_manufacturer" to "persist.sys.spoof.gms_manufacturer",
        "key_spoof_gms_brand" to "persist.sys.spoof.gms_brand",
        "key_spoof_gms_model" to "persist.sys.spoof.gms_model",
        "key_spoof_gms_fingerprint" to "persist.sys.spoof.gms_fingerprint",
        "key_spoof_gms_securitypatch" to "persist.sys.spoof.gms_security_patch",
        "key_spoof_gms_firstapilevel" to "persist.sys.spoof.gms_first_api_level",
        "key_spoof_gms_id" to "persist.sys.spoof.gms_id",
        "key_spoof_gms_type" to "persist.sys.spoof.gms_type",
        "key_spoof_gms_tags" to "persist.sys.spoof.gms_tags",
        "key_spoof_gms_incremental" to "persist.sys.spoof.gms_incremental",
        "key_spoof_gms_release" to "persist.sys.spoof.gms_release",
        "key_spoof_gms_sdk" to "persist.sys.spoof.gms_sdk",
    )

    override fun enabled(context: Context): Boolean {
        return SpoofSettings.enabled(context)
    }
}

class SpoofGmsSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_spoof_gms)

        Tools.updatePreferenceState(this, SpoofGmsSettings.stateMap)

        if (SpoofSettings.enabled(context)) {
            Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled(context)}")

            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_hardware)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_product)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_device)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_manufacturer)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_brand)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_model)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_fingerprint)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_securitypatch)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_firstapilevel)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_id)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_type)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_tags)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_incremental)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_release)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofGmsSettings.gms_sdk)!!)
        }
    }
}