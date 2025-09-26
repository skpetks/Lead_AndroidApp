package com.innovu.visitor.utlis


import android.app.Application
import android.util.Log
import com.chibatching.kotpref.Kotpref
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.innovu.visitor.services.SignalRManager
import timber.log.Timber

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Timber.plant(Timber.DebugTree())
//        if(StorePrefData.UserIId>0){
//            SignalRManager.initConnection()
//        }
    }
}