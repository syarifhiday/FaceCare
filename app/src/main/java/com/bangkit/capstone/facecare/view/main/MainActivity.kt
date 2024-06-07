package com.bangkit.capstone.facecare.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.databinding.ActivityMainBinding
import com.bangkit.capstone.facecare.view.ViewModelFactory
import com.bangkit.capstone.facecare.view.scan.ScanActivity
import com.bangkit.capstone.facecare.view.welcome.WelcomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
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

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            val userEmail = user.email
            val userToken = user.uid

            with(binding){
                nama.text = userName
                email.text = userEmail
                uid.text = userToken
            }
        } else {
            // Handle the case where the user is not signed in
        }


        setupView()
        setupAction()
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
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}