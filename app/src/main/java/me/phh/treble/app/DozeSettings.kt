package me.phh.treble.app

import android.content.Context
import android.hardware.Sensor
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import android.util.Log

object DozeSettings : Settings {
    val handwaveKey = "key_doze_handwave"
    val pocketKey = "key_doze_pocket"
    val chopchopkey = "key_doze_chopchop"

    val stateMap: Map<String, String> = mapOf()
    init { PrefSync.registerSettingsStateMap(stateMap) }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing Doze settings")
        return true
    }

    fun isMotorola(): Boolean {
        val isMoto = Tools.vendorFpLow.startsWith("motorola")
        Log.d("PHH", "Chop-Chop enabled() called, isMoto = $isMoto")
        return isMoto
    }
}

class DozeSettingsFragment : BasePreferenceFragment() {
    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_doze, rootKey)

        // Check enabled status for each preference and remove if not enabled
        val context = context ?: return
        val checkEnabled = EntryService.getEnabledPreferences(context)
        checkEnabled.forEach { (key, isEnabled) ->
            if (!isEnabled) {
                findPreference<Preference>(key)?.let {
                    it.parent?.removePreference(it)
                }
            }
        }

        // Checking for ChopChop Sensor
        val chopchopPref = findPreference<SwitchPreference>(DozeSettings.chopchopkey)
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

            val sp = PreferenceManager.getDefaultSharedPreferences(context ?: return)
            val editor = sp.edit()
            editor.putBoolean(DozeSettings.chopchopkey, false)
            editor.apply()
        }
    }
}