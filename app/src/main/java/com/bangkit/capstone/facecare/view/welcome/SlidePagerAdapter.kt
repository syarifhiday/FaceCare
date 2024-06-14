package com.bangkit.capstone.facecare.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bangkit.capstone.facecare.R
import com.bangkit.capstone.facecare.databinding.SliderLayoutBinding

class SlidePagerAdapter(private val context: Context) : PagerAdapter() {

    private val images = intArrayOf(
        R.drawable.image1,
        R.drawable.image2,
        R.drawable.image3,
    )

    private val headings = intArrayOf(
        R.string.heading_one,
        R.string.heading_two,
        R.string.heading_three,
    )

    private val descriptions = intArrayOf(
        R.string.desc_one,
        R.string.desc_two,
        R.string.desc_three,
    )

    override fun getCount(): Int {
        return headings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === (`object` as ViewGroup)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val binding = SliderLayoutBinding.inflate(inflater, container, false)

        binding.titleImage.setImageResource(images[position])
        binding.texttitle.setText(headings[position])
        binding.textdeccription.setText(descriptions[position])

        container.addView(binding.root)

        // Add animations
        startAnimations(binding.titleImage, binding.texttitle, binding.textdeccription)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ViewGroup)
    }

    private fun startAnimations(imageView: ImageView, textView: TextView, textView2: TextView) {
        imageView.translationX = 0f
        textView.alpha = 0f
        textView2.alpha = 0f


        // Slide animation for the image (left to right)
        val slideRight = ObjectAnimator.ofFloat(imageView, "translationX", -30f, 30f).apply {
            duration = 2500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }

        // Fade-in animation for the text
        val fadeInAnimator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f).apply {
            duration = 2000
        }

        val fadeInAnimator2 = ObjectAnimator.ofFloat(textView2, "alpha", 0f, 1f).apply {
            duration = 2000
        }

        // Combine animations
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(slideRight, fadeInAnimator, fadeInAnimator2)
        animatorSet.start()
    }
}
