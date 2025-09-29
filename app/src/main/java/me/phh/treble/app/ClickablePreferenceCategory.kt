package me.phh.treble.app

import android.content.Context
import android.util.AttributeSet
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ClickablePreferenceCategory(context: Context, attrs: AttributeSet) : PreferenceCategory(context, attrs) {

    init {
        isSelectable = true
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        holder.itemView.setOnClickListener {
            showCategoryInfoDialog()
        }

        holder.itemView.isClickable = true
        holder.itemView.isFocusable = true
    }

    private fun showCategoryInfoDialog() {
        val message = when (key) {
            "device_rotation_category" -> R.string.device_rotation
            else -> R.string.default_category
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}