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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startIntent = Intent(packageName+".UniversalWatch.notes_app")
        startIntent.putExtra("type",BroadcastTypes.APPLICATION_OPEN.toString())
        startIntent.putExtra("data", JSONObject("{\"ziooom\":\"safas\"}").toString())

        sendBroadcast(startIntent)
        //must be ran with a little delay to make thing synchronius
        launch {
            delay(10000)
            val intent2 = Intent(packageName+".UniversalWatch.notes_app")
            intent2.putExtra("type", BroadcastTypes.SYSTEM_ACTION.toString())
            intent2.putExtra("data", JSONObject("""{"actionName":"next", "screen":"dasda"}""").toString())
            sendBroadcast(intent2)
        }

    }


}
