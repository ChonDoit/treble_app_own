package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object XiaomiSettings : Settings {
    val dt2w = "xiaomi_double_tap_to_wake"

    override fun enabled(context: Context): Boolean {
        val isXiaomi = Tools.vendorFp.toLowerCase().startsWith("xiaomi") ||
                Tools.vendorFp.toLowerCase().startsWith("redmi/") ||
                Tools.vendorFp.toLowerCase().startsWith("poco/")
        Log.d("PHH", "XiaomiSettings enabled() called, isXiaomi = $isXiaomi")
        return isXiaomi
    }
}

class XiaomiSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_xiaomi)

        if (XiaomiSettings.enabled(context)) {
            Log.d("PHH", "Loading Xiaomi fragment ${XiaomiSettings.enabled(context)}")
        }
    }
}
