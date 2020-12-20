package com.example.plantbuddy

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.plantbuddy.model.Plant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_plants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
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
    lateinit var currentPhotoPath: String

    lateinit var livingHabitat:String
    lateinit var sunExposureLevel:String
    var startTime="00"
    var endTime="00"

    lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_plants)
        progressDialog = ProgressDialog(this)
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
            lifecycleScope.launch(Dispatchers.IO) {
                uploadImageToFirebase(plantBitmap)
            }
        }

        plantImage.setOnClickListener(View.OnClickListener {
            checkCameraPermission()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                setPic()
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


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun setPic() {
        // Get the dimensions of the View
        val targetW: Int = plantImage.width
        val targetH: Int = plantImage.height

        val bmOptions = BitmapFactory.Options().apply {
            // Get the dimensions of the bitmap
            inJustDecodeBounds = true
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            // Determine how much to scale down the image
            val scaleFactor: Int = Math.max(1, Math.min(photoW / targetW, photoH / targetH))

            // Decode the image file into a Bitmap sized to fill the View
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inPurgeable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { bitmap ->
            plantBitmap = bitmap
            plantImage.setImageBitmap(bitmap)
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
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
            reference.child(plantId).setValue(plant).addOnSuccessListener {
                progressDialog.setMessage(
                    "${plantName.text}  added!"
                )
                progressDialog.setTitle("Success!")
                progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, android.R.drawable.stat_sys_upload_done))
                progressDialog.setOnDismissListener {
                    onBackPressed()
                }
            }
        }
    }

    // UploadImage method
    private suspend fun uploadImageToFirebase(bitmap: Bitmap?){
        if ("" != null) {

            withContext(Dispatchers.Main)
            {
                progressDialog.setTitle("Uploading...")
                progressDialog.setMessage(
                    "Uploaded 0%"
                )
                progressDialog.show()
            }
            // Code for showing progressDialog while uploading

            var database = FirebaseDatabase.getInstance()
            val reference = database.getReference("plants")
            val fileName=reference.push().key
            val storageReference = FirebaseStorage.getInstance().reference
            val ref: StorageReference = storageReference.child("images/" + UUID.randomUUID().toString())

            // adding listeners on upload
            // or failure of image
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
            val data: ByteArray = baos.toByteArray()
            ref.putBytes(data).addOnSuccessListener { // Image uploaded successfully
                ref.downloadUrl.addOnSuccessListener { task ->
                            val generatedFilePath = task?.toString()
                            plantImageUrl = generatedFilePath
                            addPlant(fileName.toString())
                }.addOnFailureListener{

                    progressDialog.dismiss()
                        Toast.makeText(
                                this@AddNewPlantsActivity,
                                "Failed to get image url!!",
                                Toast.LENGTH_SHORT
                            ).show()
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