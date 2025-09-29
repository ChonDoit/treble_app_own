package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties

import android.util.Log

object HuaweiSettings : Settings {
    val fingerprintGestures = "key_huawei_fingerprint_gestures"
    val touchscreenGloveMode = "key_huawei_touchscreen_glove_mode"
    val fastCharge = "key_huawei_fast_charge"
    val noHwcomposer = "key_huawei_no_hwcomposer"
    val headsetFix = "key_huawei_headset_fix"

    val stateMap: Map<String, String> = mapOf()
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        val isHuawei = Tools.vendorFpLow.contains("huawei") ||
                Tools.vendorFpLow.contains("honor") ||
                SystemProperties.getBoolean("persist.sys.overlay.huawei", false)
        Log.d("PHH", "HuaweiSettings enabled() called, isHuawei = $isHuawei")
        return isHuawei
    }
}

class HuaweiSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_huawei, rootKey)

        if (HuaweiSettings.enabled(requireContext())) {
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(HuaweiSettings.fastCharge)!!)
        }
    }
}
