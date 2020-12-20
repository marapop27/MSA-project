package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.plantbuddy.model.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_new_plants.*
import java.util.*

class UpdatePlantActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var startTimePicker: TimePickerHelper
    lateinit var endTimePicker: TimePickerHelper
    lateinit var doneButton: Button
    lateinit var spinner_habitat: Spinner
    lateinit var spinner_sun: Spinner

    lateinit var plantName : TextView
    lateinit var wateringFreq : TextView
    lateinit var envTemp: TextView

    lateinit var livingHabitat:String
    lateinit var sunExposureLevel:String
    var startTime="00"
    var endTime="00"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_plant)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        initializeSpinner()
        startTimePicker = TimePickerHelper(this, false, false)
        bt_start_time.setOnClickListener {
            showStartTimePickerDialog()
        }
        endTimePicker = TimePickerHelper(this, false, false)
        bt_end_time.setOnClickListener {
            showEndTimePickerDialog()
        }
        initStrings()

        doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener {
            updatePlant()
        }
    }

    private fun showStartTimePickerDialog() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        startTimePicker.showDialog(h, m, object : TimePickerHelper.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
                tp_start_time.text = "${hourStr}:${minuteStr}"
                startTime= tp_start_time.text as String
            }
        })
    }

    private fun showEndTimePickerDialog() {
        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)
        endTimePicker.showDialog(h, m, object : TimePickerHelper.Callback {
            override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
                tp_end_time.text = "${hourStr}:${minuteStr}"
                endTime= tp_end_time.text as String
            }
        })
    }

    private fun initStrings(){
        plantName = findViewById<EditText>(R.id.plant_name_add)
        wateringFreq = findViewById<EditText>(R.id.plant_watering_days_add)
        envTemp=findViewById<EditText>(R.id.env_temp_add)
    }

    private fun initializeSpinner(){

        val plantType = resources.getStringArray(R.array.plant_indoor_outdoor)
        val sun_exposure_level = resources.getStringArray(R.array.sun_exposure_level)

        spinner_habitat = findViewById<Spinner>(R.id.spinner_plant_type)
        spinner_sun = findViewById<Spinner>(R.id.spinner_sun_exposure)

        if (spinner_habitat != null) {
            val adapter = ArrayAdapter(this, R.layout.row_spinner_habitat, plantType)
            spinner_habitat.adapter = adapter
            spinner_habitat.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    livingHabitat = parent.getItemAtPosition(position) as String
                }
                override fun onNothingSelected(parent: AdapterView<*>) {} }
        }

        if (spinner_sun != null) {
            val adapter = ArrayAdapter(this, R.layout.row_spinner_habitat, sun_exposure_level)
            spinner_sun.adapter = adapter
            spinner_sun.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    sunExposureLevel = parent.getItemAtPosition(position) as String
                }
                override fun onNothingSelected(parent: AdapterView<*>) {} }
        }
    }

    private fun updatePlant(){
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = FirebaseDatabase.getInstance()
        val reference = database.getReference("plants")
        val plantId=reference.push().key

        val plant = Plant(currentUser?.uid,
            plantId,
            plantName.text.toString(),
            wateringFreq.text.toString(),
            envTemp.text.toString(),
            livingHabitat,
            sunExposureLevel,
            startTime,
            endTime
        )

        if (plantId != null) {
            reference.child(plantId).setValue(plant)
        }

//        Log.i("user", currentUser?.uid)
//        Log.i("plant",reference.push().key)
//        Log.i(TAG, plantName.text.toString())
//        Log.i(TAG,wateringFreq.text.toString())
//        Log.i(TAG,envTemp.text.toString())
//        Log.i(TAG,livingHabitat)
//        Log.i(TAG,sunExposureLevel)
//        Log.i(TAG, startTime)
//        Log.i(TAG, endTime)
    }
}