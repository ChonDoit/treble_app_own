package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.preference.PreferenceFragment

object SpoofPsSettings : Settings {
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
        "key_spoof_ps_hardware" to "persist.sys.spoof.ps_hardware",
        "key_spoof_ps_product" to "persist.sys.spoof.ps_product",
        "key_spoof_ps_device" to "persist.sys.spoof.ps_device",
        "key_spoof_ps_manufacturer" to "persist.sys.spoof.ps_manufacturer",
        "key_spoof_ps_brand" to "persist.sys.spoof.ps_brand",
        "key_spoof_ps_model" to "persist.sys.spoof.ps_model",
        "key_spoof_ps_fingerprint" to "persist.sys.spoof.ps_fingerprint",
        "key_spoof_ps_securitypatch" to "persist.sys.spoof.ps_security_patch",
        "key_spoof_ps_firstapilevel" to "persist.sys.spoof.ps_first_api_level",
        "key_spoof_ps_id" to "persist.sys.spoof.ps_id",
        "key_spoof_ps_type" to "persist.sys.spoof.ps_type",
        "key_spoof_ps_tags" to "persist.sys.spoof.ps_tags",
        "key_spoof_ps_incremental" to "persist.sys.spoof.ps_incremental",
        "key_spoof_ps_release" to "persist.sys.spoof.ps_release",
        "key_spoof_ps_sdk" to "persist.sys.spoof.ps_sdk",
    )

    override fun enabled(context: Context): Boolean {
        return SpoofSettings.enabled(context)
    }
}

class SpoofPsSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_spoof_ps)

        Tools.updatePreferenceState(this, SpoofPsSettings.stateMap)

        if (SpoofSettings.enabled(context)) {
            Log.d("PHH-SPOOF", "Loading SpoofPs fragment ${SpoofSettings.enabled(context)}")

            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_hardware)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_product)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_device)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_manufacturer)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_brand)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_model)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_fingerprint)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_securitypatch)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_firstapilevel)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_id)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_type)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_tags)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_incremental)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_release)!!)
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(SpoofPsSettings.ps_sdk)!!)
        }
    }
}