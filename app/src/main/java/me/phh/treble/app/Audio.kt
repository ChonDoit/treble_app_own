package me.phh.treble.app

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemProperties
import android.util.Log
import androidx.preference.PreferenceManager
import java.io.File

object Audio: EntryStartup {
    val spListener = SharedPreferences.OnSharedPreferenceChangeListener { sp, key ->
        when(key) {
            AudioSettings.headsetDevinput -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.overlay.devinputjack", if (value) "true" else "false")
            }
            AudioSettings.disableAudioEffects -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_audio_effects", if (value) "1" else "0")
            }
            AudioSettings.disableFastAudio -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_fast_audio", if (value) "1" else "0")
            }
            AudioSettings.disableVoiceCallIn -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.disable_voice_call_in", if (value) "true" else "false")
            }
            AudioSettings.alternateAudiopolicy -> {
                val value = sp.getString(key, "auto")
                when (value) {
                    "auto" -> {
                        if (hasAlternateAudioPolicyFiles()) {
                            SystemProperties.set("persist.sys.phh.caf.audio_policy", "1")
                            Log.d("PHH-Audio", "Auto-enabled alternate audio policy")
                        } else {
                            SystemProperties.set("persist.sys.phh.caf.audio_policy", "0")
                        }
                    }
                    "enabled" -> {
                        SystemProperties.set("persist.sys.phh.caf.audio_policy", "1")
                    }
                    "disabled" -> {
                        SystemProperties.set("persist.sys.phh.caf.audio_policy", "0")
                    }
                }
                Tools.reloadAudioSettings()
            }
            AudioSettings.emptyMountAudio -> {
                val value = sp.getBoolean(key, false)
                SystemProperties.set("persist.sys.phh.empty_mount_audio", if (value) "true" else "false")
            }
        }
        PrefSync.notifyChange()
    }

    private fun hasAlternateAudioPolicyFiles(): Boolean {
        val sku = SystemProperties.get("ro.boot.product.vendor.sku", "")

        return when {
            File("/vendor/etc/audio_policy_configuration_sec.xml").exists() -> true
            File("/vendor/etc/audio/sku_${sku}_qssi/audio_policy_configuration.xml").exists() &&
                    File("/vendor/etc/audio/sku_$sku/audio_policy_configuration.xml").exists() -> true
            File("/vendor/etc/audio/audio_policy_configuration.xml").exists() -> true
            File("/vendor/etc/audio_policy_configuration_base.xml").exists() -> true
            else -> false
        }
    }

    override fun startup(ctxt: Context) {
        Log.d("PHH", "Starting Audio service")

        val sp = PreferenceManager.getDefaultSharedPreferences(ctxt)
        sp.registerOnSharedPreferenceChangeListener(spListener)

        // Refresh parameters on boot
    }
}
