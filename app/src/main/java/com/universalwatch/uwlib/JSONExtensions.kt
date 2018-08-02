package com.universalwatch.uwlib

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by emile on 29.01.2018.
 */
/**
 * Searches through the entire tree in search for objects with a  value.  Types are
 * determined at runtime. If it return null, then the type is not supported
 */
fun <T> JSONObject.getByAttribute(attr:String, value:T):MutableList<JSONObject>?{
    val ret = mutableListOf<JSONObject>()
    val queue = mutableListOf<JSONObject>()
    queue.add(this)
    var size = queue.size
    var index = 0
    while (true) {
        var obj = queue[index]
        for (key in obj.keys()) {

            if (value is String && obj.getString(key) == value) {
                ret.add(obj)
            }
            if (value is Int && obj.getInt(key) == value) {
                ret.add(obj)
            }
            if (value is Double && obj.getDouble(key) == value) {
                ret.add(obj)
            }


            try {
                obj.getJSONObject(key)

                queue.add(obj.getJSONObject(key))




            }
            catch (e:JSONException){

            }
            try {
                val arr = obj.getJSONArray(key)
                for (i in 0..arr.length()){
                    try {
                        queue.add(arr.getJSONObject(i))

                    }
                    catch (e:JSONException){

                    }
                }
                queue.add(obj.getJSONObject(key))

            }
            catch (e:JSONException){

            }

        }
    size = queue.size
    index=index+1
        if (index<size){
            Log.d("nope","nope xd ${size}, $index")
        }
        else{
            break
        }
    }
    return ret
}
/*
Converts a list to JSON and returns a JSONObject. Default to string
 */
fun <T> List<T>.toJsonWATCH():JSONArray{
    val r=JSONArray()
    for (o in this){
        r.put(o.toString())
    }
    return  r
}
fun <T> toJson(list:List<T> ):JSONArray{
    val r=JSONArray()
    for (o in list){
        r.put(o.toString())
    }
    return  r
}