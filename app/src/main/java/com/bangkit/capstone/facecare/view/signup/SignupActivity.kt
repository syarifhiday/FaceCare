package com.bangkit.capstone.facecare.view.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.facecare.databinding.ActivitySignupBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun processSignup(){
        val userName = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    val nameUpdate = userProfileChangeRequest {
                        displayName = userName
                    }
                    val user = task.result.user
                    user!!.updateProfile(nameUpdate)
                        .addOnCompleteListener{
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .addOnFailureListener{ error2->
                            Toast.makeText(this, error2.localizedMessage, LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener{ error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
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
        with(binding){
            signupButton.setOnClickListener {
                processSignup()
            }
            back.setOnClickListener{
                finish()
            }
            Login.setOnClickListener{
                finish()
            }
        }
    }

    private fun alert(){
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Akunmu sudah jadi nih. Yuk, login.")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
}