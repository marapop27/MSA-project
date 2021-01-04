package com.example.plantbuddy

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.plantbuddy.helpers.showErrorSnackbar
import com.example.plantbuddy.model.Plant
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_plants.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class AddNewPlantsActivity : AppCompatActivity() {
    var TAG = "LogTest"
    private val REQUEST_PERMISSION = 44
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private var plantForEdit:Plant? = null

    lateinit var timePicker: TimePickerHelper
    lateinit var doneButton: Button
    lateinit var spinner_habitat: Spinner
    lateinit var spinner_sun: Spinner

    lateinit var plantName :TextView
    lateinit var wateringFreq :TextView
    lateinit var envTemp:TextView
    lateinit var plantImage:ImageView
    lateinit var coordinatorLayout:CoordinatorLayout

    var plantBitmap:Bitmap? = null
    var plantImageUrl:String? = null
    lateinit var currentPhotoPath: String

    var plantHabitat:Int = 0
    var sunExposureLevel:Int = 0
    var startTime="00:00"
    var endTime="00:00"
    var initialStartHour: Int = 0;
    var initialEndHour: Int = 0;
    var initialStartMin: Int = 0;
    var initialEndMin: Int = 0;

    private lateinit var plantTypeList: Array<String>
    private lateinit var sunExposureLevelList: Array<String>

    lateinit var progressDialog:ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_plants)

        plantTypeList = resources.getStringArray(R.array.plant_indoor_outdoor)
        sunExposureLevelList = resources.getStringArray(R.array.sun_exposure_level)
        progressDialog = ProgressDialog(this)
        initViews()
        initializeSpinner()

        plantForEdit = intent?.getParcelableExtra<Plant>("plant")
        initPlantForEdit(plantForEdit)
        initDefaultTimeIfNeeded();

        var backButton = findViewById<ImageView>(R.id.btn_toolbar_back);
        backButton.setOnClickListener {
            onBackPressed();
        }

        timePicker = TimePickerHelper(this, false, false)
        bt_start_time.setOnClickListener {
            timePicker.showDialog(initialStartHour, initialStartMin, object : TimePickerHelper.Callback{
                override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                    initialStartHour = hourOfDay
                    initialStartMin = minute
                    startTime = getFormattedTime(hourOfDay, minute)
                    tp_start_time.text = startTime
                }
            })
        }

        bt_end_time.setOnClickListener {
            timePicker.showDialog(initialEndHour, initialEndMin, object : TimePickerHelper.Callback{
                override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                    initialEndHour = hourOfDay
                    initialEndMin = minute
                    endTime = getFormattedTime(hourOfDay, minute)
                    tp_end_time.text = endTime
                }
            })
        }

        doneButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                addOrUpdatePlantInFirebase()
            }
        }

        plantImage.setOnClickListener(View.OnClickListener {
            showChooseImageDialog()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                setPicFromCamera()
            }
            else if (requestCode == REQUEST_PICK_IMAGE) {
                setPicFromUri(data?.data)
            }
        }
    }

    private fun initViews()
    {
        plantImage = findViewById(R.id.iv_add_new_plant_photo)
        plantName = findViewById<EditText>(R.id.plant_name_add)
        wateringFreq = findViewById<EditText>(R.id.plant_watering_days_add)
        envTemp=findViewById<EditText>(R.id.env_temp_add)
        doneButton = findViewById(R.id.done_button);
        spinner_habitat = findViewById<Spinner>(R.id.spinner_plant_type)
        spinner_sun = findViewById<Spinner>(R.id.spinner_sun_exposure)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
    }

    private fun initDefaultTimeIfNeeded()
    {
        if(plantForEdit != null)
        {
            return
        }
        val cal = Calendar.getInstance()
        initialStartHour = cal.get(Calendar.HOUR_OF_DAY)
        initialStartMin =  cal.get(Calendar.MINUTE)
        cal.add(Calendar.MINUTE, 30)
        initialEndHour = cal.get(Calendar.HOUR_OF_DAY)
        initialEndMin = cal.get(Calendar.MINUTE)
    }
    private fun initPlantForEdit(plant:Plant?)
    {
        if(plant == null)
        {
            return;
        }
        Glide.with(this).load(plant.imageUrl).centerCrop().into(plantImage);
        plantName.text = plant.plantName
        wateringFreq.text = plant.wateringFreq
        envTemp.text = plant.temperature
        spinner_habitat.setSelection(plant.livingHabitat)
        plantImageUrl = plant.imageUrl

        val cal = Calendar.getInstance()
        initialStartHour = plant.startTime?.substring(0, 2)?.toInt() ?: cal.get(Calendar.HOUR_OF_DAY)
        initialStartMin = plant.startTime?.substring(3, 5)?.toInt() ?: cal.get(Calendar.MINUTE)
        startTime = getFormattedTime(initialStartHour, initialStartMin)

        cal.add(Calendar.MINUTE, 30)
        initialEndHour = plant.endTime?.substring(0, 2)?.toInt() ?: cal.get(Calendar.HOUR_OF_DAY)
        initialEndMin = plant.endTime?.substring(3, 5)?.toInt() ?: cal.get(Calendar.MINUTE)
        endTime = getFormattedTime(initialEndHour, initialEndMin)

        tp_start_time.text = startTime
        tp_end_time.text = endTime

        doneButton.setText(R.string.update_plant)
    }

    private fun getFormattedTime(hourOfDay:Int, minute:Int): String
    {
        val hourStr = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
        val minuteStr = if (minute < 10) "0${minute}" else "${minute}"
        return "${hourStr}:${minuteStr}"
    }

    private fun initializeSpinner(){



        val habitatAdapter = ArrayAdapter(this, R.layout.row_spinner_habitat, plantTypeList)
        spinner_habitat.adapter = habitatAdapter
        spinner_habitat.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                plantHabitat = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val sunAdapter = ArrayAdapter(this, R.layout.row_spinner_habitat, sunExposureLevelList)
        spinner_sun.adapter = sunAdapter
        spinner_sun.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sunExposureLevel = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
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
            "type",
            envTemp.text.toString(),
            plantHabitat,
            sunExposureLevel,
            startTime,
            endTime,
            plantImageUrl,
            -1
        )

        if (plantId != null) {
            reference.child(plantId).setValue(plant).addOnSuccessListener {
                progressDialog.setMessage(
                    if(plantForEdit == null)
                        "${plantName.text}  added!"
                    else "${plantName.text}  saved!"
                )
                progressDialog.setTitle("Success!")
                progressDialog.setIndeterminateDrawable(
                    ContextCompat.getDrawable(
                        this,
                        android.R.drawable.stat_sys_upload_done
                    )
                )
                progressDialog.setOnDismissListener {
                    onBackPressed()
                }
            }
        }
    }

    //region upload image from camera and gallery

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

    private fun showChooseImageDialog()
    {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val options = arrayOf("Take a photo", "Upload from Gallery")
        builder.setItems(options, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (which == 0) {
                    checkCameraPermission()
                    return;
                }
                openGallery();
            }
        })

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    @Throws(IOException::class)
    private fun createTempImageFileForCamera(): File {
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

    private fun setPicFromUri(uri:Uri?)
    {
        try {
            uri?.also {
                if(Build.VERSION.SDK_INT < 28) {
                    plantBitmap = MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        uri
                    )
                    Glide.with(this).load(plantBitmap).centerCrop().into(plantImage);
                } else {
                    val imageStream = contentResolver.openInputStream(uri)
                    plantBitmap = BitmapFactory.decodeStream(imageStream)
                    Glide.with(this).load(plantBitmap).centerCrop().into(plantImage);
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPicFromCamera() {
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
            Glide.with(this).load(plantBitmap).centerCrop().into(plantImage);
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createTempImageFileForCamera()
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

    //endregion

    // UploadImage method

    private suspend fun addOrUpdatePlantInFirebase()
    {
        if(plantName.text.isEmpty())
        {
            showErrorSnackbar(coordinatorLayout, "Plant name invalid!")
            return;
        }
        if(wateringFreq.text.isEmpty())
        {
            showErrorSnackbar(coordinatorLayout, "Watering frequency invalid!")
            return;
        }
        if(envTemp.text.isEmpty())
        {
            showErrorSnackbar(coordinatorLayout, "Environment Temperature invalid!")
            return;
        }
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("plants")
        val storageReference = FirebaseStorage.getInstance().reference
        val ref: StorageReference = storageReference.child(
            "images/" + UUID.randomUUID().toString()
        )
        var plantId:String? = "";

        if (plantForEdit == null)
        {
            plantId = reference.push().key
        }
        else
        {
            plantId = plantForEdit?.plantId
        }

        if(plantId == null)
        {
            //TODO: Error Message
            return;
        }
        if(plantBitmap == null)
        {
            withContext(Dispatchers.Main)
            {
                progressDialog.setTitle("Saving...")
                progressDialog.show()
            }
            addPlant(plantId)
            return;
        }
        uploadImageToFirebase(plantBitmap, plantId, ref, OnSuccessListener { fileUri ->
            val generatedFilePath = fileUri?.toString()
            plantImageUrl = generatedFilePath
            addPlant(plantId)
        })
    }

    private suspend fun uploadImageToFirebase(bitmap: Bitmap?, fileName:String, storageReference:StorageReference, onSuccessListener: OnSuccessListener<Uri>){
        withContext(Dispatchers.Main)
        {
            progressDialog.setTitle("Uploading...")
            progressDialog.setMessage(
                "Uploaded 0%"
            )
            progressDialog.show()
        }
        // Code for showing progressDialog while uploading

        // adding listeners on upload
        // or failure of image
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data: ByteArray = baos.toByteArray()
        storageReference.putBytes(data).addOnSuccessListener { // Image uploaded successfully
            storageReference.downloadUrl.addOnSuccessListener(onSuccessListener)
                .addOnFailureListener{
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