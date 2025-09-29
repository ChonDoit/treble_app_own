package me.phh.treble.app

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import android.graphics.Color
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.color.DynamicColors

open class BasePreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        activity?.let { DynamicColors.applyToActivityIfAvailable(it) }

        loadPreferences(rootKey)
        preferenceScreen?.let {
            applyCustomCategoryLayout(it)
            disableIconSpaceReserved(it)
        }

        // Check enabled status for each preference and remove if not enabled
        val context = context ?: return
        val checkEnabled = EntryService.getEnabledPreferences(context)
        checkEnabled.forEach { (key, isEnabled) ->
            if (!isEnabled) {
                val preference = findPreference<Preference>(key)
                preference?.let {
                    val parent = preference.parent
                    parent?.removePreference(preference)
                }
            }
        }
    }

    open fun loadPreferences(rootKey: String?) {
        // override in subclasses, e.g.:
        // override fun loadPreferences(rootKey: String?) {
        //            setPreferencesFromResource(R.xml.pref_headers, rootKey)
    }

    private fun disableIconSpaceReserved(pref: Preference) {
        pref.isIconSpaceReserved = false
        if (pref is PreferenceGroup) {
            for (i in 0 until pref.preferenceCount) {
                disableIconSpaceReserved(pref.getPreference(i))
            }
        }
    }

    private fun applyCustomCategoryLayout(pref: Preference) {
        when {
            pref.key == "credits" -> pref.layoutResource = R.layout.preference_item_center
            pref is PreferenceCategory -> pref.layoutResource = R.layout.preference_category
            else -> pref.layoutResource = R.layout.preference_item
        }
        if (pref is PreferenceGroup) {
            for (i in 0 until pref.preferenceCount) {
                applyCustomCategoryLayout(pref.getPreference(i))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            elevation = 0f
        }

        val window = activity?.window ?: return
        val isLightTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightTheme) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isLightTheme)
                window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else
                window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    override fun onResume() {
        super.onResume()
        PrefSync.register(this)
    }

    override fun onPause() {
        super.onPause()
        PrefSync.unregister(this)
    }
}