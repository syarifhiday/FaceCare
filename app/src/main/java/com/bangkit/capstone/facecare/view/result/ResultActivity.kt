package com.bangkit.capstone.facecare.view.result

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityMainBinding
import com.bangkit.capstone.facecare.databinding.ActivityResultBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.main.MainActivity
import com.bumptech.glide.Glide

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val scanResult = intent.getParcelableExtra<ScanResult>("scanResult")

        val jenisPenyakit = binding.jenisPenyakit
        val detailPenyakit = binding.detailPenyakit

        jenisPenyakit.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        detailPenyakit.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()

        binding.back.setOnClickListener{
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Tambahkan logika untuk menampilkan data scanResult di layout detail
        if (scanResult != null) {
            // Contoh menampilkan data ke TextView
            Glide.with(this)
                .load(scanResult.imageUrl)
                .into(binding.facePhoto)
            binding.jenisPenyakit.text = scanResult.skinCondition
            binding.detailPenyakit.text = scanResult.treatmentTips
        }
    }
}