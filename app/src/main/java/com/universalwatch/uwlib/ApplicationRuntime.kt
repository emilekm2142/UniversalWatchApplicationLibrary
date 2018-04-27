package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

/**
 * Created by emile on 29.01.2018.
 */
open class ApplicationRuntime :BroadcastReceiver() {
    companion object {
        var wasCreated = false
        lateinit private var app: Application
        fun sendDataToApplication(context: Context, d:Bundle){
            var i = Intent(context.packageName + ".UniversalWatch")
            i.setPackage(context.packageName) //TODO: check if packageName is the name of THIS or the sender package
            i.putExtra("bundle",d)
            context.sendBroadcast(i)
        }


    }
    fun create(context: Context) {
        if (!wasCreated) {
            app = makeApplication(context)
            launch{
                val response = app.install(context,false,true)
                //TODO: check if was installed
            }
            //tutaj robisz aplikację
            wasCreated = true
        }

    }
    fun getApplication(context: Context):Application{
        if (wasCreated){

        }
        else{
            create(context)
        }
        return app
    }
    open fun makeApplication(context: Context):Application{
        return Application(context, "notes app", listOf(Requirements.images, Requirements.color))
    }
    override fun onReceive(p0: Context?, p1: Intent?) {
        val context = p0!!
        val type = BroadcastTypes.valueOf(p1!!.getStringExtra("type"))
        create(context.applicationContext);
        when (type) {
           BroadcastTypes.APPLICATION_OPEN-> {
                app.onOpen(context.applicationContext,JSONObject(p1!!.getStringExtra("data")))
            }
            //on action
            BroadcastTypes.ACTION-> {
                app.onAction(context.applicationContext,JSONObject(p1!!.getStringExtra("data")))
            }
            BroadcastTypes.APPLICATION_CLOSE->{
                app.onClose(context.applicationContext,JSONObject(p1!!.getStringExtra("data")))
            }
            BroadcastTypes.APPLICATION_INSTALL->{
                app.install(context.applicationContext,true,false)
            }


        }
    }
}