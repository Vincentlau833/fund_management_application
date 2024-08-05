package com.example.tofund_v3

class eventModelAdmin(private var eventKey:String, private var eventName:String) {
    fun getEventKey():String{
        return eventKey
    }

    fun getEventName():String{
        return eventName
    }

    fun setEventKey(eventKey: String){
        this.eventKey = eventKey
    }

    fun setEventName(eventName: String){
        this.eventName = eventName
    }

}