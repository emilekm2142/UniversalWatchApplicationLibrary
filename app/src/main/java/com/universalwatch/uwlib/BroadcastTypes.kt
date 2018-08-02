package com.universalwatch.uwlib

/**
 * Created by emile on 30.11.2017.
 */
enum class BroadcastTypes{

    APPLICATION_INSTALL,
    VIEW_SHOW,
    VIEW_UPDATE,
    VIEW_DELETE,
    VIEW_REPLACE,
    APPLICATION_OPEN,
    CUSTOM_NOTIFICATION_SEND,
    SYSTEM_NOTIFICATION_SEND,
    APPLICATION_CLOSE,
    ACTION,
    HANDSHAKE,
    WATCH_RESPONSE,
    SYSTEM_REQUEST,
    LIST_VIEW_CLICK,
    INITIAL_VIEW_REQUEST,
    NOTIFICATION,
    SYSTEM_ACTION,
    RESOURCE_REQUEST;
    companion object {
        @JvmStatic
        public val broadcastTypesToWatchDatatypes = hashMapOf<BroadcastTypes, String>(
                BroadcastTypes.VIEW_UPDATE to "updateView",
                BroadcastTypes.VIEW_REPLACE to "replaceView",
                BroadcastTypes.VIEW_SHOW to "view",
                BroadcastTypes.ACTION to "action",
                BroadcastTypes.APPLICATION_INSTALL to "application",
                BroadcastTypes.INITIAL_VIEW_REQUEST to "initialViewRequest",
                BroadcastTypes.APPLICATION_OPEN to "initialViewRequest",
                BroadcastTypes.SYSTEM_NOTIFICATION_SEND to "notification",
                BroadcastTypes.HANDSHAKE to "handshake",
                BroadcastTypes.SYSTEM_REQUEST to "dataRequest",
                BroadcastTypes.WATCH_RESPONSE to "response",
                BroadcastTypes.LIST_VIEW_CLICK to "listViewClick",
                BroadcastTypes.SYSTEM_ACTION to "systemAction",
                BroadcastTypes.APPLICATION_CLOSE to "closeApplication",
                BroadcastTypes.NOTIFICATION to "notification",
                BroadcastTypes.RESOURCE_REQUEST to "resourceRequest"
        )

        fun toBroadcastTypeFromString(s:String):BroadcastTypes?{
            for ((key,value) in broadcastTypesToWatchDatatypes){
                if (value==s){
                    return key
                }
            }
            return null
        }
    }
}