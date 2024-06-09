package com.bangkit.capstone.facecare.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityMainBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.result.ResultActivity
import com.bangkit.capstone.facecare.view.scan.ScanActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FirebaseRecyclerAdapter<ScanResult, ScanLogViewHolder>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://facecare-82102-default-rtdb.asia-southeast1.firebasedatabase.app").reference

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Query to retrieve scan history for current user
        val userUid = mAuth.currentUser?.uid
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
                    intent.putExtra("scanResult", model) // Send scanResult model to ResultActivity
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
            skinDiseaseTextView.text = scanLog.skinCondition
            dateTimeTextView.text = scanLog.dateTime
        }
    }

    // Start listening for data when the activity starts
    override fun onResume() {
        super.onResume()
        adapter.startListening()
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
                    true
                }
                else -> false
            }
        }
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        // Clear activity stack and start LoginActivity
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
