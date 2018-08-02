package com.universalwatch.uwlib

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject

/**
 *
tutaj będa kontrolki do robienia własnych widoków fast and easy
 */
class ListElement(){
    //TODO: add data-template validation. Does every key in data exist in template?
    private val listOfEntries:MutableList<Entry> = mutableListOf()
    init{


    }
    fun toJson():JSONObject{
        val ret = JSONObject()
        for (entry in listOfEntries){
            ret.put(entry.id, entry.toJson() )
        }
        return ret
    }
    fun newId(id:String):Entry{
        val n = Entry(id, this)
        listOfEntries.add(n)
        return n
    }

}
class Entry(val id:String, private val parent:ListElement){
    private val json:JSONObject = JSONObject()
    fun addProperty(key:String, value:Any):Entry{
        json.put(key, value)
        return this
    }
    fun startNew():ListElement{
        return parent
    }
    fun finalize():ListElement{
        return parent
    }
    fun toJson():JSONObject{
        return json
    }
}
class SystemActions( ){
    var onNext:(Context, String)->Unit = {c, s->} ;
    var onPrev:(Context,String)->Unit= {c,s->}
    var onBack:(Context,String)->Unit= {c,s->}
}