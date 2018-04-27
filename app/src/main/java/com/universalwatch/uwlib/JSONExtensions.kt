package com.universalwatch.uwlib

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
    for (obj in queue) {
        for (key in obj.keys()) {
            if (value is String && this.getString(key) == value) {
                ret.add(obj)
            }
            if (value is Int && this.getInt(key) == value) {
                ret.add(obj)
            }
            if (value is Double && this.getDouble(key) == value) {
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
                        continue
                    }
                }
                queue.add(obj.getJSONObject(key))
            }
            catch (e:JSONException){

            }

        }


    }
    return ret
}