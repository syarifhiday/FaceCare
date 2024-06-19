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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.facecare.BuildConfig
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.data.response.ScanResult
import com.bangkit.capstone.facecare.databinding.ActivityMainBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.result.ResultActivity
import com.bangkit.capstone.facecare.view.scan.ScanActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator

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
        database = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_RDB_URL).reference

        val greetingText = binding.greetingText
        val descriptionText = binding.descriptionText

        greetingText.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        descriptionText.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()

        val animator = SlideInLeftAnimator().apply {
            addDuration = 1000
            moveDuration = 1000
            changeDuration = 1000
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = animator // Use SlideInLeftAnimator for sliding animation

        binding.infoHistory.visibility = View.VISIBLE
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
            override fun onDataChanged() {
                super.onDataChanged()
                // Check if the adapter contains any items
                if (itemCount > 0) {
                    binding.infoHistory.visibility = View.GONE
                } else {
                    binding.infoHistory.visibility = View.VISIBLE
                    binding.infoHistory.text = "Belum ada riwayat scan"
                }
            }
        }

        // Wrap adapter with animation adapter
        val animationAdapter = ScaleInAnimationAdapter(adapter)
        recyclerView.adapter = animationAdapter

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

        binding.greetingText.text = "Hi ${mAuth.currentUser?.displayName}, ${getString(R.string.greeting)}"

    }

    private fun setupAction() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu1 -> {
                    alert()
                    true
                }
                else -> false
            }
        }
    }

    private fun alert(){
        AlertDialog.Builder(this).apply {
            setTitle("Keluar?")
            setMessage("Anda Yakin untuk Logout?")
            setPositiveButton("Ya") { _, _ ->
                signOutAndStartSignInActivity()
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            create()
            show()
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
