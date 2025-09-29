package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.audiofx.AudioEffect
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import java.util.*

class AudioEffectsFragment : BasePreferenceFragment() {
    private val effects = AudioEffect.queryEffects()

    override fun loadPreferences(rootKey: String?) {
        val context = requireContext()
        val screen = preferenceManager.createPreferenceScreen(context)

        screen.addPreference(Preference(context).apply {
            title = "Restart media app to apply changes"
            summary = "Effects are currently applied exclusively on media output"
        })

        effects.forEach { effect ->
            if (effect.connectMode == AudioEffect.EFFECT_INSERT) {
                SwitchPreference(context).apply {
                    key = "audio_effect_${effect.uuid}"
                    title = effect.name
                    summary = "By ${effect.implementor}"
                    screen.addPreference(this)
                }
            }
        }
        preferenceScreen = screen
    }
}

object AudioEffects : SharedPreferences.OnSharedPreferenceChangeListener {
    private val takenEffects = mutableMapOf<UUID, Any>()
    private val effects = AudioEffect.queryEffects()
    private val effectNull = AudioEffect::class.java.getField("EFFECT_TYPE_NULL").get(null) as UUID

    fun startup(context: Context) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        sp.registerOnSharedPreferenceChangeListener(this)

        // Initialize existing preferences
        effects.forEach { effect ->
            if (effect.connectMode == AudioEffect.EFFECT_INSERT) {
                onSharedPreferenceChanged(sp, "audio_effect_${effect.uuid}")
            }
        }
    }

    override fun onSharedPreferenceChanged(sp: SharedPreferences, pref: String) {
        if (!pref.startsWith("audio_effect_")) return

        val effect = effects.find { "audio_effect_${it.uuid}" == pref } ?: run {
            Log.d("PHH", "No effect found for key $pref")
            return
        }

        val enabled = sp.getBoolean(pref, false)

        try {
            if (enabled) {
                Log.d("PHH", "Creating effect ${effect.uuid} ${effect.name}")
                val effectInstance = Class.forName("android.media.audiofx.StreamDefaultEffect")
                    .getConstructor(
                        UUID::class.java,
                        UUID::class.java,
                        Int::class.java,
                        Int::class.java
                    )
                    .newInstance(
                        effectNull,
                        effect.uuid,
                        0,
                        AudioAttributes.USAGE_MEDIA
                    )
                takenEffects[effect.uuid] = effectInstance
                Log.d("PHH", "Effect created successfully")
            } else {
                takenEffects[effect.uuid]?.let {
                    it.javaClass.getMethod("release").invoke(it)
                    takenEffects.remove(effect.uuid)
                    Log.d("PHH", "Effect released successfully")
                }
            }
        } catch (e: Exception) {
            Log.e("PHH", "Error handling effect $pref", e)
        }
    }
}