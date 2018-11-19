package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject

/**
 * Created by emile on 29.01.2018.
 */
/**
*Every single application has to derive from this class. Overall, it is a wrapper over a {@BroadcastReceiver} that makes it easier to use.
*A class deriving from this should override {@makeApplication} and return an application there. Note that it does not need to be created, just returned.
 */
open class ApplicationRuntime :BroadcastReceiver() {


    open fun makeApplication(context: Context):Application{
        return Application(context, "notes app", listOf(Requirements.images, Requirements.color))
    }

    /**
     * The routing, executing actions and callbacks happens there. It activates everytime a signal from the Watch comes to the phone.
     */
    override fun onReceive(p0: Context?, p1: Intent?) {
        val context = p0!!
       // val u=Toast.makeText(context,"adsdasdas", Toast.LENGTH_LONG)
      //  u.show()

        val type = BroadcastTypes.valueOf(p1!!.getStringExtra("type"))

        val app = makeApplication(context.applicationContext);
        Log.d("odebranko","odfebranko")
        Log.d("ofasfdsf", type.toString())
        when (type) {
           BroadcastTypes.APPLICATION_OPEN-> {
                app.onOpen(context.applicationContext,JSONObject(p1!!.getStringExtra("data")))
            }
            //on action
            BroadcastTypes.ACTION-> {
                app.onAction(context.applicationContext,JSONObject(p1!!.getStringExtra("data")))
            }
            BroadcastTypes.RESOURCE_REQUEST->{

                val data = JSONObject(p1!!.getStringExtra("data"))
                val resource = data.getString("resource")

                val publicUri = WatchUtils.getPublicUri(context, resource)
                var i = Intent("UniversalWatch.SendBinaryData")
                i.putExtra("sourceApp",app.friendlyName)
                i.putExtra("type",type.name)

                i.putExtra("data","""{"uri":"${publicUri}"""")
                i.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                i.setPackage(getInstalledPackage(context)) //make sure only the app receives!
                context.sendBroadcast(i)
            }
            BroadcastTypes.LIST_VIEW_CLICK->{
                val data = JSONObject(p1!!.getStringExtra("data"))
                app.onListClick(context.applicationContext, data.getInt("id"), data.getString("extras"))
            }
            BroadcastTypes.SYSTEM_ACTION-> {
                val data = JSONObject(p1!!.getStringExtra("data"))
                val actionType = data.getString("actionName")
                //val screenName = data.getString("screen")
                Log.d("t", actionType)
                //TODO: This will NOT work if the state is not saved. --PRIORITY CHECK
                //as for now, it works. Process is not killed by the system.
                when (actionType) {
                    "next" -> {
                       // app.currentView.systemCallbacks.onNext(context,screenName)
                    }
                    "previous" -> {
                     //   app.currentView.systemCallbacks.onPrev(context,screenName)
                    }
                    "back" -> {
                        app.currentView.systemCallbacks.onBack(context,"")//screenName)
                    }
                }
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