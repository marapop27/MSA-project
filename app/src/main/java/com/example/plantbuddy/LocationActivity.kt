package com.example.plantbuddy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class LocationActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var latitudeLabel: String? = null
    private var longitudeLabel: String? = null
    lateinit var latitudeText: TextView
    lateinit var longitudeText: TextView
    var gps_loc: Location? = null
    var network_loc: Location? = null
    var final_loc: Location? = null
    var longitude = 0.0
    var latitude = 0.0
//    var userCountry: String? = null
//    var userAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        latitudeLabel = resources.getString(R.string.latitudeBabel)
        longitudeLabel = resources.getString(R.string.longitudeBabel)
        latitudeText = findViewById(R.id.latitudeText1)
        longitudeText = findViewById(R.id.longitudeText2)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (gps_loc != null) {
            final_loc = gps_loc
            latitude = final_loc!!.latitude
            longitude = final_loc!!.longitude
        } else if (network_loc != null) {
            final_loc = network_loc
            latitude = final_loc!!.latitude
            longitude = final_loc!!.longitude
        } else {
            latitude = 0.0
            longitude = 0.0
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
            ),
            1
        )
        latitudeText.text = latitude.toString()
        longitudeText.text = longitude.toString()
//        try {
//            val geocoder = Geocoder(this, Locale.getDefault())
//            val addresses: List<Address>? =
//                geocoder.getFromLocation(latitude, longitude, 1)
//            if (addresses != null && addresses.size > 0) {
//                userCountry = addresses[0].getCountryName()
//                userAddress = addresses[0].getAddressLine(0)
////                latitudeText.text = "$userCountry, $userAddress"
//            } else {
//                userCountry = "Unknown"
//                latitudeText.text = userCountry
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }
}