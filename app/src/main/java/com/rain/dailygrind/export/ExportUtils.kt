package com.rain.dailygrind.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

const val EXPORT_W = 1080
const val EXPORT_H = 1350

fun saveToGallery(context: Context, bitmap: Bitmap, name: String): Uri? {
    val filename = "DailyGrind_$name.png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DailyGrind")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
    resolver.openOutputStream(uri)?.use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
    }
    return uri
}

fun cacheShareUri(context: Context, bitmap: Bitmap, name: String): Uri {
    val dir = File(context.cacheDir, "exports").apply { mkdirs() }
    val file = File(dir, "DailyGrind_$name.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

fun shareImage(context: Context, uri: Uri, toInstagram: Boolean) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (toInstagram) setPackage("com.instagram.android")
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Share DailyGrind"))
    } catch (_: Exception) {
        intent.setPackage(null)
        context.startActivity(Intent.createChooser(intent, "Share DailyGrind"))
    }
}
