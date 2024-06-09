package com.bangkit.capstone.facecare.view.welcome

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ViewGroup)
    }
}
