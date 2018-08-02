package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Debug
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by emile on 08.02.2018.
 */
object Watch {
    private var info: WatchInfo? = null
    private lateinit var returnReceiver: retRecv


    suspend fun downloadInfo(context: Context,packageName:String): WatchInfo? {

        var i = IntentFilter(packageName + ".UniversalWatch.TempRecv")
        returnReceiver = retRecv()
        context.registerReceiver(returnReceiver, i)

        var intent = Intent("UniversalWatch.WatchInfoServer")
        intent.putExtra("sourcePackage", packageName)
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        context.sendBroadcast(intent)
        var watchInfo:WatchInfo?=null
        //TODO: is runBlocking fine there?!

            var response = getLastResponse(timeout = 1000)
            val data = response?.getString("data")
            var jsonData: JSONObject? = null
            //if something was received
            data?.let {


                try {
                    jsonData = JSONObject(data)
                } catch (exc: Exception) { //when there is nothing to be parsed, leave it as null

                }
                jsonData?.let {
                    val array = jsonData!!.getJSONArray("requirements")
                    val reqs = mutableListOf<Requirements>()
                    //from json to enum
                    for (i in (0..array!!.length() - 1)) {
                        reqs.add(Requirements.valueOf(array.getString(i)))
                    }
                    jsonData?.let {
                        val modelName = try{it.getString("modelName")}catch (e:JSONException){""}
                        val manufacturer = try{it.getString("manufacturer")}catch (e:JSONException){""}
                        val dataTransferMode = try{it.getString("dataTransferMode")}catch (e:JSONException){"json"}
                        val requirements = reqs.toList()
                        val extraModelInfo =  try{it.getString("extraModelInfo")}catch (e:JSONException){""}
                        val firmwareVersion =  try{it.getString("firmwareVersion")}catch (e:JSONException){""}
                        val bufferKbSize = try{it.getInt("bufferKbSize")}catch (e:JSONException){0}
                        val screenUpdates = try{it.getBoolean("screenUpdates")}catch (e:JSONException){true}

                        val isBufferLimited = (bufferKbSize > 0)
                        watchInfo = WatchInfo(
                                modelName,
                                manufacturer,
                                requirements,
                                extraModelInfo,
                                firmwareVersion,
                                isBufferLimited,
                                bufferKbSize,
                                dataTransferMode,
                                screenUpdates
                        )

                    }

                }
            }
            context.unregisterReceiver(returnReceiver)


            return watchInfo


    }

    //I did not want to do that THIS WAY. GOD< PLZ EXCUSE ME
    private suspend fun getLastResponse(timeout: Long = 10000): Bundle? {
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

    class retRecv : BroadcastReceiver() {
        var last: Bundle? = null
        override fun onReceive(p0: Context?, p1: Intent?) {
            last = p1!!.extras
        }
    }
}

data class WatchInfo(
        var modelName: String,
        var manufacturer: String,
        var availableRequirements: List<Requirements>,
        var extraModelInfo:String,
        var firmwareVersion:String,
        var isBufferLimited:Boolean,
        var bufferKbSize:Int = 0,
        var dataTransferMode:String="json",
        var screenUpdates:Boolean = true
) {}
