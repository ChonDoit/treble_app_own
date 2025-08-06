package me.phh.treble.app

import android.util.Log

import android.os.SystemProperties
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.concurrent.TimeUnit

class SpoofPropertyManager {
    companion object {
        private const val TAG = "PHH-SPOOF"
        private val SPOOF_JSON_URL = SystemProperties.get("persist.sys.spoof.json_url")

        private val okHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }

    fun fetchAndApply(callback: (Boolean) -> Unit = {}) {
        if (SPOOF_JSON_URL.isNullOrBlank()) {
            Log.e(TAG, "Spoof JSON URL is empty")
            callback(false)
            return
        }

        val request = Request.Builder().url(SPOOF_JSON_URL).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network error", e)
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e(TAG, "Bad response: ${it.code}")
                        callback(false)
                        return
                    }

                    try {
                        // Read the full response as string first
                        val jsonString = it.body!!.string()
                        val json = JSONTokener(jsonString).nextValue() as? JSONObject
                        if (json == null || json.length() == 0) {
                            Log.e(TAG, "Empty/invalid JSON")
                            callback(false)
                            return
                        }
                        applyProperties(json)
                        callback(true)
                    } catch (e: Exception) {
                        Log.e(TAG, "JSON error", e)
                        callback(false)
                    }
                }
            }
        })
    }

    private fun applyProperties(otaJson: JSONObject) {
        val sdk = SystemProperties.get("ro.build.version.sdk")

        fun setProp(prop: String, key: String, default: String = "") {
            otaJson.optString(key, default).takeIf { it.isNotBlank() }?.let {
                SystemProperties.set(prop, it)
                Log.d(TAG, "Set property $prop = $it")
            }
        }

        // GMS Properties
        setProp("persist.sys.spoof.gms_hardware", "GMS_HARDWARE")
        setProp("persist.sys.spoof.gms_product", "GMS_PRODUCT")
        setProp("persist.sys.spoof.gms_device", "GMS_DEVICE")
        setProp("persist.sys.spoof.gms_manufacturer", "GMS_MANUFACTURER")
        setProp("persist.sys.spoof.gms_brand", "GMS_BRAND")
        setProp("persist.sys.spoof.gms_model", "GMS_MODEL")
        setProp("persist.sys.spoof.gms_fingerprint", "GMS_FINGERPRINT")
        setProp("persist.sys.spoof.gms_security_patch", "GMS_SECURITY_PATCH")
        setProp("persist.sys.spoof.gms_first_api_level", "GMS_FIRST_API_LEVEL")
        setProp("persist.sys.spoof.gms_id", "GMS_ID")
        setProp("persist.sys.spoof.gms_type", "GMS_TYPE", "user")
        setProp("persist.sys.spoof.gms_tags", "GMS_TAGS", "release-keys")
        setProp("persist.sys.spoof.gms_incremental", "GMS_INCREMENTAL")
        setProp("persist.sys.spoof.gms_release", "GMS_RELEASE")
        setProp("persist.sys.spoof.gms_sdk", "GMS_SDK", sdk)

        // Play Store Properties
        setProp("persist.sys.spoof.ps_hardware", "PS_HARDWARE")
        setProp("persist.sys.spoof.ps_product", "PS_PRODUCT")
        setProp("persist.sys.spoof.ps_device", "PS_DEVICE")
        setProp("persist.sys.spoof.ps_manufacturer", "PS_MANUFACTURER")
        setProp("persist.sys.spoof.ps_brand", "PS_BRAND")
        setProp("persist.sys.spoof.ps_model", "PS_MODEL")
        setProp("persist.sys.spoof.ps_fingerprint", "PS_FINGERPRINT")
        setProp("persist.sys.spoof.ps_security_patch", "PS_SECURITY_PATCH")
        setProp("persist.sys.spoof.ps_first_api_level", "PS_FIRST_API_LEVEL")
        setProp("persist.sys.spoof.ps_id", "PS_ID")
        setProp("persist.sys.spoof.ps_type", "PS_TYPE", "user")
        setProp("persist.sys.spoof.ps_tags", "PS_TAGS", "release-keys")
        setProp("persist.sys.spoof.ps_incremental", "PS_INCREMENTAL")
        setProp("persist.sys.spoof.ps_release", "PS_RELEASE")
        setProp("persist.sys.spoof.ps_sdk", "PS_SDK", sdk)
    }
}