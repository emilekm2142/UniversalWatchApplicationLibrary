package com.universalwatch.uwlib

import android.content.Context
import android.graphics.Color
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by emile on 14-Nov-17.
 */
//TODO: onBack w fabrykach FromJson?
data class ColorSet(
        var primary:Color,
        var secondary:Color,
        var text:Color
)
{
    fun toJson():JSONObject{
        val j= JSONObject()
        with(j){
        put("primary",primary)
            put("secondary",secondary)
            put("text",text.toString())
        }
        return j
    }
}
abstract class View(
        var name:String,
        private var datatype:String,

        var actions:MutableList<Action> = mutableListOf(),
        onBack: (Context, String) -> Unit,
        var template:JSONObject?=null
){
    lateinit var systemCallbacks:SystemActions
    var sideActionsAcive=false
    init{
        systemCallbacks = SystemActions()
        systemCallbacks.onBack=onBack
    }
    var colorSet:ColorSet?=null

    protected fun getBasicPropertiesAsMap():Map<String,String>{
        return mapOf("name" to name, "datatype" to datatype)
    }
    abstract fun asJson():JSONObject

    protected fun mergeCommonInnerPropertiesWithOutputJSON(outputJson:JSONObject):JSONObject{
        outputJson.put("sideActionsActive", sideActionsAcive)
        return outputJson
    }
    protected fun actionsToJson():JSONArray{
        var out = JSONArray()
        actions?.let {
            for (i:Action in it.toList())
            {
                out.put(i.toJson())
            }
        }
        return out
    }

}
class CustomView(
        name:String,
        template:JSONObject,
        onBack: (Context, String) -> Unit

):View(name,"custom", onBack = onBack,template = template!!){


    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        var templateMerged = mergeCommonInnerPropertiesWithOutputJSON(template!!)
        parent.put("data", template)
        return parent
    }



}
class ImageView(
        name:String,
        var imageUri:Uri= Uri.EMPTY,
        var flickable:Boolean=false,

        actions: MutableList<Action> = mutableListOf(),
        onBack:(Context, String)->Unit,
        template: JSONObject?=null
):View(name,"image",actions, onBack =onBack,template = template){
    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        var innerData = JSONObject()

        with(innerData){
            put("imageUri",imageUri.toString())
            put("flickable",flickable)
        }
        innerData= mergeCommonInnerPropertiesWithOutputJSON(innerData)
        parent.put("data",innerData)
        return parent
    }

}
class MediaView(
        name:String,
        var imageUri: Uri,
        var songName:String,
        var artist:String,
        var album:String,
        var extra:String,
        actions:MutableList<Action> = mutableListOf(),
        template: JSONObject?=null,
        onBack:(Context, String)->Unit ={c,s->}
):View(name,"media",actions, onBack =onBack, template = template){
    var blurImage:Boolean=true

    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        var innerData = JSONObject()

        with(innerData){
            put("imageUri",imageUri.toString())
            put("song",songName)
            put("artist",artist)
            put("album",album)
            put("extra",extra)
            put("actions",actionsToJson())
        }
        innerData = mergeCommonInnerPropertiesWithOutputJSON(innerData)
        parent.put("data",innerData)
        return parent
    }

}
class TextView(

        name:String,
        var major:String,
        var minor:String,


        actions:MutableList<Action> = mutableListOf(),
        var imageUri: Uri?=null,
        var progress:Double?=null,
        var style:Layouts = Layouts.NOTIFICATION_STYLE,
        template: JSONObject?=null,
                onBack:(Context, String)->Unit ={c,s->}

):View(name,"text",actions, onBack = onBack,template = template){
    companion object {

        enum class Layouts{
            NOTIFICATION_STYLE,
            TEXT_WALL_STYLE,
            TO_BOTTOM,
            TO_TOP,
            CENTER

        }

    }
    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())


        with(parent){
            put("significant",major)
            put("minor",minor)
            imageUri?.let{
                put("imageUri", imageUri.toString())
            }
            progress?.let {
                put("progress",it)
            }
            template?.let{
                put("template", it)
            }
            put("style", style.toString())
            put("actions",actionsToJson())
        }

        return parent
    }

}
class ListView(

        name:String,
        val elements:MutableList<ListElement>? =null,
        template:JSONObject? = null,
        val simpleElements:MutableList<String>? =null,
        var clickable:Boolean=false,
        var onClick:(Context, Int, String)->Unit = {context, i:Int, s:String->},
        onBack:(Context, String)->Unit = {c,s->}
):View(name,"list", onBack = onBack,template = template){

    override fun asJson():JSONObject {

        var parent = JSONObject(getBasicPropertiesAsMap())
        simpleElements?.let {
            parent.put("listData", simpleElements.toJsonWATCH())
            parent = mergeCommonInnerPropertiesWithOutputJSON(parent)
        }
        template?.let {
            parent.put("listTemplate", template)
        }


        return parent
    }

}




/*
class MessageView(
        name:String,
        actions:MutableList<Action> = mutableListOf()
):View(name,"messaging",actions){
    override fun asJson():JSONObject {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
class GPSView(
        name:String,
        actions:MutableList<Action> = mutableListOf()
):View(name,"GPS",actions){
    override fun asJson():JSONObject {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}*/