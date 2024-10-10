package me.phh.treble.app

import android.content.Context
import android.os.SystemProperties
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object HuaweiSettings : Settings {
    val fingerprintGestures = "key_huawei_fingerprint_gestures"
    val touchscreenGloveMode = "key_huawei_touchscreen_glove_mode"
    val fastCharge = "key_huawei_fast_charge"
    val noHwcomposer = "key_huawei_no_hwcomposer"
    val headsetFix = "key_huawei_headset_fix"

    override fun enabled(context: Context): Boolean {
        val isHuawei = Tools.vendorFpLow.contains("huawei") ||
                Tools.vendorFpLow.contains("honor") ||
                SystemProperties.getBoolean("persist.sys.overlay.huawei", false)
        Log.d("PHH", "HuaweiSettings enabled() called, isHuawei = $isHuawei")
        return isHuawei
    }
}

class HuaweiSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_huawei)

        if (HuaweiSettings.enabled(context)) {
            SettingsActivity.bindPreferenceSummaryToValue(findPreference(HuaweiSettings.fastCharge)!!)
        }
    }
}
