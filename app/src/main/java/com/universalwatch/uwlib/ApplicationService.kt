package com.universalwatch.uwlib

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.content.ComponentName
import android.content.Context

import android.content.ServiceConnection



/**
 * Created by emile on 24.11.2017.
 */

open class ApplicationService:Service(){
    companion object {
        var wasInitialized = false
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_REDELIVER_INTENT
    }
    open fun initialize(){

    }
    fun postInitialize(app:Application){

        wasInitialized = true
    }

    override fun onCreate() {
        initialize()

        super.onCreate()
    }


}