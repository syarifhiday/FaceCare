package com.bangkit.capstone.facecare.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityMainBinding
import com.bangkit.capstone.facecare.view.ViewModelFactory
import com.bangkit.capstone.facecare.view.result.ResultActivity
import com.bangkit.capstone.facecare.view.scan.ScanActivity
import com.bangkit.capstone.facecare.view.welcome.WelcomeActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<ScanResult, ScanLogViewHolder>
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance("https://facecare-82102-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Query to retrieve scan history for current user
        val userUid = auth.currentUser?.uid
        val query = database.child("users").child(userUid!!).child("scanHistory").orderByChild("dateTime")

        // Options for FirebaseRecyclerAdapter
        val options = FirebaseRecyclerOptions.Builder<ScanResult>()
            .setQuery(query, ScanResult::class.java)
            .build()

        // FirebaseRecyclerAdapter initialization
        adapter = object : FirebaseRecyclerAdapter<ScanResult, ScanLogViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanLogViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scan_log, parent, false)
                return ScanLogViewHolder(view)
            }

            override fun onBindViewHolder(holder: ScanLogViewHolder, position: Int, model: ScanResult) {
                holder.bind(model)
                holder.itemView.setOnClickListener {
                    val intent = Intent(this@MainActivity, ResultActivity::class.java)
                    intent.putExtra("scanResult", model) // Mengirim model scanResult ke DetailActivity
                    startActivity(intent)
                }
            }
        }

        // Set adapter to RecyclerView
        recyclerView.adapter = adapter


        setupView()
        setupAction()
    }

    class ScanLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val skinDiseaseTextView: TextView = itemView.findViewById(R.id.skinDiseaseTextView)
        private val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)

        fun bind(scanLog: ScanResult) {
            // Bind data to views
//            Glide.with(itemView)
//                .load(scanLog.imageUrl)
//                .into(imageView)
            skinDiseaseTextView.text = scanLog.skinCondition
            dateTimeTextView.text = scanLog.dateTime
        }
    }

    // Start listening for data when the activity starts
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    // Stop listening for data when the activity stops
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.fab.setOnClickListener{
            startActivity(Intent(this, ScanActivity::class.java))
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    signOutAndStartSignInActivity()
                    //viewModel.logout()
                    true
                }

                else -> false
            }
        }
    }
    private fun signOutAndStartSignInActivity() {
        auth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}