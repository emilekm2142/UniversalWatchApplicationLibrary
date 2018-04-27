package com.universalwatch.uwlib

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.EditText
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.json.JSONObject
import kotlin.reflect.KClass


class MainActivity : AppCompatActivity() {
    lateinit var appService:ExampleApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startIntent = Intent(packageName+".UniversalWatch")
        startIntent.putExtra("type",BroadcastTypes.APPLICATION_OPEN.toString())
        startIntent.putExtra("data",JSONObject("{\"ziooom\":\"safas\"}").toString())
        //sendBroadcast(startIntent)
        //must be ran with a little delay to make thing synchronius
        launch {
            delay(100)
            val intent2 = Intent("com.universalwatch.uwlib.costam")

            sendBroadcast(intent2)
        }


    }

    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as ExampleApplication.LocalBinder
            appService =binder.service

        }

        override fun onServiceDisconnected(arg0: ComponentName) {

        }
    }
}
