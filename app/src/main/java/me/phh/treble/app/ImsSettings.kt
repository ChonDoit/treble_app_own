package me.phh.treble.app

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import dalvik.system.PathClassLoader
import java.io.FileInputStream

object ImsSettings : Settings {
    val requestNetwork = "key_ims_request_network"
    val createApn = "key_ims_create_apn"
    val forceEnableSettings = "key_ims_force_enable_setting"
    val installImsApk = "key_ims_install_apn"
    val allowBinderThread = "key_ims_allow_binder_thread_on_incoming_calls"

    val stateMap = mapOf(
        "key_ims_allow_binder_thread_on_incoming_calls" to "persist.sys.phh.allow_binder_thread_on_incoming_calls",
    )
    init { PrefSync.registerSettingsStateMap(stateMap) }

    fun checkHasPhhSignature(): Boolean {
        try {
            val cl = PathClassLoader(
                "/system/framework/services.jar",
                ClassLoader.getSystemClassLoader()
            )
            val pmUtils = cl.loadClass("com.android.server.pm.PackageManagerServiceUtils")
            val field = pmUtils.getDeclaredField("PHH_SIGNATURE")
            Log.d("PHH", "checkHasPhhSignature Field $field")
            return true
        } catch(t: Throwable) {
            Log.d("PHH", "checkHasPhhSignature Field failed")
            return false
        }
    }

    override fun enabled(context: Context): Boolean {
        Log.d("PHH", "Initializing IMS settings")
        return true
    }
}

class ImsSettingsFragment : BasePreferenceFragment() {
    private var downloadReceiver: BroadcastReceiver? = null
    private var downloadId: Long = -1L

    override fun onDestroy() {
        super.onDestroy()
        downloadReceiver?.let {
            try {
                requireContext().applicationContext.unregisterReceiver(it)
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered, ignore
                Log.d("PHH", "Receiver already unregistered", e)
            }
        }
    }

    override fun loadPreferences(rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_ims, rootKey)

        SettingsActivity.bindPreferenceSummariesFromStateMap(this, ImsSettings.stateMap)

        // Update IMS package summary
        findPreference<Preference>(ImsSettings.installImsApk)?.let { pref ->
            val packagesToCheck = listOf(
                "org.codeaurora.ims",
                "com.mediatek.ims",
                "me.phh.ims"
            )
            val installedPackages = Tools.isPackageInstalled(requireContext(), packagesToCheck)
            pref.summary = if (installedPackages.isNotEmpty()) {
                "Installed packages: ${installedPackages.joinToString()}"
            } else {
                "No IMS packages installed"
            }
        }

        // APN creation setup
        findPreference<Preference>(ImsSettings.createApn)?.let { createApn ->
            val cursor = Tools.checkIfApnExists(requireContext(), "PHH IMS")
            createApn.summary = if (cursor != null && cursor.moveToFirst()) {
                "APN PHH IMS already exists"
            } else {
                "No APN PHH IMS found"
            }

            createApn.setOnPreferenceClickListener {
                Log.d("PHH", "Adding \"ims\" APN")

                val tm = requireContext().getSystemService(TelephonyManager::class.java)
                val operator = tm?.simOperator
                if (operator.isNullOrEmpty()) {
                    Log.d("PHH", "No current carrier, bailing out")
                    return@setOnPreferenceClickListener true
                }

                val mcc = operator.substring(0, 3)
                val mnc = operator.substring(3)
                Log.d("PHH", "Got mcc = $mcc and mnc = $mnc")

                val cr = requireContext().contentResolver

                Log.d("PHH", "Adding our own PHH IMS")

                val cv = ContentValues().apply {
                    put("name", "PHH IMS")
                    put("apn", "ims")
                    put("type", "ims")
                    put("edited", "1")
                    put("user_editable", "1")
                    put("user_visible", "1")
                    put("protocol", "IPV4V6")
                    put("roaming_protocol", "IPV6")
                    put("modem_cognitive", "1")
                    put("numeric", operator)
                    put("mcc", mcc)
                    put("mnc", mnc)
                }

                val res = cr.insert(Uri.parse("content://telephony/carriers"), cv)

                if (res != null) {
                    Log.d("PHH", "Insert APN returned $res")
                    createApn.summary = "IMS APN successfully added"
                } else {
                    Log.d("PHH", "Failed to add IMS APN")
                    createApn.summary = "Failed to add IMS APN"
                }

                true
            }
        }

        // IMS APK installation setup
        findPreference<Preference>(ImsSettings.installImsApk)?.let { installIms ->
            // Logging
            Log.d("PHH", "MTK P radio = ${Ims.gotMtkP}")
            Log.d("PHH", "MTK Q radio = ${Ims.gotMtkQ}")
            Log.d("PHH", "MTK R radio = ${Ims.gotMtkR}")
            Log.d("PHH", "MTK S radio = ${Ims.gotMtkS}")
            Log.d("PHH", "MTK AIDL radio = ${Ims.gotMtkAidl}")
            Log.d("PHH", "Qualcomm HIDL radio = ${Ims.gotQcomHidl}")
            Log.d("PHH", "Qualcomm AIDL radio = ${Ims.gotQcomAidl}")

            val signSuffix = if (ImsSettings.checkHasPhhSignature()) "-resigned" else ""
            val (url, message) = when {
                (Ims.gotMtkR || Ims.gotMtkS || Ims.gotMtkAidl) && Build.VERSION.SDK_INT >= 34 ->
                    Pair("https://treble.phh.me/ims-mtk-u$signSuffix.apk", "MediaTek R+ vendor")
                Ims.gotMtkP -> Pair("https://treble.phh.me/stable/ims-mtk-p$signSuffix.apk", "MediaTek P vendor")
                Ims.gotMtkQ -> Pair("https://treble.phh.me/stable/ims-mtk-q$signSuffix.apk", "MediaTek Q vendor")
                Ims.gotMtkR -> Pair("https://treble.phh.me/stable/ims-mtk-r$signSuffix.apk", "MediaTek R vendor")
                Ims.gotMtkS -> Pair("https://treble.phh.me/stable/ims-mtk-s$signSuffix.apk", "MediaTek S vendor")
                (Ims.gotQcomHidl || Ims.gotQcomAidl) && Build.VERSION.SDK_INT >= 34 ->
                    Pair("https://treble.phh.me/ims-caf-u$signSuffix.apk", "Qualcomm vendor")
                Ims.gotQcomHidlMoto -> Pair("https://treble.phh.me/stable/ims-caf-moto$signSuffix.apk", "Qualcomm pre-S vendor (Motorola)")
                Ims.gotQcomHidl -> Pair("https://treble.phh.me/stable/ims-q.64$signSuffix.apk", "Qualcomm pre-S vendor")
                Ims.gotQcomAidl -> Pair("https://treble.phh.me/stable/ims-caf-s$signSuffix.apk", "Qualcomm S+ vendor")
                else -> Pair("https://github.com/ChonDoit/treble_ims/releases/download/Stable/floss-ims-latest.apk", "Floss IMS (EXPERIMENTAL)")
            }

            installIms.title = "Install IMS APK for $message"
            installIms.setOnPreferenceClickListener {
                Toast.makeText(requireContext(), "Downloading IMS APK", Toast.LENGTH_SHORT).show()

                val dm = requireContext().getSystemService(DownloadManager::class.java)

                val downloadRequest = DownloadManager.Request(Uri.parse(url)).apply {
                    setTitle("IMS APK")
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalFilesDir(
                        requireContext(),
                        Environment.DIRECTORY_DOWNLOADS,
                        "ims.apk"
                    )
                }

                downloadId = dm.enqueue(downloadRequest)

                downloadReceiver = object : BroadcastReceiver() {
                    @SuppressLint("Range")
                    override fun onReceive(context: Context, intent: Intent) {
                        Log.d(
                            "PHH",
                            "Received download completed with intent $intent ${intent.data}"
                        )
                        if (intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID,
                                -1L
                            ) != downloadId
                        ) return

                        val query = DownloadManager.Query().setFilterById(downloadId)
                        val cursor = dm.query(query)
                        if (!cursor.moveToFirst()) {
                            Log.d("PHH", "DownloadManager gave us an empty cursor")
                            return
                        }

                        val localUri =
                            Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)))
                        Log.d("PHH", "Got localURI = $localUri")
                        val path = localUri.path!!
                        val pi = context.packageManager.packageInstaller
                        val sessionId =
                            pi.createSession(PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL))
                        val session = pi.openSession(sessionId)

                        Tools.safeSetprop("persist.vendor.vilte_support", "0")

                        Toast.makeText(context, "Installing IMS APK", Toast.LENGTH_SHORT).show()

                        session.openWrite("hello", 0, -1).use { output ->
                            FileInputStream(path).use { input ->
                                val buf = ByteArray(512 * 1024)
                                while (input.available() > 0) {
                                    val l = input.read(buf)
                                    output.write(buf, 0, l)
                                }
                                session.fsync(output)
                            }
                        }

                        val installReceiver = object : BroadcastReceiver() {
                            override fun onReceive(p0: Context?, intent: Intent?) {
                                Log.e("PHH", "Apk install received $intent")
                                Toast.makeText(
                                    p0,
                                    "IMS apk installed! You may now reboot.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        context.applicationContext.registerReceiver(
                            installReceiver,
                            IntentFilter("me.phh.treble.app.ImsInstalled"),
                            Context.RECEIVER_EXPORTED
                        )

                        session.commit(
                            PendingIntent.getBroadcast(
                                context.applicationContext,
                                1,
                                Intent("me.phh.treble.app.ImsInstalled"),
                                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
                            ).intentSender
                        )

                        // Unregister this receiver after processing
                        try {
                            context.applicationContext.unregisterReceiver(this)
                        } catch (e: IllegalArgumentException) {
                            Log.d("PHH", "Receiver already unregistered", e)
                        }
                    }
                }

                requireContext().applicationContext.registerReceiver(
                    downloadReceiver,
                    IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_EXPORTED
                )

                true
            }
        }
    }
}