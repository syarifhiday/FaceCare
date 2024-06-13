package com.bangkit.capstone.facecare.view.signup

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.facecare.databinding.ActivitySignupBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var database: DatabaseReference = FirebaseDatabase.getInstance("https://facecare-82102-default-rtdb.asia-southeast1.firebasedatabase.app").reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        setupView()
        setupAction()
        playAnimation()

    }

    private fun playAnimation(){
        val form = binding.formLayoutSignup
        form.translationY = form.height.toFloat()

        form.post {
            val animator = ObjectAnimator.ofFloat(form, "translationY", form.height.toFloat(), 0f).apply {
                duration = 1000
            }
            animator.start()
        }
    }

    private fun processSignup(){
        val userName = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    showLoading(false)
                    val nameUpdate = userProfileChangeRequest {
                        displayName = userName
                    }
                    val user = task.result.user
                    user!!.updateProfile(nameUpdate)
                        .addOnCompleteListener{
                            val userId = user.uid
                            saveUserToDatabase(userId)
                            startActivity(Intent(this, LoginActivity::class.java))
                        }
                        .addOnFailureListener{ error2->
                            Toast.makeText(this, error2.localizedMessage, LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener{ error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_LONG).show()
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
                if (binding.nameEditText.text.isNullOrEmpty() || binding.emailEditText.text.isNullOrEmpty() || binding.passwordEditText.text.isNullOrEmpty()){
                    Toast.makeText(this@SignupActivity, "Data harus lengkap", Toast.LENGTH_SHORT).show()
                }else{
                    showLoading(true)
                    processSignup()
                }

            }
            back.setOnClickListener{
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            Login.setOnClickListener{
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
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

    private fun saveUserToDatabase(userId: String) {
        val userMap = mapOf(
            "uid" to userId,
            "email" to firebaseAuth.currentUser?.email,
            "name" to firebaseAuth.currentUser?.displayName
        )

        database.child("users").child(userId).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Failed to save user: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}