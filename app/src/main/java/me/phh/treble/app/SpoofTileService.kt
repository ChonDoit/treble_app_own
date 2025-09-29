package me.phh.treble.app

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager

@RequiresApi(Build.VERSION_CODES.N)
class SpoofTileService : TileService() {

    override fun onStartListening() {
        updateTileState()
    }

    override fun onClick() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val current = prefs.getBoolean(SpoofSettingsPs.enable_ps_sdk, false)
        val newValue = !current

        prefs.edit().putBoolean(SpoofSettingsPs.enable_ps_sdk, newValue).apply()
        updateTileState()

        Toast.makeText(
            this,
            "Play Store SDK set to $newValue",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun updateTileState() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val enabled = prefs.getBoolean(SpoofSettingsPs.enable_ps_sdk, false)

        qsTile?.apply {
            state = if (enabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            subtitle = if (enabled) "On" else "Off"
            updateTile()
        }
    }
}