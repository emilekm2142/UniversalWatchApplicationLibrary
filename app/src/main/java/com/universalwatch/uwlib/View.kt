package com.universalwatch.uwlib

import android.graphics.Color
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import kotlin.reflect.KClass

/**
 * Created by emile on 14-Nov-17.
 */

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
open abstract class View(
        var name:String,
        private var datatype:String,
        var actions:MutableList<Action> = mutableListOf()
){
    var colorSet:ColorSet?=null
    protected fun getBasicPropertiesAsMap():Map<String,String>{
        return mapOf(name to name, datatype to datatype)
    }
    abstract fun asJson():JSONObject
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
    companion object {
        public val namesToClasses = hashMapOf<String, Any>(
                "text" to TextView::class,
                        "media" to MediaView::class
        )
        fun asJson(view:View){

        }
        public fun FromJson(j:JSONObject){
            /*
            format:
            {
            "name":"anem",
            "datatype":"datatype",
            "data":{
                "major":"minor" etc

            }

             */
            //TODO: WYJEBAC KLASY ZOSTAWIC FABRYKI OBIEKTÓW JSONA XDDD baka da yo
            (namesToClasses["text"] as KClass<Any>)!!.java.fields[0].name //TODO: przerobić wszystkie toJSON na refleksję xdd
        }
        public fun FromJson(s:String){
            FromJson(JSONObject(s))
        }
    }
}
class CustomView(
        name:String,
        var template:String
):View(name,"custom"){
    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        parent.put("data",JSONObject(template))
        return parent
    }

    fun retreiveActions(): List<Action>? {
        var actions: MutableList<JSONObject>? = JSONObject(template).getByAttribute("type", "action")
        val ret = mutableListOf<Action>()
        actions?.let {

            for (action in actions) {
                ret.add(Action({},action.getString("callback"),action.getString("name"),action.getString("extras")))
            }
        }
        return ret
    }
}
class ImageView(
        name:String,
        var imageUri:Uri,
        var flickable:Boolean=false,
        actions: MutableList<Action>
):View(name,"image",actions){
    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        val innerData = JSONObject()

        with(innerData){
            put("imageUri",imageUri.toString())
            put("flickable",flickable)
        }
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
        actions:MutableList<Action> = mutableListOf()
):View(name,"media",actions){
    var blurImage:Boolean=true

    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        val innerData = JSONObject()

        with(innerData){
            put("imageUri",imageUri.toString())
            put("song",songName)
            put("artist",artist)
            put("album",album)
            put("extra",extra)
            put("actions",actionsToJson())
        }
        parent.put("data",innerData)
        return parent
    }
}
class TextView(

        name:String,
        var major:String,
        var minor:String,
        actions:MutableList<Action> = mutableListOf(),
        var style:Layouts = Layouts.NOTIFICATION_STYLE
):View(name,"text",actions){
    companion object {
        enum class Layouts{
            NOTIFICATION_STYLE,
            TEXT_WALL_STYLE

        }
    }
    override fun asJson():JSONObject {
        val parent = JSONObject(getBasicPropertiesAsMap())
        val innerData = JSONObject()

        with(innerData){
            put("major",major)
            put("minor",minor)
            put("style", style.toString())
            put("actions",actionsToJson())
        }
        parent.put("data",innerData)
        return parent
    }

}
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
}