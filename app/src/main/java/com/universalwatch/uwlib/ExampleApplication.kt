package com.universalwatch.uwlib

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Debug
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * Created by emile on 18.11.2017.
 */
//TODO: opisać ten niezwykle skomplikowany binder

class ExampleApplication:ApplicationService(){
    private val mBinder = LocalBinder()
    public var WatchApplication:Application?=null
    override fun initialize() {
        //Debug.waitForDebugger()
        Log.d("dasda","asads")
        WatchApplication = Application(applicationContext,"notes app", listOf(Requirements.images,Requirements.color))
        var actions = mutableListOf(Action({ extras-> exampleCallback(extras)},"sampleCallback","asdas", "dupeczka xd"))
        WatchApplication?.bindAction(actions[0])

        val exampleView = MediaView("sample", Uri.EMPTY, "hikaru nara", "ziom", "xd", "some shit",actions)
        WatchApplication?.showView(applicationContext,exampleView)

        //val exampleTextView = TextView("xd", "You got an update", "fine!", actions)
        //WatchApplication?.showView(applicationContext,exampleTextView)
        //non-blocking
        launch{
            val respo = WatchApplication!!.showView(applicationContext,exampleView,shouldReturn = true)
            //jak odpalić 2 na raz i zczytać w main?
        }
        postInitialize(WatchApplication!!)
    }

    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }
    fun exampleCallback(extras:String){
        Log.d("a",extras)
    }
    public inner class LocalBinder : Binder() {
        internal// Return this instance of LocalService so clients can call public methods
        val service: ExampleApplication
            get() = this@ExampleApplication
    }
}