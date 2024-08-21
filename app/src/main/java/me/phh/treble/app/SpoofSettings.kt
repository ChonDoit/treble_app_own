package me.phh.treble.app

import android.app.AlertDialog
import android.os.Bundle
import android.os.SystemProperties
import android.util.Log
import android.widget.Toast
import androidx.preference.ListPreference
import androidx.preference.Preference

object SpoofSettings : Settings {
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

    override fun enabled() = SystemProperties.get("persist.sys.spoof.enabled", "false") == "true"
}

class SpoofSettingsFragment : SettingsFragment() {
    override val preferencesResId = R.xml.pref_spoof

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)
        android.util.Log.d("PHH-SPOOF", "Loading Spoof fragment ${SpoofSettings.enabled()}")

        val autoSpoofPref = findPreference<Preference>(SpoofSettings.auto)
        autoSpoofPref!!.setOnPreferenceClickListener {
                val builder = AlertDialog.Builder( this.getActivity() )
                builder.setTitle(getString(R.string.update_props))
                builder.setMessage(getString(R.string.props_summary))

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                Log.d("PHH-SPOOF", "Running props update")
                SystemProperties.set("persist.sys.spoof.auto_update", "true")
                Toast.makeText(getActivity(), R.string.toast_reboot, Toast.LENGTH_LONG).show();
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
            }

            builder.show()
            return@setOnPreferenceClickListener true
        }
    }
}
