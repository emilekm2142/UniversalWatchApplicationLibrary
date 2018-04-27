package com.universalwatch.uwlib

import org.json.JSONObject

/**
 * Created by emile on 14-Nov-17.
 */
class Action(
        var callbackFunction:(String)->Unit,
        var callback:String,
        var name:String,
        var extras:String
){
    init{

    }
    fun toJson(): JSONObject {
        var out = JSONObject()
        out.put("name",name)
        out.put("type","action")
        out.put("callback",callback)
        out.put("extras",extras)
        return out
    }
}
