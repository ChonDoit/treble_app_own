package me.phh.treble.app

import android.content.Context
import android.hardware.Sensor
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log

object DozeSettings : Settings {
    val handwaveKey = "key_doze_handwave"
    val pocketKey = "key_doze_pocket"
    val chopchopkey = "key_doze_chopchop"

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Doze settings")
        return true
    }

    fun isMotorola(): Boolean {
        val isMoto = Tools.vendorFp.toLowerCase().startsWith("motorola")
        Log.d("PHH", "Chop-Chop enabled() called, isMoto = $isMoto")
        return isMoto
    }
}

class DozeSettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_doze)

        // Check enabled status for each preference and remove if not enabled
        val context = activity ?: return
        val checkEnabled = EntryService.getEnabledPreferences(context)
        checkEnabled.forEach { (key, isEnabled) ->
            if (!isEnabled) {
                val preference = findPreference(key)
                preference?.let {
                    val parent = preference.parent
                    parent?.removePreference(preference)
                }
            }
        }

        // Checking for ChopChop Sensor
        val chopchopPref = findPreference(DozeSettings.chopchopkey) as? android.preference.SwitchPreference
        var chopchopSensor: Sensor? = null
        try {
            chopchopSensor = Doze.sensorManager.getSensorList(Sensor.TYPE_ALL)
                .firstOrNull { it.stringType == "com.motorola.sensor.chopchop" }
        } catch (e: Exception) {
            // Disabling ChopChop Preference if sensor not found
            chopchopPref?.apply {
                isEnabled = false
                isChecked = false
            }

            val sp = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor = sp.edit()
            editor.putBoolean(DozeSettings.chopchopkey, false)
            editor.apply()
        }
    }
}