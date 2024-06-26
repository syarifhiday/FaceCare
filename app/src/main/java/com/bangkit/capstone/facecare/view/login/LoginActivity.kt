package com.bangkit.capstone.facecare.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.databinding.ActivityLoginBinding
import com.bangkit.capstone.facecare.view.main.MainActivity
import com.bangkit.capstone.facecare.view.signup.SignupActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private var auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation(){
        val form = binding.formLayoutLogin
        form.translationY = form.height.toFloat()

        form.post {
            val animator = ObjectAnimator.ofFloat(form, "translationY", form.height.toFloat(), 0f).apply {
                duration = 1000
            }
            animator.start()
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
            loginButton.setOnClickListener {
                if(binding.emailEditText.text.isNullOrEmpty() || binding.passwordEditText.text.isNullOrEmpty()){
                    Toast.makeText(this@LoginActivity, "Email atau password harus diisi", Toast.LENGTH_SHORT).show()
                }else{
                    showLoading(true)
                    processLogin()
                }
            }
            Signup.setOnClickListener{
                val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }
            signInButton.setOnClickListener {
                showLoading(true)
                signIn()
            }
        }
    }

    private fun processLogin(){
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                alert()
                showLoading(false)
            }
            .addOnFailureListener{ error->
                showLoading(false)
                alert2()
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun alert(){
        AlertDialog.Builder(this).apply {
            setTitle("Yeah!")
            setMessage("Login Berhasil")
            setPositiveButton("Lanjut") { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun alert2(){
        AlertDialog.Builder(this).apply {
            setMessage("Email dan Password salah")
            setPositiveButton("Ok") { _, _ ->
            }
            create()
            show()
        }
    }

    // Loading
    private fun showLoading(isLoading: Boolean) {
        binding.signInButton.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    companion object {
        private const val RC_SIGN_IN = 9001
    }
}