package com.universalwatch.uwlib

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

/**
 * Created by emile on 23.05.2018.
 */
private val allowedReceiverPackages = mutableListOf<String>(
        "io.universalwatch.universalwatchapplication"
)
fun updateAppList(){
    allowedReceiverPackages.add("new.package")
}
fun isWatchAvailable(context: Context):Boolean{
    return getInstalledPackage(context) != null
}
fun getInstalledPackage(context: Context):String? {
    for (pack in allowedReceiverPackages) {
        try {
            if (context.packageManager.getApplicationInfo(pack, 0).enabled) {
                return pack
            }
        } catch (e: PackageManager.NameNotFoundException) {

        }

    }
    return null
}
