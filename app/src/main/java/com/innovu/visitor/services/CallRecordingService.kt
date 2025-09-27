package com.innovu.visitor.services

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import java.io.File

class CallRecordingService : Service() {
    private var recorder: MediaRecorder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val file = File(getExternalFilesDir(null), "call_${System.currentTimeMillis()}.mp3")

        Log.d("CallRecord File", "file:"+file.absolutePath)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        recorder?.apply {
            stop()
            release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
