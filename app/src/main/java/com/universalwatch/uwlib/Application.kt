package com.universalwatch.uwlib

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_RECEIVER_FOREGROUND
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
import android.content.pm.PackageManager
import android.os.Debug
import org.json.JSONArray


/**
 * Created by emile on 14-Nov-17.
 */


private val BROADCAST_ACTION_NAME = "UniversalWatch.SendData"
private val ACTION_NAME_ADDITION = ".UniversalWatch"

private val RETURN_BROADCAST_NAME = ".UniversalWatch.Return"

open class Application(
        context: Context,
        var friendlyName: String,
        var requirements: List<Requirements>,
        var icon: Uri = Uri.EMPTY
) {

    private var appPackageName: String
    private var receiverActionName: String
    lateinit var initialView: View
    lateinit private var returnReceiver: ReturnReceiver
    var actualActions: HashMap<Action, () -> Unit> = hashMapOf()
    var usesVoice = false
    var voicePhrases = mutableListOf<String>()
    var views: MutableList<View> = mutableListOf()
    lateinit var currentView: View
    open var onOpen: (Context, JSONObject) -> Unit = { context, jsonObject -> this.showView(context, initialView) }
    open var onAction: (Context, JSONObject) -> Unit = { context, jsonObject ->
        val callbackName = jsonObject!!.getString("callbackName")
        val extras = jsonObject!!.getJSONObject("extras")
        executeAction(callbackName, extras)
    }
    open var onListClick: (context: Context, Int, String) -> Unit = { context, id, jsonObject -> Log.d("aa", "nothing changed!") }
    open var onClose: (Context, JSONObject) -> Unit = { context, jsonObject -> close(context) }

    init {

        appPackageName = context.packageName
        receiverActionName = appPackageName + ACTION_NAME_ADDITION + "." + friendlyName.replace(' ', '_')
        startReceivers(context)
        //install(context,false,false)
    }
    //configurin a actionsReceiver

    private fun startReceivers(context: Context) {


        returnReceiver = ReturnReceiver()
        val i2 = IntentFilter(appPackageName + RETURN_BROADCAST_NAME + "." + friendlyName.replace(' ', '_'))
        context.registerReceiver(returnReceiver, i2)

    }

    private fun sendJSONObject(j: JSONObject, type: BroadcastTypes, context: Context, extras: Bundle = Bundle(), shouldReturn: Boolean = false): String? {
        var i = Intent(BROADCAST_ACTION_NAME)
        val inString = j.toString()
        i.putExtra("sourceApp", friendlyName)
        i.putExtra("type", type.name)
        i.putExtra("data", inString)
        i.setFlags(FLAG_RECEIVER_FOREGROUND)
        i.extras.putAll(extras)
        i.setPackage(getInstalledPackage(context)) //make sure only the app receives!
        context.sendBroadcast(i)

        if (shouldReturn) {
            return runBlocking { getLastResponse() }
        } else {
            return null
        }
    }

    suspend fun getLastResponse(timeout: Long = 10000): String? {
        //block
        var returnNothing = false
        Timer().schedule(object : TimerTask() {
            override fun run() {
                returnNothing = true // this code will be executed after 2 seconds
            }
        }, timeout)
        while (returnReceiver!!.last == null && !returnNothing) {
            delay(50, TimeUnit.MILLISECONDS)
        }
        val d = returnReceiver!!.last
        returnReceiver!!.last = null
        return d
    }

    fun updateView(context: Context, view:View) {
        showView(context, view,false,false,true)
    }

    fun executeAction(callbackName: String, extras: JSONObject) {
        var found:Action?=null
        for ((action, callback) in actualActions) {
            if (action.callback == callbackName) {
                found=action
                break
            }
        }
        found?.let{
            it.callbackFunction(IncomingExtras(extras)) //lambda
        }
    }

    fun replaceView(context: Context, viewName: String, newView: View, shouldReturn: Boolean = false): String? {
        val inJson = newView.asJson()
        val extras = Bundle()
        extras.putString("viewName", viewName)
        currentView = newView
        return sendJSONObject(inJson, BroadcastTypes.VIEW_SHOW, context, extras, shouldReturn = shouldReturn)
    }

    fun install(context: Context, forceReinstall: Boolean = false, shouldReturn: Boolean = false): String? {
        //requirements to strings
        val reqsJsonArray = JSONArray(requirements.map { it.toString() })
        val voicePhrasesJsonArray = JSONArray(voicePhrases)

        var inner = hashMapOf(
                "package" to appPackageName,
                "reintall" to forceReinstall,
                "name" to friendlyName,
                "package" to appPackageName,
                "icon" to icon.toString(),
                "usesEncryption" to false,
                "voicePhrases" to voicePhrasesJsonArray
        )
        var a = JSONObject(inner)
        a.put("requirements", reqsJsonArray)
        return sendJSONObject(a, BroadcastTypes.APPLICATION_INSTALL, context, shouldReturn = shouldReturn)
    }

    fun showView(context: Context, view: View, shouldReturn: Boolean = false, forceShow: Boolean = false, update:Boolean=false): String? {

        val inJson = view.asJson()
        currentView = view
        inJson.put("forceShow", forceShow)
        removeActions()
        bindActions(view)
        return sendJSONObject(inJson, if (!update) BroadcastTypes.VIEW_SHOW else BroadcastTypes.VIEW_UPDATE, context, shouldReturn = shouldReturn)
    }

    fun sendNotification(context: Context, notification: Notification) {

    }

    fun close(context: Context) {
        actualActions.clear()
        val data = JSONObject("""
            {"package":"${context.packageName}",
            "friendlyName":"${friendlyName}"
            }
        """.trimIndent())
        sendJSONObject(data, BroadcastTypes.APPLICATION_CLOSE, context)
    }

    fun bindAction(a: Action) {
        actualActions.put(a, { a.callbackFunction })
    }

    fun bindActions(view: View) {
        when (view){
            is TextView -> {
                for (action in view.actions) {
                    bindAction(action)
                }
            }
            is ListView ->{
                for (entry in view.elements){
                    entry.mainAction?.let{
                        bindAction(it)
                    }
                    entry.secondaryActions?.let {
                        for (action in it){
                            bindAction(action)
                        }
                    }
                }
            }
        }


    }

    fun removeActions() {
        actualActions.clear()
    }

    inner class ReturnReceiver : BroadcastReceiver() {
        var last: String? = null;
        override fun onReceive(p0: Context?, p1: Intent?) {
            last = p1!!.getStringExtra("data")
        }
    }

}