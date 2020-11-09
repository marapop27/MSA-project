package com.example.plantbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_add_new_plants.*
import java.util.*

class AddNewPlantsActivity : AppCompatActivity() {

    lateinit var startTimePicker: TimePickerHelper
    lateinit var endTimePicker: TimePickerHelper
    lateinit var doneButton: Button
    lateinit var spinner_habitat: Spinner
    lateinit var spinner_sun: Spinner
    val outputMes = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_plants)

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        initializeSpinner()
        doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener {
            addPlantName(it)
        }

        startTimePicker = TimePickerHelper(this, false, false)
        bt_start_time.setOnClickListener {
            showStartTimePickerDialog()
        }

        endTimePicker = TimePickerHelper(this, false, false)
        bt_end_time.setOnClickListener {
            showEndTimePickerDialog()
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
                tp_start_time.text = "${hourOfDay}:${minuteStr}"
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
                tp_end_time.text = "${hourOfDay}:${minuteStr}"
            }
        })
    }

    private fun addPlantName(view: View){
        val input_check = findViewById<TextView>(R.id.add_plant_name_check)
        val plant_name_add = findViewById<EditText>(R.id.plant_name_add)
        val plant_watering_days_add = findViewById<EditText>(R.id.plant_watering_days_add)
        val env_temp_add=findViewById<EditText>(R.id.env_temp_add)

        outputMes.append(plant_name_add.text).append("\n")
        outputMes.append(plant_watering_days_add.text).append("\n")
        outputMes.append(env_temp_add.text).append("\n")
        input_check.text = outputMes
        input_check.visibility=View.VISIBLE
    }

    private fun initializeSpinner(){
        val indoor_outdoor_check = findViewById<TextView>(R.id.indoor_outdoor_check)
        val plantType = resources.getStringArray(R.array.plant_indoor_outdoor)
        val sun_exposure_level = resources.getStringArray(R.array.sun_exposure_level)
        val sun_exposure_check = findViewById<TextView>(R.id.sun_exposure_check)

        spinner_habitat = findViewById<Spinner>(R.id.spinner_plant_type)
        spinner_sun = findViewById<Spinner>(R.id.spinner_sun_exposure)

        if (spinner_habitat != null) {
            val adapter = ArrayAdapter(this, R.layout.row_spinner_habitat, plantType)
            spinner_habitat.adapter = adapter
            spinner_habitat.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                    indoor_outdoor_check.text=plantType[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {} }
        }

        if (spinner_sun != null) {
            val adapter = ArrayAdapter(this, R.layout.row_spinner_habitat, sun_exposure_level)
            spinner_sun.adapter = adapter
            spinner_sun.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    sun_exposure_check.text=sun_exposure_level[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {} }
        }
    }

}