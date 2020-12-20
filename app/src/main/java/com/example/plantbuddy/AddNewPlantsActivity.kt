package com.example.plantbuddy

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.plantbuddy.model.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_plants.*
import java.io.ByteArrayOutputStream
import java.util.*


class AddNewPlantsActivity : AppCompatActivity() {
    var TAG = "LogTest"
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    lateinit var startTimePicker: TimePickerHelper
    lateinit var endTimePicker: TimePickerHelper
    lateinit var doneButton: Button
    lateinit var spinner_habitat: Spinner
    lateinit var spinner_sun: Spinner

    lateinit var plantName :TextView
    lateinit var wateringFreq :TextView
    lateinit var envTemp:TextView
    lateinit var plantImage:ImageView
    var plantBitmap:Bitmap? = null
    var plantImageUrl:String? = null

    lateinit var livingHabitat:String
    lateinit var sunExposureLevel:String
    var startTime="00"
    var endTime="00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_plants)
        initViews()

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

        doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener {
            var database = FirebaseDatabase.getInstance()
            val reference = database.getReference("plants")
            val plantId=reference.push().key
            uploadImageToFirebase(plantBitmap, plantId.toString())
        }

        plantImage.setOnClickListener(View.OnClickListener {
            checkCameraPermission()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                plantBitmap = data?.extras?.get("data") as Bitmap
                plantImage.setImageBitmap(data?.extras?.get("data") as Bitmap)
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                val uri = data?.getData()
                plantImage.setImageURI(uri)
            }
        }
    }
    private fun initViews()
    {
        //TODO: add the rest of inits here
        plantImage = findViewById(R.id.iv_add_new_plant_photo)
        plantName = findViewById<EditText>(R.id.plant_name_add)
        wateringFreq = findViewById<EditText>(R.id.plant_watering_days_add)
        envTemp=findViewById<EditText>(R.id.env_temp_add)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
            return;
        }
        openCamera()
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
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
                startTime = tp_start_time.text as String
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
                endTime = tp_end_time.text as String
            }
        })
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

    private fun addPlant(plantId: String){

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        var database = FirebaseDatabase.getInstance()
        val reference = database.getReference("plants")

        val plant = Plant(
            currentUser?.uid,
            plantId,
            plantName.text.toString(),
            wateringFreq.text.toString(),
            envTemp.text.toString(),
            livingHabitat,
            sunExposureLevel,
            startTime,
            endTime,
            plantImageUrl
        )

        if (plantId != null) {
            reference.child(plantId).setValue(plant)
        }

        Toast.makeText(applicationContext, "Added Plant", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

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

    // UploadImage method
    private fun uploadImageToFirebase(bitmap: Bitmap?, fileName: String){
        val storageReference = FirebaseStorage.getInstance().reference
        val mountainsRef = storageReference.child("plantImages/${fileName}")
        if (fileName != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref: StorageReference = storageReference
                .child(
                    "images/"
                            + UUID.randomUUID().toString()
                )

            // adding listeners on upload
            // or failure of image
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.WEBP, 100, baos)
            val data: ByteArray = baos.toByteArray()
            ref.putBytes(data).addOnSuccessListener { // Image uploaded successfully
                ref.downloadUrl.addOnSuccessListener { task ->
                            progressDialog.dismiss()
                            Toast.makeText(
                                    this@AddNewPlantsActivity,
                                    "Image Uploaded!!",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                            val generatedFilePath = task?.toString()
                            plantImageUrl = generatedFilePath
                            addPlant(fileName.toString())
                }.addOnFailureListener{
                    progressDialog.dismiss()
                        Toast
                            .makeText(
                                this@AddNewPlantsActivity,
                                "Failed to get image url!!",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
            .addOnFailureListener { e -> // Error, Image not uploaded
                progressDialog.dismiss()
                Toast
                    .makeText(
                        this@AddNewPlantsActivity,
                        "Failed " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
            .addOnProgressListener { taskSnapshot ->

                    // Progress Listener for loading
                    // percentage on the dialog box
                    val progress = (100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount)
                    progressDialog.setMessage(
                        "Uploaded "
                                + progress.toInt() + "%"
                    )
                }
        }
    }
}