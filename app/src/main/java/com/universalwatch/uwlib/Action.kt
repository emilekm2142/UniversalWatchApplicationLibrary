package com.universalwatch.uwlib

import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by emile on 14-Nov-17.
 */
class Action(
        var callbackFunction:(IncomingExtras)->Unit,
        var name:String,
        extras:String="",
        advancedExtras: Extras?=null,
        callbackName:String?=null
){
    lateinit var callback:String
    lateinit var extras:Extras
    init{
        this.extras = Extras(extras)
        advancedExtras?.let{
            this.extras=advancedExtras
        }
        callback = name.toLowerCase().replace(" ", "_")
        callbackName?.let{
            callback = callbackName
        }
    }
    fun toJson(): JSONObject {
        var out = JSONObject()
        out.put("name",name)
        out.put("type","action")
        out.put("callback",callback)
        out.put("extras",extras.extras?:extras.getArray())
        return out
    }
}
class Extras(var extras: String? = null){
    private val json:JSONArray = JSONArray()
    fun addInput(id:String):Extras{
        json.put(JSONObject("""{type:"input", "name":"${id}"}"""))
        return this
    }

    fun finalize():Extras{
        return this
    }
     fun getArray():JSONArray{
        return json
    }

}
class IncomingExtras(var extras: JSONObject){
    fun getString():String?{
        if (extras.has("extra")){
            return extras.getString("extras")
        }
        else return null
    }
    fun getJson():JSONObject{
        return extras
    }
}