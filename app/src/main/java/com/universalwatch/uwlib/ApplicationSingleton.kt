package com.universalwatch.uwlib

import android.content.Context
import android.util.Log

abstract class ApplicationSingleton(){
    protected var wasInitialized=false
    var app: Application?=null
    fun forceRecreation(context: Context){
        wasInitialized=false
        __createApplication(context)
        wasInitialized=true
    }
    protected inline fun creation(f:()->Unit){
        Log.d("b", "before")
        if (!wasInitialized ) {
            f()
            wasInitialized = true
        }
        Log.d("b", "after")
    }
    private fun __createApplication(context: Context)=creation{
        if (isWatchAvailable(context))
            createApplication(context)
    }
    open fun createApplication(context: Context){

    }
    fun getApplication(context: Context):Application{
        if (wasInitialized) {
            Log.d("b", "was Initialized")
        }
        else{
            Log.d("b", "was not Initialized")
            __createApplication(context)
        }
        return app!!
    }
}