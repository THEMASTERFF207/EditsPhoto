package com.t8rin.imagetoolboxlite.core.domain.update

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.t8rin.imagetoolboxlite.core.domain.APP_RELEASES
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

data class UpdateInfo(
    val versionName: String,
    val versionCode: Long,
    val downloadUrl: String,
    val changelog: String
)

class UpdateChecker {

    suspend fun checkForUpdate(
        currentVersionName: String,
        currentVersionCode: Long
    ): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val apiUrl = "https://api.github.com/repos/THEMASTERFF207/EditsPhoto/releases/latest"
            val connection = URL(apiUrl).openConnection()
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            val response = connection.inputStream.bufferedReader().readText()
            val json = JSONObject(response)

            val tagName = json.getString("tag_name")
            val body = json.getString("body") ?: ""

            val assets = json.getJSONArray("assets")
            var apkDownloadUrl: String? = null

            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.getString("name")
                if (name.endsWith(".apk")) {
                    apkDownloadUrl = asset.getString("browser_download_url")
                    break
                }
            }

            if (apkDownloadUrl == null) return@withContext null

            val remoteVersionCode = parseVersionCode(tagName)
            val currentCode = currentVersionCode

            if (remoteVersionCode > currentCode) {
                UpdateInfo(
                    versionName = tagName,
                    versionCode = remoteVersionCode,
                    downloadUrl = apkDownloadUrl,
                    changelog = body
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseVersionCode(versionName: String): Long {
        val cleaned = versionName.replace(Regex("[^0-9.]"), "")
        val parts = cleaned.split(".")
        var code = 0L
        parts.forEach { part ->
            code = code * 100 + (part.toLongOrNull() ?: 0)
        }
        return code
    }
}

class ApkDownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == -1L) return

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor: Cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                val uri = Uri.parse(uriString)
                val file = File(uri.path!!)

                val installIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        ),
                        "application/vnd.android.package-archive"
                    )
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(installIntent)
            }
        }
        cursor.close()
    }
}

object AutoUpdateManager {
    private const val CHANNEL_ID = "update_channel"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Updates",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for app updates"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showUpdateNotification(
        context: Context,
        versionName: String,
        downloadId: Long
    ) {
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading update...")
            .setContentText("Edits Photo $versionName")
            .setProgress(100, 0, true)
            .setOngoing(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun showDownloadCompleteNotification(
        context: Context,
        versionName: String,
        file: File
    ) {
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(NotificationManager::class.java)

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                ),
                "application/vnd.android.package-archive"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            installIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Update ready to install")
            .setContentText("Edits Photo $versionName")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun downloadApk(
        context: Context,
        downloadUrl: String,
        versionName: String
    ): Long {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("Downloading Edits Photo")
            .setDescription("Version $versionName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "EditsPhoto-$versionName.apk"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        showUpdateNotification(context, versionName, 0)

        return downloadManager.enqueue(request)
    }
}
