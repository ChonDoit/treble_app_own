package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log

object TranssionSettings : Settings {
    val usbOtg = "key_transsion_usb_otg"
    val dt2w = "key_transsion_dt2w"

    override fun enabled(context: Context): Boolean {
        val isTranssion = Tools.vendorFp.startsWith("Infinix/") || Tools.vendorFp.startsWith("TECNO/")
                || Tools.vendorFp.startsWith("Itel/")
        Log.d("PHH", "TranssionSettings enabled() called, isTranssion = $isTranssion")
        return isTranssion
    }
}

class TranssionSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_transsion)

        if (TranssionSettings.enabled(context)) {
            Log.d("PHH", "Loading Transsion fragment ${TranssionSettings.enabled(context)}")
        }
    }
}
