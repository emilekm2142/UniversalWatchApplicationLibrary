package com.universalwatch.uwlib

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI

/**
 *
tutaj będa kontrolki do robienia własnych widoków fast and easy
 */
enum class ListItemType{
    Text,  WithIcon, Menu
}
data class ListEntry( var layoutType: ListItemType, var lines:MutableList<String>, var icon:Uri?=null, var mainAction: Action?=null,var secondaryActions: MutableList<Action>?=null){
 fun toJSON():JSONObject{
     val ret = JSONObject()
     ret.put("lines", lines.toJsonWATCH())
     val secActions = JSONArray()
     secondaryActions?.let {
         for (d in it) {
             secActions.put(d.toJson())
         }
         ret.put("secondaryActions", secActions)
     }
    mainAction?.let {
        ret.put("mainAction", it.toJson())
    }
     icon?.let{
         ret.put("icon", icon.toString())
     }
     ret.put("layoutType", layoutType.toString())
     return ret
 }
}
class SystemActions( ){
    var onNext:(Context, String)->Unit = {c, s->} ;
    var onPrev:(Context,String)->Unit= {c,s->}
    var onBack:(Context,String)->Unit= {c,s->}
}

data class MessagingProfile(var name:String, var avatarUri:Uri){
    fun toJSON():JSONObject{
        return JSONObject("""
            {"name":"${name}", "avatar":"${avatarUri}"}
        """.trimIndent())
    }
}
data class MessagingMessage(var sender:String, var content:String, var image:Uri?=null ){
    fun toJSON():JSONObject{
        val a = JSONObject("""
            {"sender":"${sender}", "content":"${content}"}
        """.trimIndent())
        image?.let{
            a.put("image", image.toString())
        }
        return a
    }
}