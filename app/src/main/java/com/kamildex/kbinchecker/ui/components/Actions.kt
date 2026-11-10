package com.kamildex.kbinchecker.ui.components

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun copyText(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    // Android 13 and newer show their own confirmation.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }
}

/** Text currently on the clipboard, or an empty string. */
fun readClipboard(context: Context): String {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return ""
    return try {
        val clip = clipboard.primaryClip
        if (clip == null || clip.itemCount == 0) "" else clip.getItemAt(0).coerceToText(context).toString()
    } catch (e: Exception) {
        ""
    }
}

fun shareText(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    try {
        context.startActivity(Intent.createChooser(send, null))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show()
    }
}

fun openUrl(context: Context, url: String): Boolean = try {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    true
} catch (e: ActivityNotFoundException) {
    false
}

fun timeAgo(timestamp: Long, now: Long = System.currentTimeMillis()): String {
    val seconds = (now - timestamp) / 1000
    return when {
        seconds < 60 -> "Just now"
        seconds < 3600 -> "${seconds / 60}m ago"
        seconds < 86400 -> "${seconds / 3600}h ago"
        seconds < 604800 -> "${seconds / 86400}d ago"
        else -> SimpleDateFormat("d MMM yyyy", Locale.US).format(Date(timestamp))
    }
}

fun fullDate(timestamp: Long): String =
    SimpleDateFormat("d MMM yyyy, HH:mm", Locale.US).format(Date(timestamp))
