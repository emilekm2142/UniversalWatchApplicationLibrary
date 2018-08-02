package com.universalwatch.uwlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import kotlinx.coroutines.experimental.delay
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by emile on 23.05.2018.
 */
private val CHAR_LIST = "abcdef!@#$%^&*()-=+_|}{';,.<>?/`ghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"



private fun generateRandomString(length:Int): String {

    val randStr = StringBuffer()
    for (i in 0 until length) {
        val number = getRandomNumber()
        val ch = CHAR_LIST[number]
        randStr.append(ch)
    }
    return randStr.toString()
}


private fun getRandomNumber(): Int {
    var randomInt = 0
    val randomGenerator = Random()
    randomInt = randomGenerator.nextInt(CHAR_LIST.length)
    return if (randomInt - 1 == -1) {
        randomInt
    } else {
        randomInt - 1
    }
}


class BroadcastTransceiver{
    //pooling ftw!
    private var asyncRecv:InnerAsyncReceiver? = null
    private var lastMsg:String? = null
    suspend fun await(context: Context, action:String, payload:Bundle?=null, targetPackage:String?=null, timeout: Long=3000):Bundle?{
        var i = Intent(action)
        payload?.let {
            i.putExtras(payload)
        }
        targetPackage?.let {
            i.setPackage(it)
        }
        val innerAction = generateRandomString(15)
        i.putExtra("respondTo", innerAction)
        context.sendBroadcast(i)

        var intent = IntentFilter(innerAction)
        val recv = InnerBlockingReceiver()
        context.registerReceiver(recv, intent)

        var returnNothing = false
        Timer().schedule(object : TimerTask() {
            override fun run() {
                returnNothing=true // this code will be executed after 2 seconds
            }
        }, timeout)
        while (recv!!.last == null && !returnNothing){
            delay(50, TimeUnit.MILLISECONDS)
        }
        val d = recv!!.last
        recv!!.last=null
        context.unregisterReceiver(recv)
        return d
    }
    fun awaitCallback(callback:(Bundle)->Unit, context: Context, action:String, payload:Bundle?, targetPackage:String?=null)
    {
        var i = Intent(action)
        i.putExtras(payload)
        targetPackage?.let {
            i.setPackage(it)
        }
        val innerAction = generateRandomString(15)
        i.putExtra("respondTo", innerAction)
        context.sendBroadcast(i)

        var intent = IntentFilter(innerAction)
        asyncRecv = InnerAsyncReceiver()
        context.registerReceiver(asyncRecv, intent)
    }
    inner private class InnerBlockingReceiver:BroadcastReceiver(){
        var last:Bundle?=null

        override fun onReceive(p0: Context?, p1: Intent?) {
            last = p1!!.extras
        }
    }

    inner private class InnerAsyncReceiver:BroadcastReceiver(){
        var last:Bundle?=null
        var callback:(Bundle)->Unit = {}
        override fun onReceive(p0: Context?, p1: Intent?) {

            callback(p1!!.extras)
            p0!!.unregisterReceiver(asyncRecv!!)
            asyncRecv=null
        }
    }
}