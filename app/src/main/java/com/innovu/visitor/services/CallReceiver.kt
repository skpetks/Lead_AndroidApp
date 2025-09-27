package com.innovu.visitor.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.innovu.visitor.MainActivity

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
        val number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                Log.d("CallReceiver", "Incoming call ringing from: $number")
            }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                Log.d("CallReceiver", "Call started, start recording")
                context.startService(Intent(context, CallRecordingService::class.java))
            }
            TelephonyManager.EXTRA_STATE_IDLE -> {
                Log.d("CallReceiver", "Call ended, stop recording")
                context.stopService(Intent(context, CallRecordingService::class.java))

                val broadcastIntent = Intent("CALL_ENDED_ACTION")
                broadcastIntent.putExtra("phoneNumber", number)
//                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent)


                val activityIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("CALL_ENDED", true)
                    putExtra("PHONE_NUMBER", number)
                }
                context.startActivity(activityIntent)

            }
        }
    }
}
