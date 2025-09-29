package me.phh.treble.app

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemProperties
import android.text.Spannable
import android.text.SpannableString
import android.text.format.Formatter
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.IOException
import java.lang.Runnable
import java.net.URL
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.concurrent.thread
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import org.tukaani.xz.XZInputStream

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId

class UpdaterActivity : AppCompatActivity() {

    private val OTA_JSON_URL = SystemProperties.get("ro.system.ota.json_url")
    private var hasUpdate = false
    private var isUpdating = false
    private var otaJson = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivityIfAvailable(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_updater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.apply {
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            elevation = 0f
        }

        val window = window ?: return
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

        updateUiElements(false)
        checkUpdate()

        val btn_update = findViewById(R.id.btn_update) as Button
        btn_update.setOnClickListener {
            if (hasUpdate) {
                isUpdating = true
                downloadUpdate()
            } else {
                isUpdating = false
                checkUpdate()
            }
            return@setOnClickListener
        }
    }

    override fun onBackPressed() {
        if (isUpdating) {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.ota_title))
            builder.setMessage(getString(R.string.prevent_exit_message))
            builder.setPositiveButton(android.R.string.yes) { _, _ ->
                super.onBackPressed()
                finish()
            }
            builder.setNegativeButton(android.R.string.no) { _, _ -> }
            builder.show()
        } else {
            super.onBackPressed()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_delete_ota -> {
                Log.e("PHH", "Deleting OTA file")
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(getString(R.string.warning))
                builder.setMessage(getString(R.string.delete_ota_message))
                builder.setPositiveButton(android.R.string.yes) { _, _ ->
                    Log.e("PHH", "Delete in progress")
                    SystemProperties.set("sys.phh.uninstall-ota", "true");
                    Toast.makeText(this, R.string.toast_delete_ota, Toast.LENGTH_LONG).show()
                }
                builder.setNegativeButton(android.R.string.no) { _, _ ->
                    Log.e("PHH", "Delete canceled")
                }
                builder.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkUpdate() {
        val btnUpdate = findViewById<Button>(R.id.btn_update)
        val titleTextView = findViewById<TextView>(R.id.txt_update_title) // Get the title TextView
        val progressBar = findViewById<LinearProgressIndicator>(R.id.progress_horizontal)
        val progressText = findViewById<TextView>(R.id.progress_value)

        // Hide button and progress initially
        btnUpdate.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        progressText.visibility = View.INVISIBLE

        // Set checking status
        titleTextView.text = getString(R.string.checking_update_title)

        if (isDynamic()) {
            isMagiskInstalled()
            Log.e("PHH", "Updating OTA info at: $OTA_JSON_URL")
            val request = Request.Builder().url(OTA_JSON_URL).build()
            OkHttpClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("PHH", "Failed downloading OTA info. Error: ${e.toString()}", e)
                    runOnUiThread {
                        hasUpdate = false
                        titleTextView.text = getString(R.string.update_not_found_title)
                        updateUiElements(false)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if ((response.code == 200 || response.code == 304) && response.body != null) {
                        try {
                            val body = response.body?.string()
                            otaJson = JSONTokener(body).nextValue() as JSONObject
                            runOnUiThread {
                                hasUpdate = existsUpdate()
                                titleTextView.text = if (hasUpdate) {
                                    getString(R.string.update_found_title)
                                } else {
                                    getString(R.string.update_not_found_title)
                                }
                                Thread.sleep(1000) // Keep your delay if needed
                                updateUiElements(false)
                            }
                        } catch (e: Exception) {
                            Log.e("PHH", "Error parsing OTA info", e)
                            runOnUiThread {
                                hasUpdate = false
                                titleTextView.text = getString(R.string.update_not_found_title)
                                updateUiElements(false)
                            }
                        }
                    } else {
                        runOnUiThread {
                            hasUpdate = false
                            titleTextView.text = getString(R.string.update_not_found_title)
                            updateUiElements(false)
                        }
                    }
                }
            })
        } else {
            hasUpdate = false
            titleTextView.text = getString(R.string.update_not_found_title)
            updateUiElements(false)
        }
    }

    private fun updateUiElements(wasUpdated: Boolean) {
        val btnUpdate = findViewById<Button>(R.id.btn_update)
        val update_title = findViewById(R.id.txt_update_title) as TextView
        val currentBuildTextView = findViewById<TextView>(R.id.txt_current_build)
        val updateAvailableTextView = findViewById<TextView>(R.id.txt_update_available)
        val updateAvailableContainer = findViewById<View>(R.id.update_available_container)
        val updateAvailableChangelog = findViewById<View>(R.id.update_available_changelog)

        if (!wasUpdated) {
            btnUpdate.visibility = View.VISIBLE
        }

        // Current build info
        val currentBuildText = """
        ${getGSIName()}
        ${getBuildDate()}
        
        Android version: ${getAndroidVersion()}
        Build variant: ${getVariant()}
        Security patch: ${getPatchDate()}
    """.trimIndent()

        currentBuildTextView.text = currentBuildText

        if (hasUpdate) {
            updateAvailableContainer.visibility = View.VISIBLE
            updateAvailableChangelog.visibility = if (getChangelogUrl().isNotEmpty()) View.VISIBLE else View.GONE

            val updateText = """
            ${getGSIName()}
            ${getOtaDate()}
            
            Android version: ${getAndroidVersion()}
            Build variant: ${getBuildVariant()}
            Security patch: ${getPatchDate()}
            Image size: ${getUpdateSize()}
        """.trimIndent()

            updateAvailableTextView.text = updateText

            update_title.text = getString(R.string.update_found_title)
            btnUpdate.text = getString(R.string.update_found_button)
        } else {
            updateAvailableContainer.visibility = View.GONE
            update_title.text = getString(R.string.update_not_found_title)
            btnUpdate.text = getString(R.string.update_not_found_button)
        }

        if (getChangelogUrl().isNotEmpty()) {
            loadChangelog()
        } else {
            updateAvailableChangelog.visibility = View.GONE
        }
    }

    private fun loadChangelog(){
        val changelogContainer = findViewById<LinearLayout>(R.id.update_available_changelog)
        changelogContainer.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(getChangelogUrl())
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                val content = connection.inputStream.bufferedReader().use { it.readText() }
                connection.disconnect()

                withContext(Dispatchers.Main) {
                    changelogContainer.visibility = View.VISIBLE
                    while (changelogContainer.childCount > 1) {
                        changelogContainer.removeViewAt(1)
                    }
                    parseChangelog(content, changelogContainer)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    while (changelogContainer.childCount > 1) {
                        changelogContainer.removeViewAt(1)
                    }
                }
            }
        }
    }

    private fun parseChangelog(rawText: String, container: LinearLayout) {
        val inflater = LayoutInflater.from(this)
        val entries = rawText.split("\n\n")

        entries.forEach { entry ->
            val lines = entry.trim().split("\n")
            var title: String? = null
            val summaryItems = mutableListOf<Pair<String, Boolean>>()

            lines.forEach { line ->
                when {
                    line.startsWith("[Title]") -> title = line.removePrefix("[Title]").trim()
                    line.startsWith("[Summary]") -> summaryItems.add(Pair(line.removePrefix("[Summary]").trim(), false))
                    line.startsWith("[SummaryTitle]") -> summaryItems.add(Pair(line.removePrefix("[SummaryTitle]").trim(), true))
                }
            }

            if (!title.isNullOrEmpty() && summaryItems.isNotEmpty()) {
                inflater.inflate(R.layout.changelog_title, container, false).apply {
                    findViewById<TextView>(R.id.title_text).text = title
                    container.addView(this)
                }

                summaryItems.forEach { (summary, isBold) ->
                    inflater.inflate(R.layout.changelog_summary, container, false).apply {
                        val textView = findViewById<TextView>(R.id.summary_text)

                        if (isBold) {
                            val spannable = SpannableString(summary)
                            spannable.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                summary.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            textView.text = spannable
                        } else {
                            textView.text = if (summary.isBlank()) {
                                "" // keep empty line
                            } else {
                                "• $summary"
                            }
                        }
                        container.addView(this)
                    }
                }
            }
        }
    }

    private fun getAndroidVersion() : String {
        return SystemProperties.get("ro.system.build.version.release")
    }

    private fun getPatchDate() : String {
        val patchDate = SystemProperties.get("ro.build.version.security_patch")
        Log.e("PHH", "Security patch date: " + patchDate)
        val localDate = LocalDate.parse(patchDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
    }

    private fun getBuildDate() : String {
        val buildDate = SystemProperties.get("ro.system.build.date.utc")
        Log.e("PHH", "Build date: $buildDate")
        val buildDateLong = buildDate.toLong()
        return unixToHumanReadable(buildDateLong)
    }

    private fun getOtaDate(): String {
        if (otaJson.length() > 0) {
            val unixSeconds = otaJson.getLong("date")
            return unixToHumanReadable(unixSeconds)
        }
        return "Unknown"
    }

    private fun unixToHumanReadable(unixSeconds: Long): String {
        return try {
            val instant = Instant.ofEpochSecond(unixSeconds)
            val localTime = instant.atZone(ZoneId.systemDefault())
            DateTimeFormatter
                .ofPattern("MMM dd, yyyy hh:mm a")
                .format(localTime)
        } catch (e: Exception) {
            Log.e("PHH", "Error parsing update date", e)
            "Invalid date"
        }
    }

    private fun deletePackageCache() {
        val PackageCache = File("/data/system/package_cache")
        try {
            PackageCache.deleteRecursively()
            Log.d("PHH", "Deleted package_cache successfully.")
            Toast.makeText(this, R.string.toast_delete_cache, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, R.string.toast_reboot, Toast.LENGTH_LONG).show();
        } catch (e: Exception) {
            Log.e("PHH", "Failed deleting package_cache. Error: " + e.toString(), e)
        }
    }

    private fun getGSIName() : String {
        if (otaJson.length() > 0) {
            return otaJson.getString("gsi")
        }
        Log.e("PHH", "OTA json is empty")
        return ""
    }

    private fun getChangelogUrl(): String {
        return try {
            if (otaJson.length() > 0 && otaJson.has("changelog")) {
                otaJson.getString("changelog").takeIf { it.isNotEmpty() } ?: ""
            } else {
                Log.e("PHH", "OTA json is empty or missing changelog field")
                ""
            }
        } catch (e: Exception) {
            Log.e("PHH", "Error getting changelog URL", e)
            ""
        }
    }

    private fun getBuildVariant() : String {
        if (otaJson.length() > 0) {
            var otaVariants = otaJson.getJSONArray("variants")
            Log.e("PHH", "OTA variants found: " + otaVariants.length())
            for (i in 0 until otaVariants.length()) {
                val otaVariant = otaVariants.get(i) as JSONObject
                val otaVariantName = otaVariant.getString("name")
                if (otaVariantName == getVariant()) {
                    return otaVariantName;
                }
            }
        }
        Log.e("PHH", "OTA json is empty")
        return ""
    }

    private fun getUpdateSize() : String {
        if (otaJson.length() > 0) {
            var otaVariants = otaJson.getJSONArray("variants")
            Log.e("PHH", "OTA variants found: " + otaVariants.length())
            for (i in 0 until otaVariants.length()) {
                val otaVariant = otaVariants.get(i) as JSONObject
                val otaVariantName = otaVariant.getString("name")
                Log.e("PHH", "OTA variant found: " + otaVariantName)
                if (otaVariantName == getVariant()) {
                    Log.e("PHH", "OTA variant is the same")
                    val otaSize = otaVariant.getString("size")
                    Log.e("PHH", "OTA variant size: " + otaSize)
                    return Formatter.formatShortFileSize(
                        this.applicationContext,
                        otaSize.toLong()
                    )
                }
            }
        } else {
            Log.e("PHH", "OTA json is empty")
        }
        return ""
    }

    private fun isDynamic() : Boolean {
        val isDynamic = SystemProperties.get("ro.boot.dynamic_partitions")
        if (isDynamic != "true") {
            Log.e("PHH", "Device is not dynamic")
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.error))
            builder.setMessage(getString(R.string.dynamic_device_message))
            builder.setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.show()
            return false
        }
        Log.e("PHH", "Device is dynamic")
        return true
    }

    private fun isMagiskInstalled() {
        val magiskBin = File("/system/bin/magisk")
        if (magiskBin.exists()) {
            Log.e("PHH", "Magisk is installed")
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.warning))
            builder.setMessage(getString(R.string.magisk_exists_message))
            builder.setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.show()
        }
    }

    private fun existsUpdate() : Boolean {
        if (otaJson.length() > 0) {
            var otaDate = otaJson.getString("date")
            Log.e("PHH", "OTA image date: " + otaDate)
            val buildDate = SystemProperties.get("ro.system.build.date.utc")
            Log.e("PHH", "System image date: " + buildDate)
            if (otaDate > buildDate) {
                Log.e("PHH", "System image date is newer than the currently installed")
                return true
            }
            Log.e("PHH", "System image date is older than the currently installed")
        } else {
            Log.e("PHH", "OTA json is empty")
        }
        return false
    }

    private fun getVariant() : String {
        var buildvariant = SystemProperties.get("persist.sys.phh.buildvariant")
        Log.e("PHH", "Device variant is: " + buildvariant)
        return buildvariant
    }

    private fun getUrl() : String {
        if (otaJson.length() > 0) {
            var otaVariants = otaJson.getJSONArray("variants")
            Log.e("PHH", "OTA variants found: " + otaVariants.length())
            for (i in 0 until otaVariants.length()) {
                val otaVariant = otaVariants.get(i) as JSONObject
                val otaVariantName = otaVariant.getString("name")
                Log.e("PHH", "OTA variant found: " + otaVariantName)
                if (otaVariantName == getVariant()) {
                    val url = otaVariant.getString("url")
                    Log.d("PHH", "OTA URL: " + url)
                    return url
                }
            }
        } else {
            Log.e("PHH", "OTA json is empty")
        }
        return ""
    }

    private fun downloadUpdate() {
        val progress_bar = findViewById<LinearProgressIndicator>(R.id.progress_horizontal)
        val progress_text = findViewById(R.id.progress_value) as TextView

        val btn_update = findViewById(R.id.btn_update) as Button
        btn_update.setVisibility(View.INVISIBLE)

        val url = getUrl()
        if (url.isEmpty()) {
            Log.d("PHH", "Empty URL")
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.error))
            builder.setMessage(getString(R.string.update_error_message))
            builder.setPositiveButton(android.R.string.ok) { _, _ -> }
            builder.show()
            isUpdating = false
            hasUpdate = false
            updateUiElements(false)
        } else {
            Log.d("PHH", "Got URL: " + url)
            try {
                thread {
                    var httpsConnection = URL(url).openConnection() as HttpsURLConnection
                    if (httpsConnection.responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                        httpsConnection.responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                        httpsConnection.responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                        val newUrl = httpsConnection.getHeaderField("Location")
                        httpsConnection = URL(newUrl).openConnection() as HttpsURLConnection
                    }
                    val completeFileSize = httpsConnection.contentLengthLong
                    Log.d("PHH", "Download size is: " + completeFileSize)
                    httpsConnection.inputStream.use { httpStream ->
                        var hasSuccess = false
                        try {
                            Log.e("PHH", "OTA image install started")
                            prepareOTA()
                            Log.e("PHH", "New slot created")
                            Log.e("PHH", "OTA image extracting")
                            extractUpdate(httpStream, completeFileSize)
                            Log.e("PHH", "OTA image extracted")
                            applyUpdate()
                            Log.e("PHH", "Slot switch made")
                            Log.e("PHH", "OTA image install finished")
                            hasSuccess = true
                        } catch (e: Exception) {
                            Log.e("PHH", "Failed applying OTA image. Error: " + e.toString(), e)
                        }
                        runOnUiThread(Runnable {
                            val builder = MaterialAlertDialogBuilder(this)
                            if (hasSuccess) {
                                Toast.makeText(this, R.string.toast_install_done, Toast.LENGTH_LONG).show();
                                builder.setTitle(getString(R.string.ota_title))
                                builder.setMessage(getString(R.string.success_install_message))
                                deletePackageCache()
                            } else {
                                progress_bar.setVisibility(View.GONE)
                                progress_text.setVisibility(View.GONE)
                                builder.setTitle(getString(R.string.error))
                                builder.setMessage(getString(R.string.failed_install_message))
                                Toast.makeText(this, R.string.toast_install_fail, Toast.LENGTH_SHORT).show();
                            }
                            builder.setPositiveButton(android.R.string.ok) { _, _ -> }
                            builder.show()
                            isUpdating = false
                            hasUpdate = false
                            updateUiElements(true)
                        })
                    }
                }
            } catch (e: Exception) {
                Log.e("PHH", "Failed downloading OTA image. Error: " + e.toString(), e)
                Toast.makeText(this, R.string.toast_download_fail, Toast.LENGTH_SHORT).show();
                progress_bar.setVisibility(View.GONE)
                progress_text.setVisibility(View.GONE)
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(getString(R.string.error))
                builder.setMessage(getString(R.string.failed_download_message))
                builder.setPositiveButton(android.R.string.ok) { _, _ -> }
                builder.show()
                isUpdating = false
                hasUpdate = false
                updateUiElements(false)
            }
        }
    }

    private fun prepareOTA() {
        val progress_bar = findViewById<LinearProgressIndicator>(R.id.progress_horizontal)
        val progress_text = findViewById(R.id.progress_value) as TextView
        val update_title = findViewById(R.id.txt_update_title) as TextView

        runOnUiThread(Runnable {
            update_title.text = getString(R.string.preparing_update_title)
            progress_bar.isIndeterminate = true
            progress_text.text = "Preparing storage for OTA..."
            progress_bar.setVisibility(View.VISIBLE)
            progress_text.setVisibility(View.VISIBLE)
        })

        SystemProperties.set("ctl.start", "phh-ota-make")
        Thread.sleep(1000)

        while (!SystemProperties.get("init.svc.phh-ota-make", "").equals("stopped")) {
            val state = SystemProperties.get("init.svc.phh-ota-make", "not-defined")
            Log.d("PHH", "Current value of phh-ota-make svc is " + state)
            Thread.sleep(100)
        }
    }

    private fun extractUpdate(stream: InputStream, completeFileSize: Long) {
        val progress_bar = findViewById<LinearProgressIndicator>(R.id.progress_horizontal)
        val progress_text = findViewById(R.id.progress_value) as TextView
        val update_title = findViewById(R.id.txt_update_title) as TextView

        runOnUiThread(Runnable {
            Toast.makeText(this, R.string.toast_download_start, Toast.LENGTH_SHORT).show();
            update_title.text = getString(R.string.downloading_update_title)
            progress_bar.isIndeterminate = false
            progress_bar.setProgressCompat(0, false)
            progress_text.text = "Downloading 0%"
            progress_bar.setVisibility(View.VISIBLE)
            progress_text.setVisibility(View.VISIBLE)
        })

        val xzOut = XZInputStream(object: InputStream() {
            var nBytesRead = 0L
            override fun read(): Int {
                nBytesRead++
                return stream.read()
            }

            override fun available(): Int {
                return stream.available()
            }

            override fun close() {
                return stream.close()
            }

            override fun mark(readlimit: Int) {
                return stream.mark(readlimit)
            }

            override fun markSupported(): Boolean {
                return stream.markSupported()
            }

            override fun read(b: ByteArray?): Int {
                val n = stream.read(b)
                nBytesRead += n
                return n
            }

            override fun read(b: ByteArray?, off: Int, len: Int): Int {
                val n = stream.read(b, off, len)
                nBytesRead += n
                var extProgress = (100 * nBytesRead) / completeFileSize
                runOnUiThread(Runnable {
                    if (extProgress < 100) {
                        progress_bar.setProgressCompat(extProgress.toInt(), true)
                        progress_text.text = "Downloading " + extProgress.toInt().toString() + "%"
                    }
                })
                return n
            }

            override fun reset() {
                return stream.reset()
            }

            override fun skip(n: Long): Long {
                return stream.skip(n)
            }
        })
        FileOutputStream("/dev/phh-ota").use { blockDev ->
            val extBuf = ByteArray(1024 * 1024)
            var totalWritten = 0L
            while (true) {
                val extRead = xzOut.read(extBuf)
                if (extRead == -1) break
                blockDev.write(extBuf, 0, extRead)
                totalWritten += extRead
                Log.d("PHH", "Total written to block dev is " + totalWritten)
            }
        }

        runOnUiThread(Runnable {
            progress_bar.setProgressCompat(100, true)
            progress_text.text = "100%"
        })
    }

    private fun applyUpdate() {
        val progress_bar = findViewById<LinearProgressIndicator>(R.id.progress_horizontal)
        val progress_text = findViewById(R.id.progress_value) as TextView
        val update_title = findViewById(R.id.txt_update_title) as TextView

        runOnUiThread(Runnable {
            Toast.makeText(this, R.string.toast_download_finished, Toast.LENGTH_SHORT).show();
            update_title.text = getString(R.string.applying_update_title)
            progress_text.text = "Switching slot..."
            progress_bar.setVisibility(View.VISIBLE)
            progress_text.setVisibility(View.VISIBLE)
        })

        SystemProperties.set("ctl.start", "phh-ota-switch")
        Thread.sleep(1000)

        while (!SystemProperties.get("init.svc.phh-ota-switch", "").equals("stopped")) {
            val state = SystemProperties.get("init.svc.phh-ota-switch", "not-defined")
            Log.d("PHH", "Current value of phh-ota-switch svc is " + state)
            Thread.sleep(1000)
        }

        runOnUiThread(Runnable {
            progress_text.text = "Switched slot. OTA finalized."
        })
    }
}