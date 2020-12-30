package com.example.plantbuddy.model

import android.os.Parcel
import android.os.Parcelable

class Plant() : Parcelable
{
    var userId: String? = null
    var plantId: String? = null
    var plantName: String? = null
    var wateringFreq: String? = null
    var plantType: String? = null
    var temperature: String? = null
    var livingHabitat: Int = 0
    var sunExposure: Int = 0
    var startTime: String? = null
    var endTime: String? = null
    var imageUrl: String? = null
    var alarmId: Int = -1

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        plantId = parcel.readString()
        plantName = parcel.readString()
        wateringFreq = parcel.readString()
        plantType = parcel.readString()
        temperature = parcel.readString()
        livingHabitat = parcel.readInt()
        sunExposure = parcel.readInt()
        startTime = parcel.readString()
        endTime = parcel.readString()
        imageUrl = parcel.readString()
        alarmId = parcel.readInt()
    }


    constructor(
        userId: String?,
        plantId: String? ,
        plantName: String?,
        wateringFreq: String?,
        plantType: String?,
        temperature: String,
        livingHabitat: Int,
        sunExposure: Int,
        startTime: String?,
        endTime: String?,
        imageUrl: String?,
        alarmId:Int) : this()
    {
        this.userId= userId
        this.plantId= plantId
        this.plantName = plantName
        this.wateringFreq=wateringFreq
        this.plantType=plantType
        this.temperature=temperature
        this.livingHabitat = livingHabitat
        this.sunExposure=sunExposure
        this.startTime=startTime
        this.endTime=endTime
        this.imageUrl = imageUrl
        this.alarmId = alarmId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(plantId)
        parcel.writeString(plantName)
        parcel.writeString(wateringFreq)
        parcel.writeString(plantType)
        parcel.writeString(temperature)
        parcel.writeInt(livingHabitat)
        parcel.writeInt(sunExposure)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(imageUrl)
        parcel.writeInt(alarmId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Plant> {
        override fun createFromParcel(parcel: Parcel): Plant {
            return Plant(parcel)
        }

        override fun newArray(size: Int): Array<Plant?> {
            return arrayOfNulls(size)
        }
    }


}
