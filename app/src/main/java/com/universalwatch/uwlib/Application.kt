package com.universalwatch.uwlib

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by emile on 14-Nov-17.
 */

/*
Convention: Functions there send only an inner body, without an outer parent
not:{
type:"view"
data:{SomeData}
}
but just {someData}
add any extra values to a seperate field in extras

example of getting the response ->
val ret = async { somthingThatReturnsResponse() }

 */

private var BROADCAST_ACTION_NAME = "UniversalWatch.SendData"
private var ACTION_NAME_ADDITION = ".UniversalWatch"

private val RETURN_BROADCAST_NAME = ".UniversalWatch.Return"
open class Application(
        context: Context,
        var friendlyName:String,
        var requirements: List<Requirements>,
        var icon:Uri = Uri.EMPTY
){
    private var appPackageName:String
    private var receiverActionName:String
    lateinit var initialView:View
    lateinit private var returnReceiver:ReturnReceiver
    var actualActions:HashMap<Action,()->Unit> = hashMapOf()
    var usesVoice =false
    var voicePhrases = mutableListOf<String>()
    var views:MutableList<View> = mutableListOf()

    open var onOpen: (Context, JSONObject) -> Unit = {context, jsonObject -> this.showView(context, initialView) }
    open var onAction: (Context, JSONObject)->Unit= {context, jsonObject ->
        val callbackName = jsonObject!!.getString("callbackName")
        executeAction(callbackName)}
    open var onClose: (Context,JSONObject)->Unit = {context, jsonObject -> close() }

    init {

        appPackageName = context.packageName
        receiverActionName = appPackageName + ACTION_NAME_ADDITION
        startReceivers(context)
    }
        //configurin a actionsReceiver

    private fun startReceivers(context: Context){
       // actionsReceiver = ActionsReceiver()
       // val i = IntentFilter(receiverActionName)
       // context.registerReceiver(actionsReceiver,i)

        returnReceiver = ReturnReceiver()
        val i2 = IntentFilter(  appPackageName +RETURN_BROADCAST_NAME)
        context.registerReceiver(returnReceiver,i2)

    }
     private fun sendJSONObject(j:JSONObject,type:BroadcastTypes,context: Context,extras:Bundle=Bundle(), shouldReturn:Boolean=false):String?{
        var i = Intent(BROADCAST_ACTION_NAME)
        val inString  = j.toString()
        i.putExtra("sourceApp",friendlyName)
        i.putExtra("type",type.name)
        i.putExtra("data",inString)
        i.extras.putAll(extras)
        i.setPackage("io.universalwatch.universalwatchapplication") //make sure only the app receives!
        context.sendBroadcast(i)

        if (shouldReturn) {
            return runBlocking {  getLastResponse() }
        }
        else{
            return null
        }
    }
    suspend fun getLastResponse(timeout:Long=3000):String?{
        //block
        var returnNothing = false
        Timer().schedule(object : TimerTask() {
           override fun run() {
                returnNothing=true // this code will be executed after 2 seconds
            }
        }, timeout)
        while (returnReceiver!!.last == null && !returnNothing){
            delay(50, TimeUnit.MILLISECONDS)}
        val d = returnReceiver!!.last
        returnReceiver!!.last=null
        return d
    }
    fun updateView(viewName:String, properties: HashMap<String,String>){

    }
    fun executeAction(callbackName:String) {


        //TODO: Replace with something more idiomatic
        //JEBAC xd
        //search through all the action and invoke its callback when name matches
        var found = false
        for ((action,callback) in actualActions) {
            if (action.callback == callbackName) {
                action.callbackFunction(action.extras) //lambda
                found=true
            }
        }
        if (!found){
            Log.d("aa","not found")
        }
    }
    fun replaceView(context: Context, viewName:String,newView:View, shouldReturn: Boolean=false):String?{
        val inJson = newView.asJson()
        val extras= Bundle()
        extras.putString("viewName",viewName)
        return sendJSONObject(inJson,BroadcastTypes.VIEW_SHOW,context,extras, shouldReturn=shouldReturn)
    }
    fun install(context: Context, forceReinstall:Boolean=false, shouldReturn: Boolean=false):String?{
        var inner = hashMapOf(
                "package" to appPackageName,
                "reintall" to forceReinstall,
                "name" to friendlyName,
                "package" to appPackageName,
                "icon" to icon.toString(),
                "usesEncryption" to false,
                "voicePhrases" to voicePhrases,
                "requirements" to requirements
        )
        var a=JSONObject(inner)
        return sendJSONObject(a, BroadcastTypes.APPLICATION_INSTALL, context,shouldReturn = shouldReturn)
    }
    fun showView(context:Context, view:View,shouldReturn: Boolean=false):String?{
        val inJson = view.asJson()
        return sendJSONObject(inJson,BroadcastTypes.VIEW_SHOW,context,shouldReturn = shouldReturn)
    }
    fun sendNotification(context:Context, notification:Notification){

    }
    fun close(){
        actualActions.clear()
    }
    fun bindAction(a:Action){
        actualActions.put(a,{a.callbackFunction})
    }
    fun removeActions(){
       actualActions.clear()
    }
    inner class ReturnReceiver:BroadcastReceiver(){
        var last:String?=null;
        override fun onReceive(p0: Context?, p1: Intent?) {
            last=p1!!.getStringExtra("data")
        }
    }
    inner class ActionsReceiver:BroadcastReceiver(){

        var initialized=false
        fun initialize(){

            initialized=true
        }
        override fun onReceive(p0: Context?, p1: Intent?) {
            val callbackName = p1!!.getStringExtra("callbackName")

            //TODO: Replace with something more idiomatic
            //JEBAC xd
            //search through all the action and invoke its callback when name matches
            var found = false
            for ((action,callback) in actualActions) {
                if (action.callback == callbackName) {
                    action.callbackFunction(action.extras) //lambda
                    found=true
                }
            }
            if (!found){
                Log.d("aa","not found")
            }
        }
    }
}