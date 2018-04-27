package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.ActivityManager
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import android.support.v4.content.ContextCompat.startActivity




/**
 * Created by emile on 29.01.2018.
 */
private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

class ApplicationActivationListener : BroadcastReceiver() {

    //add this to manifest with the same name as in-app receiver
    override fun onReceive(p0: Context?, p1: Intent?) {
        var lastIntent:Intent?=null
        if (!isMyServiceRunning(p0!!, applicationClass as Class<ExampleApplication>) && lastIntent!=p1) {
            launch {
                applicationClass = ExampleApplication::class.java
                val intent = Intent(p0, applicationClass as Class<ExampleApplication>)
                p0.startService(intent)
                while (!ApplicationService.wasInitialized) { delay(10) }
                lastIntent=p1

                val d=p1!!.action
                val p = p0.packageName
                val u=p1!!.getPackage()
                val q=ApplicationService.wasInitialized

                val copyToForward= Intent(p1)


                    p0.sendBroadcast(copyToForward)

            }

        } else {
            //nothing
        }


    }


}