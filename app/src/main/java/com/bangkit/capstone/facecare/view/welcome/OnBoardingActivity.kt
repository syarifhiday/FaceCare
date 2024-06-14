package com.bangkit.capstone.facecare.view.welcome

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.databinding.ActivityOnBoardingBinding
import com.bangkit.capstone.facecare.view.login.LoginActivity
import com.bangkit.capstone.facecare.view.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var slidePagerAdapter: SlidePagerAdapter
    private lateinit var dots: Array<TextView>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkSession()
        supportActionBar?.hide()

        binding.backbtn.setOnClickListener {
            if (getitem(0) > 0) {
                binding.slideViewPager.setCurrentItem(getitem(-1), true)
            }
        }

        binding.nextbtn.setOnClickListener {
            if (getitem(0) < 2) {
                binding.slideViewPager.setCurrentItem(getitem(1), true)
            } else {
                val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        binding.skipButton.setOnClickListener {
            val intent = Intent(this@OnBoardingActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        slidePagerAdapter = SlidePagerAdapter(this)
        binding.slideViewPager.adapter = slidePagerAdapter

        setUpIndicator(0)
        binding.slideViewPager.addOnPageChangeListener(viewListener)
    }

    private fun checkSession(){
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }
    }

    private fun setUpIndicator(position: Int) {
        dots = Array(3) { TextView(this) }
        binding.indicatorLayout.removeAllViews()

        for (i in dots.indices) {
            dots[i].text = Html.fromHtml("&#8226")
            dots[i].textSize = 35f
            dots[i].setTextColor(resources.getColor(R.color.inactive, applicationContext.theme))
            binding.indicatorLayout.addView(dots[i])
        }

        dots[position].setTextColor(resources.getColor(R.color.active, applicationContext.theme))
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
            if(position >= 1){
                binding.backbtn.visibility =  View.VISIBLE
            }else{
                binding.backbtn.visibility = View.INVISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun getitem(i: Int): Int {
        return binding.slideViewPager.currentItem + i
    }
}
