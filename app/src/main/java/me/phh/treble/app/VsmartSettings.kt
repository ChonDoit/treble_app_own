package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object VsmartSettings : Settings {
    val dt2w = "key_vsmart_dt2w"

    override fun enabled(context: Context): Boolean {
        val isVsmart = Tools.vendorFp.startsWith("vsmart/")
        Log.d("PHH", "VsmartSettings enabled() called, isVsmart = $isVsmart")
        return isVsmart
    }
}

class VsmartSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_vsmart)

        if (VsmartSettings.enabled(context)) {
            Log.d("PHH", "Loading  fragment ${VsmartSettings.enabled(context)}")
        }
    }
}
