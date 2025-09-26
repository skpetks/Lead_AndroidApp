package com.innovu.visitor.services

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.innovu.visitor.data.api.RetrofitClient
import com.innovu.visitor.utlis.StorePrefData
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ImageUploadService : IntentService("ImageUploadService") {

    private val CHANNEL_ID = "upload_channel"
    private val NOTIFICATION_ID = 1001

    override fun onHandleIntent(intent: Intent?) {
        createNotificationChannel()
        showUploadingNotification()
        val imagePaths = intent?.getStringArrayListExtra("imagePaths")
        val visitorId = StorePrefData.visitorID.toString()
        val userId = StorePrefData.UserID
        val orgId = StorePrefData.OrgId.toString()

        val filesParts = imagePaths?.map { filePath ->
            val file = File(filePath)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", file.name, requestFile)
        } ?: emptyList()

        val orgPart = orgId.toRequestBody("text/plain".toMediaTypeOrNull())
        val visitorPart = visitorId.toRequestBody("text/plain".toMediaTypeOrNull())
        val userPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        runBlocking {
            try {
                val response = RetrofitClient.instance.uploadVisitorImages(
                    orgPart, visitorPart, userPart, filesParts
                )
                showUploadResult(response.isSuccessful)
                if (response.isSuccessful) {
                    Log.d("UploadService", "Uploaded: ${response.body()?.path}")
                } else {
                    showUploadResult(false)
                    Log.e("UploadService", "Upload failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                showUploadResult(false)
                Log.e("UploadService", "Exception: ${e.localizedMessage}")
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Upload Notification",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ForegroundServiceType")
    private fun showUploadingNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setContentTitle("Uploading images")
            .setContentText("Upload in progress...")
            .setOngoing(true)
            .setProgress(0, 0, true)

        startForeground(NOTIFICATION_ID, builder.build())
    }
    private fun showUploadResult(success: Boolean) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle("Upload ${if (success) "Successful" else "Failed"}")
            .setContentText(
                if (success) "Images uploaded successfully." else "Upload failed. Please try again."
            )
            .setAutoCancel(true)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
    }
}
