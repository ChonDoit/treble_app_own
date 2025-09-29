package me.phh.treble.app

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.UserHandle
import android.os.SystemProperties
import android.util.Log
import android.widget.Toast
import kotlin.concurrent.thread


class EntryService: Service() {
    companion object {
        var service: EntryService? = null

        // Return the map of enabled status for settings
        fun getEnabledPreferences(context: Context): Map<String, Boolean> {
            return mapOf(
                "mydevice_settings" to MyDeviceSettings.enabled(context),
                "oneplus_settings" to OnePlusSettings.enabled(context),
                "nubia_settings" to NubiaSettings.enabled(context),
                "vsmart_settings" to VsmartSettings.enabled(context),
                "qualcomm_settings" to QualcommSettings.enabled(context),
                "huawei_settings" to HuaweiSettings.enabled(context),
                "samsung_settings" to SamsungSettings.enabled(context),
                "transsion_settings" to TranssionSettings.enabled(context),
                "lenovo_settings" to LenovoSettings.enabled(context),
                "xiaomi_settings" to XiaomiSettings.enabled(context),
                "oppo_settings" to OppoSettings.enabled(context),
                "asus_settings" to AsusSettings.enabled(context),
                "mediatek_settings" to MediatekSettings.enabled(context),
                "spoof_category" to SpoofSettings.enabled(context),
                "key_doze_motorola" to DozeSettings.isMotorola(),
                "key_misc_root_access" to MiscSettings.isRoot(),
                "key_backlight_force_hwc_brightness" to Tools.isAtLeastSdk(34),
                "key_backlight_force_fallback_light_hal" to Tools.isAtLeastSdk(34),
                "key_misc_virtual_sensors_are_real" to Tools.isAtLeastSdk(34),
                "key_ui_fod_color" to Tools.isLowerAsSdk(34),
            )
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun tryC(fnc: () -> Unit) {
        try {
            fnc()
        } catch(e: Throwable) {
            Log.e("PHH", "Caught", e)
        }
    }

    override fun onCreate() {
        service = this

        thread {
            // Tools
            tryC { Tools.startup(this) }
            tryC { QtiAudio.startup(this) }
            tryC { Lid.startup(this) }
            tryC { Doze.startup(this) }
            tryC { OverlayPicker.startup(this) }

            // Device Specific
            tryC { Mediatek.startup(this) }
            tryC { Qualcomm.startup(this) }

            tryC { Asus.startup(this) }
            tryC { Huawei.startup(this) }
            tryC { Hct.startup(this) }
            tryC { Lenovo.startup(this) }
            tryC { Nubia.startup(this) }
            tryC { OnePlus.startup(this) }
            tryC { Oppo.startup(this) }
            tryC { Samsung.startup(this) }
            tryC { Transsion.startup(this) }
            tryC { Vsmart.startup(this) }
            tryC { Xiaomi.startup(this) }

            // Telephony
            tryC { Ims.startup(this) }
            tryC { Telephony.startup(this) }

            // Display
            tryC { Display.startup(this) }
            tryC { Backlight.startup(this) }
            tryC { Ui.startup(this) }

            // Audio
            tryC { Audio.startup(this) }
            tryC { AudioEffects.startup(this) }
			tryC { Bluetooth.startup(this) }

            // Camera
            tryC { Camera.startup(this) }
			
			// Spoof
            tryC { Spoof.startup(this) }
            tryC { SpoofPs.startup(this) }
            tryC { SpoofGms.startup(this) }

            // Miscellaneous
            tryC { Misc.startup(this) }
            tryC { Debug.startup(this) }

            // Presets
            tryC { PresetDownloader.startup(this) }
            tryC {
                val p = SystemProperties.get("ro.system.ota.json_url", "")
                val c = ComponentName(this, UpdaterActivity::class.java)
                if(p.trim() == "") {
                    packageManager.setComponentEnabledSetting(c, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0)
                } else {
                    packageManager.setComponentEnabledSetting(c, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, 0)
                }
            }
        }
    }
}

class Starter: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val caller = UserHandle.getCallingUserId()
        if (caller != 0) {
            Log.d("PHH", "Service called from user none 0, ignore")
            return
        }
        Log.d("PHH", "Starting service")
        //TODO: Check current user == "admin" == 0
        when(intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                context.startService(Intent(context, EntryService::class.java).apply {
                    flags = Intent.FLAG_RECEIVER_REGISTERED_ONLY
                })

                if (SpoofSettings.enabled(context)) {
                    val shouldRunOnBoot = SystemProperties.getBoolean("persist.sys.sp00f.run_on_boot", false)
                    if (shouldRunOnBoot) {
                        Log.d("PHH-SPOOF", "Running props fetch on boot")
                        SpoofPropertyManager().fetchAndApply(context,
                            SpoofPropertyManager.ApplyMode.GMS_ONLY
                        ) { success -> }
                    }
                }
            }
        }
    }
}

interface EntryStartup {
    fun startup(ctxt: Context)
}
