package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.util.Log

/**
 * Created by emile on 29.01.2018.
 */
/*
object static{
    lateinit var app:Application
    var wasInit = false
}

class ExampleApplicationRuntime:ApplicationRuntime(){

    override fun makeApplication(context: Context): Application {
        //return application
        val wasInit  = static.wasInit
        if (!static.wasInit) {
            static.app = Application(context, "notes app", listOf(Requirements.images, Requirements.color))

            val actions = mutableListOf<Action>(
                    Action(
                            callbackFunction = { s: String ->
                                //some useful code here...
                                val b = s
                            },
                            callback = "action1",
                            name = "label1",
                            extras = "label1 extras"
                    )
            )
            val v: TextView = TextView(
                    name = "main",
                    major = "typieee",
                    minor = "to twoja ostatnia szansa",
                    actions = actions, onBack = {}
            )


            static.app.initialView = v;
            static.wasInit = true
        }
        return static.app;
    }


    //To receive broadcasts
    override fun onReceive(p0: Context?, p1: Intent?) {
        try {
            super.onReceive(p0, p1)
        }
        catch (e:java.lang.RuntimeException){
            //not a json Object!
            val a = getApplication(p0!!.applicationContext)
            val bundle = p1!!.getBundleExtra("bundle")

            //do something
        }
    }






}
class MusicReceiver:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        ApplicationRuntime.sendDataToApplication(p0!!.applicationContext, Bundle())
        //resend to main app
    }
}
        */