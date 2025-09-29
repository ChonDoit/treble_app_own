package me.phh.treble.app

import android.content.Context
import android.util.Log
import android.widget.Toast
import android.os.SystemProperties
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.concurrent.TimeUnit

class SpoofPropertyManager {
    enum class ApplyMode {
        ALL, GMS_ONLY, PLAY_STORE_ONLY
    }

    companion object {
        private const val TAG = "PHH-SPOOF"

        private fun getSpoofJsonUrl(): String {
            return SystemProperties.get("persist.sys.sp00f.json_url", "")
        }

        private val okHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()
        }
    }
    
    fun fetchAndApply(context: Context, mode: ApplyMode = ApplyMode.ALL, callback: (Boolean) -> Unit = {}) {
        val spoofJsonUrl = getSpoofJsonUrl()
    
        if (spoofJsonUrl.isNullOrBlank()) {
            Log.e(TAG, "Spoof JSON URL is empty")
            showToastOnUiThread(context, "Spoof failed: URL not configured")
            callback(false)
            return
        }
    
        val request = Request.Builder().url(spoofJsonUrl).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Network error", e)
                showToastOnUiThread(context, "Spoof failed: Network error")
                callback(false)
            }
    
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        Log.e(TAG, "Bad response: ${it.code}")
                        showToastOnUiThread(context, "Spoof failed: Bad response ${it.code}")
                        callback(false)
                        return
                    }
    
                    try {
                        val jsonString = it.body!!.string()
                        val json = JSONTokener(jsonString).nextValue() as? JSONObject
                        if (json == null || json.length() == 0) {
                            Log.e(TAG, "Empty/invalid JSON")
                            showToastOnUiThread(context, "Spoof failed: Empty or Invalid JSON")
                            callback(false)
                            return
                        }
                        
                        when (mode) {
                            ApplyMode.ALL -> applyProperties(json)
                            ApplyMode.GMS_ONLY -> applyGmsProperties(json)
                            ApplyMode.PLAY_STORE_ONLY -> applyPlayStoreProperties(json)
                        }
                        
                        showToastOnUiThread(context, "Spoof properties applied successfully!")
                        Tools.forceStopPackages(context, "com.google.android.gms", "com.android.vending")
                        callback(true)
                    } catch (e: Exception) {
                        Log.e(TAG, "JSON error", e)
                        showToastOnUiThread(context, "Spoof failed: JSON error")
                        callback(false)
                    }
                }
            }
        })
    }


    private fun showToastOnUiThread(context: Context, message: String) {
        android.os.Handler(context.mainLooper).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyPropertiesHelper(certJson: JSONObject, propertyPrefix: String, jsonPrefix: String, applyTransformation: Boolean = false) {
        val sdk = SystemProperties.get("ro.build.version.sdk")
        val overrideEmpty = SystemProperties.getBoolean("persist.sys.sp00f.json_override_empty", true)

        var codenameReplaced = false
        var newModelName: String? = null

        fun parseSetCodename(setCodename: String): Pair<String, String> {
            return if (setCodename.contains(":")) {
                val parts = setCodename.split(":", limit = 2)
                parts[0].trim() to parts[1].trim()
            } else {
                setCodename to setCodename
            }
        }

        fun setDevice(originalValue: String): String {
            if (!applyTransformation) return originalValue

            val checkCodename = SystemProperties.get("persist.sys.sp00f.pi.check_codename", "")
            val setCodename = SystemProperties.get("persist.sys.sp00f.pi.set_codename", "")

            if (checkCodename.isNotBlank() && setCodename.isNotBlank()) {
                // Parse set_codename in format "codename:model"
                val (newCodename, modelName) = parseSetCodename(setCodename)

                val wordList = checkCodename.split(",").map { it.trim() }
                wordList.forEach { codename ->
                    if (codename.isNotBlank() && originalValue.contains(codename)) {
                        val transformedValue = originalValue.replace(codename, newCodename)
                        Log.d(TAG, "Replaced '$codename' with '$newCodename'")
                        codenameReplaced = true
                        newModelName = modelName
                        return transformedValue
                    }
                }
            }
            return originalValue
        }

        fun setProp(prop: String, key: String, default: String = "") {
            if (overrideEmpty) {
                val originalValue = certJson.optString(key, default)
                val finalValue = setDevice(originalValue)
                SystemProperties.set(prop, finalValue)
                Log.d(TAG, "Set property $prop = $finalValue")
            } else {
                certJson.optString(key, default).takeIf { it.isNotBlank() }?.let { originalValue ->
                    val finalValue = setDevice(originalValue)
                    SystemProperties.set(prop, finalValue)
                    Log.d(TAG, "Set property $prop = $finalValue")
                }
            }
        }

        // Common properties for both GMS and Play Store
        setProp("${propertyPrefix}_hardware", "${jsonPrefix}_HARDWARE")
        setProp("${propertyPrefix}_product", "${jsonPrefix}_PRODUCT")
        setProp("${propertyPrefix}_device", "${jsonPrefix}_DEVICE")
        setProp("${propertyPrefix}_manufacturer", "${jsonPrefix}_MANUFACTURER")
        setProp("${propertyPrefix}_brand", "${jsonPrefix}_BRAND")
        setProp("${propertyPrefix}_model", "${jsonPrefix}_MODEL")
        setProp("${propertyPrefix}_fingerprint", "${jsonPrefix}_FINGERPRINT")
        setProp("${propertyPrefix}_security_patch", "${jsonPrefix}_SECURITY_PATCH")
        setProp("${propertyPrefix}_first_api_level", "${jsonPrefix}_FIRST_API_LEVEL")
        setProp("${propertyPrefix}_id", "${jsonPrefix}_ID")
        setProp("${propertyPrefix}_type", "${jsonPrefix}_TYPE", "user")
        setProp("${propertyPrefix}_tags", "${jsonPrefix}_TAGS", "release-keys")
        setProp("${propertyPrefix}_incremental", "${jsonPrefix}_INCREMENTAL")
        setProp("${propertyPrefix}_release", "${jsonPrefix}_RELEASE")
        setProp("${propertyPrefix}_sdk", "${jsonPrefix}_SDK", sdk)

        if (applyTransformation && codenameReplaced && newModelName != null) {
            val modelProperty = "${propertyPrefix}_model"
            SystemProperties.set(modelProperty, newModelName)
            Log.d(TAG, "Set model to '$newModelName' based on set_codename parsing")
        }
    }
    
    private fun applyGmsProperties(certJson: JSONObject) {
        applyPropertiesHelper(certJson, "persist.sys.sp00f.gms", "GMS", applyTransformation = true)
    }
    
    private fun applyPlayStoreProperties(certJson: JSONObject) {
        applyPropertiesHelper(certJson, "persist.sys.sp00f.ps", "PS", applyTransformation = false)
    }
    
    private fun applyProperties(certJson: JSONObject) {
        applyGmsProperties(certJson)
        applyPlayStoreProperties(certJson)
    }   
}