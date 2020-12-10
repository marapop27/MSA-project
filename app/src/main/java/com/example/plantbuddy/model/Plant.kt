package com.example.plantbuddy.model

class Plant {
    var userId: String? = null
    var plantId: String? = null
    var plantName: String? = null
    var wateringFreq: String? =null
    var plantType:String? =null
    var temperature:String? =null
    var sunExposure:String?=null
    var startTime:String?=null
    var endTime:String?=null

    constructor(
        userId: String?,
        plantId: String? ,
        plantName: String?,
        wateringFreq: String?,
        plantType: String?,
        temperature: String?,
        sunExposure: String?,
        startTime: String?,
        endTime: String?
    ) {
        this.userId= userId
        this.plantId= plantId
        this.plantName = plantName
        this.wateringFreq=wateringFreq
        this.plantType=plantType
        this.temperature=temperature
        this.sunExposure=sunExposure
        this.startTime=startTime
        this.endTime=endTime
    }

}