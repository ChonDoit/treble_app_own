package me.phh.treble.app

import android.content.Context
import android.os.Bundle
import androidx.annotation.XmlRes
import androidx.preference.PreferenceFragmentCompat

interface Settings {
    fun enabled(context: Context): Boolean
}

abstract class SettingsFragment : PreferenceFragmentCompat() {
    @get:XmlRes
    abstract val preferencesResId: Int

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(preferencesResId, rootKey)
    }
}