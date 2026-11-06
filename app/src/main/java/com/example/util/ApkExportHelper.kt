package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ApkExportHelper {

    /**
     * Copies the app's own installed APK binary into the public Downloads directory
     * so the user can easily install or distribute it directly from their device.
     */
    fun exportApkToPublicDownloads(context: Context): Result<String> {
        return try {
            val apkFile = File(context.packageCodePath)
            if (!apkFile.exists()) {
                return Result.failure(Exception("Base APK file not found at ${apkFile.absolutePath}"))
            }

            val fileName = "OmniSwarm-v${com.example.BuildConfig.VERSION_NAME}.apk"
            val mimeType = "application/vnd.android.package-archive"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return Result.failure(Exception("Failed to allocate file in Downloads"))

                resolver.openOutputStream(uri)?.use { outputStream ->
                    apkFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Result.success("Saved $fileName to your device Downloads folder!")
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                targetDir.mkdirs()
                val targetFile = File(targetDir, fileName)
                apkFile.copyTo(targetFile, overwrite = true)
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Exports a workflow deliverable summary and recipe text to Downloads.
     */
    fun exportTextToDownloads(context: Context, fileName: String, content: String): Result<String> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: return Result.failure(Exception("Could not create text file in Downloads"))

                resolver.openOutputStream(uri)?.use { out ->
                    out.write(content.toByteArray())
                }
                Result.success("Saved $fileName to your Downloads folder!")
            } else {
                val targetDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                targetDir.mkdirs()
                val targetFile = File(targetDir, fileName)
                targetFile.writeText(content)
                Result.success("Saved to ${targetFile.absolutePath}")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
