package com.bangkit.capstone.facecare.view.scan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityScanBinding
import com.bangkit.capstone.facecare.databinding.ActivitySignupBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.result.ResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private val database = Firebase.database("https://facecare-82102-default-rtdb.asia-southeast1.firebasedatabase.app")

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

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                binding.imageButton.setImageBitmap(imageBitmap)
                binding.imageButton.setBackgroundResource(R.drawable.bg_add_image_transparent)
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
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun openGallery() {
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(pickPhotoIntent)
    }

    private fun scanNow(){
        val userUid = Firebase.auth.currentUser?.uid
        val scanHistoryRef = database.getReference("users/$userUid/scanHistory")

        val imageUrl = "https://placehold.co/600x400.png" // Ganti dengan URL gambar dari respons API
        val skinCondition = "Eczema" // Ganti dengan jenis penyakit kulit dari respons API
        val treatmentTips = "Use moisturizer regularly." // Ganti dengan tips pengobatan dari respons API
        val dateTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val scanHistory = ScanResult(imageUrl, skinCondition, treatmentTips, dateTime)

        scanHistoryRef.push().setValue(scanHistory)
            .addOnSuccessListener {
                startActivity(Intent(this, ResultActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal", Toast.LENGTH_LONG).show()
            }

    }
}