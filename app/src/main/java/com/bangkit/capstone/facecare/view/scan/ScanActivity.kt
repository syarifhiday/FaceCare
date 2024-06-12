package com.bangkit.capstone.facecare.view.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.PredictionResponse
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityScanBinding
import com.bangkit.capstone.facecare.view.result.ResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private val database = Firebase.database("https://facecare-82102-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val urlAPI = "https://facecare3-6jxdikw4pa-et.a.run.app/"
    private val REQUEST_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                val tempUri = saveBitmapToFile(imageBitmap)
                if (tempUri != null) {
                    selectedImageUri = tempUri
                    binding.imageButton.setImageURI(selectedImageUri)
                    binding.imageButton.setBackgroundResource(R.drawable.bg_add_image_transparent)
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_LONG).show()
                }
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                this.selectedImageUri = result.data?.data
                binding.imageButton.setImageURI(selectedImageUri)
                binding.imageButton.setBackgroundResource(R.drawable.bg_add_image_transparent)
            }
        }

        binding.imageButton.setOnClickListener {
            showBottomSheetDialog()
        }

        binding.scanBtn.setOnClickListener {
            showLoading(true)
            scanNow()
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val tvCamera = bottomSheetView.findViewById<TextView>(R.id.tvCamera)
        val tvGallery = bottomSheetView.findViewById<TextView>(R.id.tvGallery)

        tvCamera.setOnClickListener {
            openCamera()
            bottomSheetDialog.dismiss()
        }

        tvGallery.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(takePictureIntent)
    }

    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickPhotoIntent)
    }

    private fun scanNow() {
        val uri = selectedImageUri
        if (uri != null) {
            val filePath = getPathFromUri(uri)
            if (filePath != null) {
                val file = File(filePath)
                val mediaType = "application/octet-stream".toMediaType()
                val requestBody = file.asRequestBody(mediaType)

                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.name, requestBody)
                    .build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url(urlAPI)
                            .post(body)
                            .build()

                        val response = client.newCall(request).execute()
                        runOnUiThread {
                            showLoading(false) // Move showLoading inside runOnUiThread
                        }

                        response.use {
                            if (!it.isSuccessful) throw IOException("Unexpected code $it")
                            val responseBody = it.body?.string()
                            println(responseBody)

                            val predictionResponse = Gson().fromJson(responseBody, PredictionResponse::class.java)
                            val kondisi = predictionResponse.kondisi
                            val treatmentTips = if (kondisi == "Jerawat") getString(R.string.acneDescription) else getString(R.string.healthyDescription)

                            // Handle the response, e.g., parse JSON and update UI
                            // For demonstration, let's assume response contains imageUrl, skinCondition, and treatmentTips
                            saveToHistory(kondisi, treatmentTips)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this@ScanActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Failed to get file path from URI", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "temp_image.jpg")
        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getPathFromUri(uri: Uri): String? {
        return if (uri.scheme == "content") {
            val tempFile = File(cacheDir, "temp_image.jpg")
            try {
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                tempFile.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        } else {
            uri.path
        }
    }


    private fun saveToHistory(skinCondition: String, treatmentTips: String){
        val imageUrl = selectedImageUri.toString()
        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val scanResult = ScanResult(imageUrl, skinCondition, treatmentTips, dateTime)

        val userUid = Firebase.auth.currentUser?.uid
        val scanHistoryRef = database.getReference("users/$userUid/scanHistory")
        scanHistoryRef.push().setValue(scanResult)
            .addOnSuccessListener {
                runOnUiThread {
                    val intent = Intent(this@ScanActivity, ResultActivity::class.java)
                    intent.putExtra("scanResult", scanResult) // Send scanResult model to ResultActivity
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this@ScanActivity, "Gagal", Toast.LENGTH_LONG).show()
                }
            }
    }


    private fun checkPermissions() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // Method to request permissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissions granted, proceed with the action
                } else {
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    // Companion object for permissions
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}
