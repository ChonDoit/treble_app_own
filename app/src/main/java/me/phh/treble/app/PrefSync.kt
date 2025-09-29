package me.phh.treble.app

import android.os.SystemProperties
import androidx.preference.*

object PrefSync {
    private val listeners = mutableSetOf<PreferenceFragmentCompat>()
    private val registeredMaps = mutableListOf<Map<String, String>>()

    /** Register a settings stateMap (called from each Settings object) */
    fun registerSettingsStateMap(stateMap: Map<String, String>) {
        registeredMaps.add(stateMap)
        val listenersCopy = listeners.toSet()
        listenersCopy.forEach { updateFragment(it, stateMap) }
    }

    /** Register a fragment to automatically receive state updates */
    fun register(fragment: PreferenceFragmentCompat) {
        listeners.add(fragment)
        val mapsCopy = registeredMaps.toList()
        mapsCopy.forEach { updateFragment(fragment, it) }
    }

    fun unregister(fragment: PreferenceFragmentCompat) {
        listeners.remove(fragment)
    }

    /** Notify all fragments of a change (e.g., property changed) */
    fun notifyChange() {
        val listenersCopy = listeners.toSet()
        val mapsCopy = registeredMaps.toList()
        listenersCopy.forEach { fragment ->
            mapsCopy.forEach { updateFragment(fragment, it) }
        }
    }

    /** Internal: update all preferences in a fragment based on the given stateMap */
    private fun updateFragment(fragment: PreferenceFragmentCompat, preferenceMap: Map<String, String>) {
        preferenceMap.forEach { (key, propertyKey) ->
            val preference = fragment.findPreference<Preference>(key) ?: return@forEach

            when (preference) {
                is SwitchPreference -> {
                    preference.isChecked = SystemProperties.getBoolean(propertyKey, preference.isChecked)
                    preference.setOnPreferenceChangeListener { _, newValue ->
                        SystemProperties.set(propertyKey, if (newValue as Boolean) "true" else "false")
                        true
                    }
                }
                is EditTextPreference -> {
                    val value = SystemProperties.get(propertyKey, "")
                    if (value != null) {
                        preference.text = value
                    }
                    preference.summary = if (value.isNullOrEmpty()) {
                        preference.summary
                    } else {
                        value
                    }
                    preference.setOnPreferenceChangeListener { _, newValue ->
                        SystemProperties.set(propertyKey, newValue as String)
                        true
                    }
                }
                is ListPreference -> {
                    val value = SystemProperties.get(propertyKey, "")
                    preference.value = value
                    val index = preference.findIndexOfValue(value)
                    preference.summary = if (index >= 0) preference.entries[index] else value
                    preference.setOnPreferenceChangeListener { _, newValue ->
                        SystemProperties.set(propertyKey, newValue as String)
                        true
                    }
                }
            }
        }
    }
}