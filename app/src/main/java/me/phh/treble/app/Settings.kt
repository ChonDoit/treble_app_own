package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragment

interface Settings {
    fun enabled(context: Context): Boolean
}

abstract class SettingsFragment : PreferenceFragment() {

    @get:XmlRes
    abstract val preferencesResId: Int

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(preferencesResId)
    }
}
